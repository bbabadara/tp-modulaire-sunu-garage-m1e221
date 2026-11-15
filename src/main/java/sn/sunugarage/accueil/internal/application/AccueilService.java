package sn.sunugarage.accueil.internal.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import sn.sunugarage.accueil.AccueilApi;
import sn.sunugarage.accueil.ClientDto;
import sn.sunugarage.accueil.ClientEnregistre;
import sn.sunugarage.accueil.ClientIntrouvableException;
import sn.sunugarage.accueil.VehiculeDto;
import sn.sunugarage.accueil.VehiculeEnregistre;
import sn.sunugarage.accueil.VehiculeIntrouvableException;
import sn.sunugarage.accueil.VehiculeSortable;
import sn.sunugarage.accueil.internal.domain.Client;
import sn.sunugarage.accueil.internal.domain.Clients;
import sn.sunugarage.accueil.internal.domain.Vehicule;
import sn.sunugarage.accueil.internal.domain.Vehicules;

import java.util.UUID;

@Service
public class AccueilService implements AccueilApi {

    private final Clients clients;
    private final Vehicules vehicules;
    private final ApplicationEventPublisher evenements;

    public AccueilService(Clients clients, Vehicules vehicules, ApplicationEventPublisher evenements) {
        this.clients = clients;
        this.vehicules = vehicules;
        this.evenements = evenements;
    }

    @Override
    public UUID enregistrerClient(String nom, String telephone) {
        exigerTexte(nom, "Le nom du client est obligatoire");
        exigerTexte(telephone, "Le telephone du client est obligatoire");

        Client client = new Client(UUID.randomUUID(), nom, telephone);
        clients.save(client);
        evenements.publishEvent(new ClientEnregistre(client.id(), client.nom()));
        return client.id();
    }

    @Override
    public UUID enregistrerVehicule(String immatriculation, UUID clientId) {
        exigerTexte(immatriculation, "L'immatriculation est obligatoire");
        clients.findById(clientId).orElseThrow(() -> new ClientIntrouvableException(clientId));

        if (vehicules.findByImmatriculation(immatriculation).isPresent()) {
            throw new IllegalArgumentException("Un vehicule avec cette immatriculation existe deja : " + immatriculation);
        }

        Vehicule vehicule = new Vehicule(UUID.randomUUID(), immatriculation, clientId);
        vehicules.save(vehicule);
        evenements.publishEvent(new VehiculeEnregistre(vehicule.id(), clientId, vehicule.immatriculation()));
        return vehicule.id();
    }

    @Override
    public boolean vehiculeExiste(UUID vehiculeId) {
        return vehicules.findById(vehiculeId).isPresent();
    }

    @Override
    public ClientDto client(UUID clientId) {
        return clients.findById(clientId)
                .map(this::versDto)
                .orElseThrow(() -> new ClientIntrouvableException(clientId));
    }

    @Override
    public VehiculeDto vehicule(UUID vehiculeId) {
        return vehicules.findById(vehiculeId)
                .map(this::versDto)
                .orElseThrow(() -> new VehiculeIntrouvableException(vehiculeId));
    }

    @Override
    public void marquerSortable(UUID vehiculeId) {
        Vehicule vehicule = vehicules.findById(vehiculeId)
                .orElseThrow(() -> new VehiculeIntrouvableException(vehiculeId));
        vehicule.marquerSortable();
        vehicules.save(vehicule);
        evenements.publishEvent(new VehiculeSortable(vehicule.id(), vehicule.immatriculation()));
    }

    private ClientDto versDto(Client client) {
        return new ClientDto(client.id(), client.nom(), client.telephone());
    }

    private VehiculeDto versDto(Vehicule vehicule) {
        return new VehiculeDto(
                vehicule.id(),
                vehicule.immatriculation(),
                vehicule.clientId(),
                vehicule.statut().name());
    }

    private void exigerTexte(String valeur, String message) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
