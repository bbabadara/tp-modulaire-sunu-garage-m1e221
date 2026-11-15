package sn.sunugarage.accueil;

import sn.sunugarage.RessourceIntrouvableException;

import java.util.UUID;

public class ClientIntrouvableException extends RessourceIntrouvableException {

    public ClientIntrouvableException(UUID clientId) {
        super("Client introuvable : " + clientId);
    }
}
