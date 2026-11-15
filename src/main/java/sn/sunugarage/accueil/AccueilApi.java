package sn.sunugarage.accueil;

import java.util.UUID;

public interface AccueilApi {

    UUID enregistrerClient(String nom, String telephone);

    UUID enregistrerVehicule(String immatriculation, UUID clientId);

    boolean vehiculeExiste(UUID vehiculeId);

    ClientDto client(UUID clientId);

    VehiculeDto vehicule(UUID vehiculeId);

    void marquerSortable(UUID vehiculeId);
}
