package sn.sunugarage.atelier;

import sn.sunugarage.RegleMetierException;

import java.util.UUID;

public class OrdreDejaOuvertException extends RegleMetierException {

    public OrdreDejaOuvertException(UUID vehiculeId) {
        super("Un ordre de reparation est deja ouvert pour le vehicule " + vehiculeId);
    }
}
