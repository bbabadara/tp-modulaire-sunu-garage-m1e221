package sn.sunugarage.stock.internal.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import sn.sunugarage.stock.PieceAjoutee;
import sn.sunugarage.stock.PieceDto;
import sn.sunugarage.stock.PieceIntrouvableException;
import sn.sunugarage.stock.PieceSortie;
import sn.sunugarage.stock.PieceSortieDto;
import sn.sunugarage.stock.StockApi;
import sn.sunugarage.stock.StockSousSeuil;
import sn.sunugarage.stock.internal.domain.Piece;
import sn.sunugarage.stock.internal.domain.Pieces;

import java.util.List;

@Service
public class StockService implements StockApi {

    private final Pieces pieces;
    private final ApplicationEventPublisher evenements;

    public StockService(Pieces pieces, ApplicationEventPublisher evenements) {
        this.pieces = pieces;
        this.evenements = evenements;
    }

    @Override
    public void ajouterPiece(String reference, String libelle, int quantite, long prixUnitaire, int seuilAlerte) {
        exigerTexte(reference, "La reference de la piece est obligatoire");
        exigerTexte(libelle, "Le libelle de la piece est obligatoire");
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite a ajouter doit etre strictement positive");
        }

        Piece piece = pieces.findById(reference)
                .map(existante -> {
                    existante.ajouter(quantite);
                    existante.changerPrix(prixUnitaire);
                    return existante;
                })
                .orElseGet(() -> new Piece(reference, libelle, quantite, prixUnitaire, seuilAlerte));

        pieces.save(piece);
        evenements.publishEvent(new PieceAjoutee(reference, quantite, prixUnitaire));
        signalerSeuil(piece);
    }

    @Override
    public int disponible(String reference) {
        return trouver(reference).quantite();
    }

    @Override
    public PieceDto piece(String reference) {
        return versDto(trouver(reference));
    }

    @Override
    public PieceSortieDto sortirPiece(String reference, int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite a sortir doit etre strictement positive");
        }
        Piece piece = trouver(reference);
        long prixDuJour = piece.prixUnitaire();
        piece.sortir(quantite);
        pieces.save(piece);
        evenements.publishEvent(new PieceSortie(reference, quantite, prixDuJour));
        signalerSeuil(piece);
        return new PieceSortieDto(reference, quantite, prixDuJour);
    }

    @Override
    public List<PieceDto> pieces() {
        return pieces.findAll().stream().map(this::versDto).toList();
    }

    @Override
    public List<PieceDto> piecesSousSeuil() {
        return pieces.findAll().stream()
                .filter(Piece::sousSeuil)
                .map(this::versDto)
                .toList();
    }

    private Piece trouver(String reference) {
        return pieces.findById(reference)
                .orElseThrow(() -> new PieceIntrouvableException(reference));
    }

    private void signalerSeuil(Piece piece) {
        if (piece.sousSeuil()) {
            evenements.publishEvent(new StockSousSeuil(piece.reference(), piece.quantite(), piece.seuilAlerte()));
        }
    }

    private PieceDto versDto(Piece piece) {
        return new PieceDto(
                piece.reference(),
                piece.libelle(),
                piece.quantite(),
                piece.prixUnitaire(),
                piece.seuilAlerte());
    }

    private void exigerTexte(String valeur, String message) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
