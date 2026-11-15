package sn.sunugarage.accueil.internal.infrastructure;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sn.sunugarage.accueil.AccueilApi;
import sn.sunugarage.facturation.FacturePayee;

@Component
public class FacturePayeeListener {

    private final AccueilApi accueil;

    public FacturePayeeListener(AccueilApi accueil) {
        this.accueil = accueil;
    }

    @EventListener
    public void surPaiementDeLaFacture(FacturePayee evenement) {
        if (accueil.vehiculeExiste(evenement.vehiculeId())) {
            accueil.marquerSortable(evenement.vehiculeId());
        }
    }
}
