package sn.sunugarage.atelier.internal.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sn.sunugarage.accueil.AccueilApi;
import sn.sunugarage.atelier.AtelierApi;
import sn.sunugarage.facturation.FacturationApi;
import sn.sunugarage.facturation.FactureDto;
import sn.sunugarage.stock.StockApi;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "sunugarage.donnees-demarrage", havingValue = "true")
public class DonneesDemarrage implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DonneesDemarrage.class);

    private final AccueilApi accueil;
    private final StockApi stock;
    private final AtelierApi atelier;
    private final FacturationApi facturation;

    public DonneesDemarrage(
            AccueilApi accueil,
            StockApi stock,
            AtelierApi atelier,
            FacturationApi facturation) {
        this.accueil = accueil;
        this.stock = stock;
        this.atelier = atelier;
        this.facturation = facturation;
    }

    @Override
    public void run(ApplicationArguments args) {
        stock.ajouterPiece("PLAQUETTES-FREIN", "Plaquettes de frein", 10, 15_000, 3);
        stock.ajouterPiece("FILTRE-HUILE", "Filtre à huile", 12, 4_500, 4);
        stock.ajouterPiece("HUILE-5W30", "Huile moteur 5W30 5L", 8, 8_500, 5);
        stock.ajouterPiece("AMPOULE-H7", "Ampoule H7", 2, 2_500, 4);
        stock.ajouterPiece("BATTERIE-12V", "Batterie 12V 70Ah", 4, 65_000, 1);
        stock.ajouterPiece("PLAQUETTES-ARRIERE", "Plaquettes de frein arrière", 6, 14_000, 2);
        stock.ajouterPiece("PASTILLES-FREIN", "Pastilles de frein", 4, 3_500, 2);
        stock.ajouterPiece("COURROIE-DISTRIBUTION", "Courroie de distribution", 3, 28_000, 1);
        stock.ajouterPiece("AMORTISSEUR-AV", "Amortisseur avant", 2, 42_000, 1);
        stock.ajouterPiece("LIQUIDE-FREIN", "Liquide de frein DOT4", 9, 6_500, 3);
        stock.ajouterPiece("PNEU-175-65", "Pneu 175/65 R15", 4, 55_000, 2);

        UUID awa = accueil.enregistrerClient("Awa Sow", "771234567");
        UUID moussa = accueil.enregistrerClient("Moussa Ndiaye", "778001122");
        UUID fatou = accueil.enregistrerClient("Fatou Ba", "761112233");
        UUID ibrahima = accueil.enregistrerClient("Ibrahima Fall", "770998877");

        UUID dk123 = accueil.enregistrerVehicule("DK-123-AB", awa);
        UUID dk456 = accueil.enregistrerVehicule("DK-456-CD", moussa);
        UUID dk789 = accueil.enregistrerVehicule("DK-789-EF", fatou);
        UUID dk321 = accueil.enregistrerVehicule("DK-321-GH", ibrahima);

        UUID aissatou = accueil.enregistrerClient("Aïssatou Diop", "770112233");
        UUID cheikh = accueil.enregistrerClient("Cheikh Gueye", "781234567");
        UUID mariama = accueil.enregistrerClient("Mariama Sy", "769988776");

        UUID dk555 = accueil.enregistrerVehicule("DK-555-IJ", aissatou);
        UUID dk666 = accueil.enregistrerVehicule("DK-666-KL", cheikh);
        UUID dk777 = accueil.enregistrerVehicule("DK-777-MN", mariama);

        UUID ordreAwa = atelier.ouvrirOrdre(dk123);
        atelier.ajouterMainOeuvre(ordreAwa, new BigDecimal("2"));
        atelier.ajouterPiece(ordreAwa, "PLAQUETTES-FREIN", 2);
        atelier.cloturer(ordreAwa);
        FactureDto factureAwa = facturation.factureDe(ordreAwa);
        facturation.payer(factureAwa.id());

        UUID ordreMoussa = atelier.ouvrirOrdre(dk456);
        atelier.ajouterMainOeuvre(ordreMoussa, new BigDecimal("1.5"));
        atelier.ajouterPiece(ordreMoussa, "FILTRE-HUILE", 1);
        atelier.ajouterPiece(ordreMoussa, "HUILE-5W30", 1);

        UUID ordreFatou = atelier.ouvrirOrdre(dk789);
        atelier.ajouterMainOeuvre(ordreFatou, new BigDecimal("3"));
        atelier.ajouterPiece(ordreFatou, "BATTERIE-12V", 1);
        atelier.cloturer(ordreFatou);
        FactureDto factureFatou = facturation.factureDe(ordreFatou);

        UUID ordreAissatou = atelier.ouvrirOrdre(dk555);
        atelier.ajouterMainOeuvre(ordreAissatou, new BigDecimal("1.5"));
        atelier.ajouterPiece(ordreAissatou, "PLAQUETTES-ARRIERE", 2);
        atelier.ajouterPiece(ordreAissatou, "PASTILLES-FREIN", 1);

        UUID ordreCheikh = atelier.ouvrirOrdre(dk666);
        atelier.ajouterMainOeuvre(ordreCheikh, new BigDecimal("4"));
        atelier.ajouterPiece(ordreCheikh, "COURROIE-DISTRIBUTION", 1);
        atelier.cloturer(ordreCheikh);
        FactureDto factureCheikh = facturation.factureDe(ordreCheikh);
        facturation.payer(factureCheikh.id());

        UUID ordreMariama = atelier.ouvrirOrdre(dk777);
        atelier.ajouterMainOeuvre(ordreMariama, new BigDecimal("2"));
        atelier.ajouterPiece(ordreMariama, "PNEU-175-65", 2);
        atelier.cloturer(ordreMariama);
        FactureDto factureMariama = facturation.factureDe(ordreMariama);

        log.info("""
                Données de démonstration chargées :
                  Awa Sow       {}  DK-123-AB {}  ordre clôturé payé {}  facture {}  PEUT_SORTIR
                  Moussa        {}  DK-456-CD {}  ordre ouvert {}
                  Fatou Ba      {}  DK-789-EF {}  ordre clôturé {}  facture impayée {}
                  Ibrahima      {}  DK-321-GH (pas d'ordre)
                  Aïssatou      {}  DK-555-IJ {}  ordre ouvert en cours {}
                  Cheikh        {}  DK-666-KL {}  ordre clôturé payé {}  facture {}  PEUT_SORTIR
                  Mariama Sy    {}  DK-777-MN {}  ordre clôturé {}  facture impayée {}
                  Alertes stock : AMPOULE-H7
                """,
                awa, dk123, ordreAwa, factureAwa.id(),
                moussa, dk456, ordreMoussa,
                fatou, dk789, ordreFatou, factureFatou.id(),
                ibrahima, dk321,
                aissatou, dk555, ordreAissatou,
                cheikh, dk666, ordreCheikh, factureCheikh.id(),
                mariama, dk777, ordreMariama, factureMariama.id());
    }
}
