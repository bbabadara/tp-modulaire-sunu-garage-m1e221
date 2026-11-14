package sn.sunugarage.stock;

import java.util.List;

public interface StockApi {

    void ajouterPiece(String reference, String libelle, int quantite, long prixUnitaire, int seuilAlerte);

    int disponible(String reference);

    PieceDto piece(String reference);

    PieceSortieDto sortirPiece(String reference, int quantite);

    List<PieceDto> pieces();

    List<PieceDto> piecesSousSeuil();
}
