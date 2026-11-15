package sn.sunugarage.facturation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record FactureRequest(
        UUID ordreId,
        UUID vehiculeId,
        BigDecimal heuresMainOeuvre,
        List<LignePieceFacture> pieces) {

    public FactureRequest {
        pieces = pieces == null ? List.of() : List.copyOf(pieces);
    }
}
