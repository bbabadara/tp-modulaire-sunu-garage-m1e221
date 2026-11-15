package sn.sunugarage.facturation;

import sn.sunugarage.RegleMetierException;

import java.util.UUID;

public class FactureDejaPayeeException extends RegleMetierException {

    public FactureDejaPayeeException(UUID factureId) {
        super("La facture est deja payee : " + factureId);
    }
}
