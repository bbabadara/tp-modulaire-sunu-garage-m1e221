package sn.sunugarage.accueil;

import java.util.UUID;

public record VehiculeEnregistre(UUID vehiculeId, UUID clientId, String immatriculation) {
}
