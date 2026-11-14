package sn.sunugarage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sn.sunugarage.accueil.AccueilApi;
import sn.sunugarage.atelier.AtelierApi;
import sn.sunugarage.facturation.FacturationApi;
import sn.sunugarage.stock.PieceDto;
import sn.sunugarage.stock.StockApi;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "sunugarage.donnees-demarrage=true")
class DonneesDemarrageTest {

    @Autowired
    private AccueilApi accueil;

    @Autowired
    private StockApi stock;

    @Autowired
    private AtelierApi atelier;

    @Autowired
    private FacturationApi facturation;

    @Test
    void chargeUnAtelierPretADemonstrer() {
        assertThat(accueil.clients()).extracting(client -> client.nom())
                .contains("Awa Sow", "Moussa Ndiaye", "Fatou Ba", "Ibrahima Fall",
                        "Aïssatou Diop", "Cheikh Gueye", "Mariama Sy");
        assertThat(accueil.vehicules()).extracting(vehicule -> vehicule.immatriculation())
                .contains("DK-123-AB", "DK-456-CD", "DK-789-EF", "DK-321-GH",
                        "DK-555-IJ", "DK-666-KL", "DK-777-MN");
        assertThat(stock.pieces()).extracting(PieceDto::reference)
                .contains("PLAQUETTES-FREIN", "FILTRE-HUILE", "AMPOULE-H7",
                        "COURROIE-DISTRIBUTION", "PNEU-175-65");
        assertThat(stock.piecesSousSeuil()).extracting(PieceDto::reference).contains("AMPOULE-H7");
        assertThat(atelier.ordres()).hasSize(6);
        assertThat(facturation.factures()).hasSize(4);
        assertThat(accueil.vehicules()).anySatisfy(vehicule -> {
            if ("DK-123-AB".equals(vehicule.immatriculation())) {
                assertThat(vehicule.statut()).isEqualTo("PEUT_SORTIR");
            }
        });
    }
}
