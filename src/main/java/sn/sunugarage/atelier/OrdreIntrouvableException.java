package sn.sunugarage.atelier;

import sn.sunugarage.RessourceIntrouvableException;

import java.util.UUID;

public class OrdreIntrouvableException extends RessourceIntrouvableException {

    public OrdreIntrouvableException(UUID ordreId) {
        super("Ordre de reparation introuvable : " + ordreId);
    }
}
