package sn.sunugarage.facturation;

import java.util.UUID;

public record FacturePayee(UUID factureId, UUID ordreId, UUID vehiculeId, long total) {
}
