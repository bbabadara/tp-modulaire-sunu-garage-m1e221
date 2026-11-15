package sn.sunugarage.accueil.internal.domain;

import java.util.Optional;
import java.util.UUID;

public interface Vehicules {

    void save(Vehicule vehicule);

    Optional<Vehicule> findById(UUID vehiculeId);

    Optional<Vehicule> findByImmatriculation(String immatriculation);
}
