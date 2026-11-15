package sn.sunugarage.accueil.internal.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import sn.sunugarage.accueil.AccueilApi;
import sn.sunugarage.accueil.ClientDto;
import sn.sunugarage.accueil.VehiculeDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/accueil")
@Tag(name = "Accueil", description = "Clients, véhicules et statut de sortie")
public class AccueilController {

    private final AccueilApi accueil;

    public AccueilController(AccueilApi accueil) {
        this.accueil = accueil;
    }

    @PostMapping("/clients")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto creerClient(@RequestBody CreerClientRequest requete) {
        UUID clientId = accueil.enregistrerClient(requete.nom(), requete.telephone());
        return accueil.client(clientId);
    }

    @GetMapping("/clients/{id}")
    public ClientDto client(@PathVariable UUID id) {
        return accueil.client(id);
    }

    @PostMapping("/vehicules")
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeDto creerVehicule(@RequestBody CreerVehiculeRequest requete) {
        UUID vehiculeId = accueil.enregistrerVehicule(requete.immatriculation(), requete.clientId());
        return accueil.vehicule(vehiculeId);
    }

    @GetMapping("/vehicules/{id}")
    public VehiculeDto vehicule(@PathVariable UUID id) {
        return accueil.vehicule(id);
    }

    public record CreerClientRequest(String nom, String telephone) {
    }

    public record CreerVehiculeRequest(String immatriculation, UUID clientId) {
    }
}
