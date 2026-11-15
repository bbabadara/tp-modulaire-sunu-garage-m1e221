package sn.sunugarage.atelier;

import sn.sunugarage.RessourceIntrouvableException;

import java.util.UUID;

public class VehiculeInconnuException extends RessourceIntrouvableException {

    public VehiculeInconnuException(UUID vehiculeId) {
        super("Le vehicule n'existe pas dans l'accueil : " + vehiculeId);
    }
}
