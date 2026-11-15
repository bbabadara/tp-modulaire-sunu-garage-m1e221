package sn.sunugarage.facturation;

import java.util.UUID;

public record FactureEmise(UUID factureId, UUID ordreId, long total) {
}
