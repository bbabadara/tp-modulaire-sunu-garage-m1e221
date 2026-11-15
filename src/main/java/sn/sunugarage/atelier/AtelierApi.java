package sn.sunugarage.atelier;

import java.math.BigDecimal;
import java.util.UUID;

public interface AtelierApi {

    UUID ouvrirOrdre(UUID vehiculeId);

    void ajouterMainOeuvre(UUID ordreId, BigDecimal heures);

    void ajouterPiece(UUID ordreId, String reference, int quantite);

    void cloturer(UUID ordreId);

    OrdreDto ordre(UUID ordreId);

    boolean ordreOuvert(UUID vehiculeId);
}
