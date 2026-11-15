package sn.sunugarage.facturation;

import sn.sunugarage.RessourceIntrouvableException;

import java.util.UUID;

public class FactureIntrouvableException extends RessourceIntrouvableException {

    public FactureIntrouvableException(UUID factureId) {
        super("Facture introuvable : " + factureId);
    }

    public FactureIntrouvableException(String message) {
        super(message);
    }
}
