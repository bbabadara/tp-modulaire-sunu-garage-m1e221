package sn.sunugarage.stock;

import sn.sunugarage.RessourceIntrouvableException;

public class PieceIntrouvableException extends RessourceIntrouvableException {

    public PieceIntrouvableException(String reference) {
        super("Piece introuvable : " + reference);
    }
}
