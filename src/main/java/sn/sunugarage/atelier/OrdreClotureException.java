package sn.sunugarage.atelier;

import sn.sunugarage.RegleMetierException;

import java.util.UUID;

public class OrdreClotureException extends RegleMetierException {

    public OrdreClotureException(UUID ordreId) {
        super("L'ordre de reparation est cloture et ne peut plus etre modifie : " + ordreId);
    }
}
