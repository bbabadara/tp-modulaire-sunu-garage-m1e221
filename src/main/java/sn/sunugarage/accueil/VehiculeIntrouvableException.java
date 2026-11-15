package sn.sunugarage.accueil;

import sn.sunugarage.RessourceIntrouvableException;

import java.util.UUID;

public class VehiculeIntrouvableException extends RessourceIntrouvableException {

    public VehiculeIntrouvableException(UUID vehiculeId) {
        super("Vehicule introuvable : " + vehiculeId);
    }
}
