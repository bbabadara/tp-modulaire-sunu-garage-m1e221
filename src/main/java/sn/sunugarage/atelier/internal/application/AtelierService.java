package sn.sunugarage.atelier.internal.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import sn.sunugarage.accueil.AccueilApi;
import sn.sunugarage.atelier.AtelierApi;
import sn.sunugarage.atelier.LigneMainOeuvreDto;
import sn.sunugarage.atelier.LignePieceDto;
import sn.sunugarage.atelier.OrdreCloture;
import sn.sunugarage.atelier.OrdreDejaOuvertException;
import sn.sunugarage.atelier.OrdreDto;
import sn.sunugarage.atelier.OrdreIntrouvableException;
import sn.sunugarage.atelier.OrdreOuvert;
import sn.sunugarage.atelier.VehiculeInconnuException;
import sn.sunugarage.atelier.internal.domain.LignePiece;
import sn.sunugarage.atelier.internal.domain.OrdreReparation;
import sn.sunugarage.atelier.internal.domain.Ordres;
import sn.sunugarage.facturation.FacturationApi;
import sn.sunugarage.facturation.FactureRequest;
import sn.sunugarage.facturation.LignePieceFacture;
import sn.sunugarage.stock.PieceSortieDto;
import sn.sunugarage.stock.StockApi;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AtelierService implements AtelierApi {

    private final Ordres ordres;
    private final AccueilApi accueil;
    private final StockApi stock;
    private final FacturationApi facturation;
    private final ApplicationEventPublisher evenements;

    public AtelierService(
            Ordres ordres,
            AccueilApi accueil,
            StockApi stock,
            FacturationApi facturation,
            ApplicationEventPublisher evenements) {
        this.ordres = ordres;
        this.accueil = accueil;
        this.stock = stock;
        this.facturation = facturation;
        this.evenements = evenements;
    }

    @Override
    public UUID ouvrirOrdre(UUID vehiculeId) {
        if (!accueil.vehiculeExiste(vehiculeId)) {
            throw new VehiculeInconnuException(vehiculeId);
        }
        if (ordres.findOuvertByVehiculeId(vehiculeId).isPresent()) {
            throw new OrdreDejaOuvertException(vehiculeId);
        }

        OrdreReparation ordre = new OrdreReparation(UUID.randomUUID(), vehiculeId);
        ordres.save(ordre);
        evenements.publishEvent(new OrdreOuvert(ordre.id(), vehiculeId));
        return ordre.id();
    }

    @Override
    public void ajouterMainOeuvre(UUID ordreId, BigDecimal heures) {
        OrdreReparation ordre = trouver(ordreId);
        ordre.ajouterMainOeuvre(heures);
        ordres.save(ordre);
    }

    @Override
    public void ajouterPiece(UUID ordreId, String reference, int quantite) {
        OrdreReparation ordre = trouver(ordreId);
        ordre.verifierOuvert();

        PieceSortieDto sortie = stock.sortirPiece(reference, quantite);

        ordre.ajouterPiece(sortie.reference(), sortie.quantite(), sortie.prixUnitaire());
        ordres.save(ordre);
    }

    @Override
    public void cloturer(UUID ordreId) {
        OrdreReparation ordre = trouver(ordreId);
        ordre.verifierOuvert();

        FactureRequest requete = new FactureRequest(
                ordre.id(),
                ordre.vehiculeId(),
                ordre.totalHeures(),
                ordre.pieces().stream()
                        .map(this::versLigneFacture)
                        .toList());

        facturation.emettreFacture(requete);

        ordre.cloturer();
        ordres.save(ordre);
        evenements.publishEvent(new OrdreCloture(ordre.id(), ordre.vehiculeId()));
    }

    @Override
    public OrdreDto ordre(UUID ordreId) {
        return versDto(trouver(ordreId));
    }

    @Override
    public boolean ordreOuvert(UUID vehiculeId) {
        return ordres.findOuvertByVehiculeId(vehiculeId).isPresent();
    }

    private LignePieceFacture versLigneFacture(LignePiece ligne) {
        return new LignePieceFacture(ligne.reference(), ligne.quantite(), ligne.prixUnitaire());
    }

    private OrdreReparation trouver(UUID ordreId) {
        return ordres.findById(ordreId)
                .orElseThrow(() -> new OrdreIntrouvableException(ordreId));
    }

    String violationVolontaireDeFrontiere() {
        return sn.sunugarage.stock.internal.domain.Piece.class.getName();
    }

    private OrdreDto versDto(OrdreReparation ordre) {
        return new OrdreDto(
                ordre.id(),
                ordre.vehiculeId(),
                ordre.statut().name(),
                ordre.mainOeuvre().stream()
                        .map(ligne -> new LigneMainOeuvreDto(ligne.heures()))
                        .toList(),
                ordre.pieces().stream()
                        .map(ligne -> new LignePieceDto(ligne.reference(), ligne.quantite(), ligne.prixUnitaire()))
                        .toList());
    }
}
