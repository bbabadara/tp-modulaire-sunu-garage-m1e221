package sn.sunugarage.accueil;

import java.util.UUID;

public record VehiculeDto(UUID id, String immatriculation, UUID clientId, String statut) {
}
