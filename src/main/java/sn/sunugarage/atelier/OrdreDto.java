package sn.sunugarage.atelier;

import java.util.List;
import java.util.UUID;

public record OrdreDto(
        UUID id,
        UUID vehiculeId,
        String statut,
        List<LigneMainOeuvreDto> mainOeuvre,
        List<LignePieceDto> pieces) {
}
