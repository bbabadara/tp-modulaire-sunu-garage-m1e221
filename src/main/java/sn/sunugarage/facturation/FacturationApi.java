package sn.sunugarage.facturation;

import java.util.UUID;

public interface FacturationApi {

    UUID emettreFacture(FactureRequest requete);

    void payer(UUID factureId);

    FactureDto facture(UUID factureId);

    FactureDto factureDe(UUID ordreId);
}
