package sn.sunugarage.facturation.internal.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import sn.sunugarage.RegleMetierException;
import sn.sunugarage.facturation.FacturationApi;
import sn.sunugarage.facturation.FactureDto;
import sn.sunugarage.facturation.FactureEmise;
import sn.sunugarage.facturation.FactureIntrouvableException;
import sn.sunugarage.facturation.FacturePayee;
import sn.sunugarage.facturation.FactureRequest;
import sn.sunugarage.facturation.internal.domain.Facture;
import sn.sunugarage.facturation.internal.domain.Factures;

import java.util.List;
import java.util.UUID;

@Service
public class FacturationService implements FacturationApi {

    private final Factures factures;
    private final ApplicationEventPublisher evenements;

    public FacturationService(Factures factures, ApplicationEventPublisher evenements) {
        this.factures = factures;
        this.evenements = evenements;
    }

    @Override
    public UUID emettreFacture(FactureRequest requete) {
        if (requete == null) {
            throw new IllegalArgumentException("La demande de facture est obligatoire");
        }
        if (factures.findByOrdreId(requete.ordreId()).isPresent()) {
            throw new RegleMetierException("Une facture existe deja pour l'ordre " + requete.ordreId());
        }

        Facture facture = new Facture(
                UUID.randomUUID(),
                requete.ordreId(),
                requete.vehiculeId(),
                requete.heuresMainOeuvre(),
                requete.pieces());

        factures.save(facture);
        evenements.publishEvent(new FactureEmise(facture.id(), facture.ordreId(), facture.total()));
        return facture.id();
    }

    @Override
    public void payer(UUID factureId) {
        Facture facture = trouver(factureId);
        facture.payer();
        factures.save(facture);
        evenements.publishEvent(new FacturePayee(
                facture.id(),
                facture.ordreId(),
                facture.vehiculeId(),
                facture.total()));
    }

    @Override
    public FactureDto facture(UUID factureId) {
        return versDto(trouver(factureId));
    }

    @Override
    public FactureDto factureDe(UUID ordreId) {
        return factures.findByOrdreId(ordreId)
                .map(this::versDto)
                .orElseThrow(() -> new FactureIntrouvableException("Aucune facture pour l'ordre " + ordreId));
    }

    @Override
    public List<FactureDto> factures() {
        return factures.findAll().stream().map(this::versDto).toList();
    }

    private Facture trouver(UUID factureId) {
        return factures.findById(factureId)
                .orElseThrow(() -> new FactureIntrouvableException(factureId));
    }

    private FactureDto versDto(Facture facture) {
        return new FactureDto(
                facture.id(),
                facture.ordreId(),
                facture.vehiculeId(),
                facture.statut().name(),
                facture.heuresMainOeuvre(),
                facture.pieces(),
                facture.total());
    }
}
