package sn.sunugarage.facturation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record FactureDto(
        UUID id,
        UUID ordreId,
        UUID vehiculeId,
        String statut,
        BigDecimal heuresMainOeuvre,
        List<LignePieceFacture> pieces,
        long total) {
}
