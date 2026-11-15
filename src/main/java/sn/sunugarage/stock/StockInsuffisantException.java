package sn.sunugarage.stock;

import sn.sunugarage.RegleMetierException;

public class StockInsuffisantException extends RegleMetierException {

    public StockInsuffisantException(String reference, int disponible, int demande) {
        super("Stock insuffisant pour " + reference
                + " : disponible = " + disponible + ", demande = " + demande);
    }
}
