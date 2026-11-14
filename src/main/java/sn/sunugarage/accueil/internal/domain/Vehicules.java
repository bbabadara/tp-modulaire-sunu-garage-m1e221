package sn.sunugarage.accueil.internal.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Vehicules {

    void save(Vehicule vehicule);

    Optional<Vehicule> findById(UUID vehiculeId);

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    List<Vehicule> findAll();
}
