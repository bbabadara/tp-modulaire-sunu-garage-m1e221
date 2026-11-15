package sn.sunugarage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import sn.sunugarage.accueil.AccueilApi;
import sn.sunugarage.accueil.VehiculeSortable;
import sn.sunugarage.atelier.AtelierApi;
import sn.sunugarage.atelier.OrdreClotureException;
import sn.sunugarage.facturation.FacturationApi;
import sn.sunugarage.facturation.FactureDto;
import sn.sunugarage.facturation.FactureEmise;
import sn.sunugarage.facturation.FacturePayee;
import sn.sunugarage.stock.StockApi;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@RecordApplicationEvents
class ScenarioE2ETest {

    @Autowired
    private AccueilApi accueil;

    @Autowired
    private StockApi stock;

    @Autowired
    private AtelierApi atelier;

    @Autowired
    private FacturationApi facturation;

    @Autowired
    private ApplicationEvents evenements;

    @Test
    void derouleLeScenarioSunuGarageDeBoutEnBout() {
        // 1. Enregistrer un client et son vehicule
        UUID clientId = accueil.enregistrerClient("Awa Sow", "771234567");
        UUID vehiculeId = accueil.enregistrerVehicule("DK-123-AB", clientId);
        assertThat(accueil.vehicule(vehiculeId).statut()).isEqualTo("EN_ATELIER");

        // 2. Mettre 4 plaquettes de frein en stock a 15 000 XOF
        stock.ajouterPiece("PLAQUETTES-FREIN", "Plaquettes de frein", 4, 15_000, 2);
        assertThat(stock.disponible("PLAQUETTES-FREIN")).isEqualTo(4);

        // 3. Ouvrir un ordre de reparation
        UUID ordreId = atelier.ouvrirOrdre(vehiculeId);
        assertThat(atelier.ordreOuvert(vehiculeId)).isTrue();

        // 4. Ajouter 2 heures et 2 plaquettes : le stock passe a 2
        atelier.ajouterMainOeuvre(ordreId, new BigDecimal("2"));
        atelier.ajouterPiece(ordreId, "PLAQUETTES-FREIN", 2);
        assertThat(stock.disponible("PLAQUETTES-FREIN")).isEqualTo(2);
        assertThat(atelier.ordre(ordreId).pieces())
                .singleElement()
                .satisfies(ligne -> assertThat(ligne.prixUnitaire()).isEqualTo(15_000));

        // 5. Cloturer : une facture de 50 000 XOF existe
        atelier.cloturer(ordreId);
        FactureDto facture = facturation.factureDe(ordreId);
        assertThat(facture.ordreId()).isEqualTo(ordreId);
        assertThat(facture.total()).isEqualTo(50_000);
        assertThat(evenements.stream(FactureEmise.class)).hasSize(1);

        // 6. Ajouter une piece sur un ordre cloture est refuse
        assertThatThrownBy(() -> atelier.ajouterPiece(ordreId, "PLAQUETTES-FREIN", 1))
                .isInstanceOf(OrdreClotureException.class);
        assertThat(stock.disponible("PLAQUETTES-FREIN")).isEqualTo(2);

        // 7. Payer : le vehicule est marque « peut sortir »
        facturation.payer(facture.id());
        assertThat(facturation.facture(facture.id()).statut()).isEqualTo("PAYEE");
        assertThat(accueil.vehicule(vehiculeId).statut()).isEqualTo("PEUT_SORTIR");
        assertThat(evenements.stream(FacturePayee.class)).hasSize(1);
        assertThat(evenements.stream(VehiculeSortable.class)).hasSize(1);
    }
}
