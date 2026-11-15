package sn.sunugarage.atelier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sn.sunugarage.accueil.AccueilApi;
import sn.sunugarage.facturation.FacturationApi;
import sn.sunugarage.facturation.FactureDto;
import sn.sunugarage.stock.StockApi;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AtelierModuleTest {

    @Autowired
    private AccueilApi accueil;

    @Autowired
    private StockApi stock;

    @Autowired
    private AtelierApi atelier;

    @Autowired
    private FacturationApi facturation;

    @Test
    void ouvreUnSeulOrdreParVehiculeEtProduitLaFactureALaCloture() {
        UUID clientId = accueil.enregistrerClient("Moussa Ndiaye", "770000002");
        UUID vehiculeId = accueil.enregistrerVehicule("DK-ATELIER-01", clientId);
        stock.ajouterPiece("PLAQUETTE-ATELIER-TEST", "Plaquettes de frein", 4, 15_000, 2);

        UUID ordreId = atelier.ouvrirOrdre(vehiculeId);
        assertThat(atelier.ordreOuvert(vehiculeId)).isTrue();
        assertThatThrownBy(() -> atelier.ouvrirOrdre(vehiculeId))
                .isInstanceOf(OrdreDejaOuvertException.class);

        atelier.ajouterMainOeuvre(ordreId, new BigDecimal("2"));
        atelier.ajouterPiece(ordreId, "PLAQUETTE-ATELIER-TEST", 2);
        assertThat(stock.disponible("PLAQUETTE-ATELIER-TEST")).isEqualTo(2);

        atelier.cloturer(ordreId);

        FactureDto facture = facturation.factureDe(ordreId);
        assertThat(facture.total()).isEqualTo(50_000);
        assertThat(atelier.ordre(ordreId).statut()).isEqualTo("CLOTURE");
        assertThat(atelier.ordreOuvert(vehiculeId)).isFalse();

        assertThatThrownBy(() -> atelier.ajouterPiece(ordreId, "PLAQUETTE-ATELIER-TEST", 1))
                .isInstanceOf(OrdreClotureException.class);
        assertThat(stock.disponible("PLAQUETTE-ATELIER-TEST")).isEqualTo(2);

        UUID autreVehicule = accueil.enregistrerVehicule("DK-ATELIER-02", clientId);
        assertThatThrownBy(() -> atelier.ouvrirOrdre(UUID.randomUUID()))
                .isInstanceOf(VehiculeInconnuException.class);
        assertThat(atelier.ouvrirOrdre(autreVehicule)).isNotNull();
    }
}
