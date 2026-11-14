package sn.sunugarage.accueil.internal.infrastructure;

import org.springframework.stereotype.Repository;
import sn.sunugarage.accueil.internal.domain.Vehicule;
import sn.sunugarage.accueil.internal.domain.Vehicules;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryVehicules implements Vehicules {

    private final Map<UUID, Vehicule> parId = new ConcurrentHashMap<>();
    private final Map<String, UUID> parImmatriculation = new ConcurrentHashMap<>();

    @Override
    public void save(Vehicule vehicule) {
        parId.put(vehicule.id(), vehicule);
        parImmatriculation.put(vehicule.immatriculation(), vehicule.id());
    }

    @Override
    public Optional<Vehicule> findById(UUID vehiculeId) {
        return Optional.ofNullable(parId.get(vehiculeId));
    }

    @Override
    public Optional<Vehicule> findByImmatriculation(String immatriculation) {
        return Optional.ofNullable(parImmatriculation.get(immatriculation)).map(parId::get);
    }

    @Override
    public List<Vehicule> findAll() {
        return List.copyOf(parId.values());
    }
}
