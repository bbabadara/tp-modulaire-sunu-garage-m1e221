package sn.sunugarage.atelier.internal.domain;

import java.util.Optional;
import java.util.UUID;

public interface Ordres {

    void save(OrdreReparation ordre);

    Optional<OrdreReparation> findById(UUID ordreId);

    Optional<OrdreReparation> findOuvertByVehiculeId(UUID vehiculeId);
}
