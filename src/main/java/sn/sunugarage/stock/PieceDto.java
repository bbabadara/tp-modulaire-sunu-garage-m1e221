package sn.sunugarage.stock;

public record PieceDto(
        String reference,
        String libelle,
        int quantite,
        long prixUnitaire,
        int seuilAlerte) {
}
