package sn.sunugarage.atelier.internal.infrastructure;

import org.springframework.stereotype.Repository;
import sn.sunugarage.atelier.internal.domain.OrdreReparation;
import sn.sunugarage.atelier.internal.domain.Ordres;
import sn.sunugarage.atelier.internal.domain.StatutOrdre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrdres implements Ordres {

    private final Map<UUID, OrdreReparation> parId = new ConcurrentHashMap<>();

    @Override
    public void save(OrdreReparation ordre) {
        parId.put(ordre.id(), ordre);
    }

    @Override
    public Optional<OrdreReparation> findById(UUID ordreId) {
        return Optional.ofNullable(parId.get(ordreId));
    }

    @Override
    public Optional<OrdreReparation> findOuvertByVehiculeId(UUID vehiculeId) {
        return parId.values().stream()
                .filter(ordre -> ordre.vehiculeId().equals(vehiculeId))
                .filter(ordre -> ordre.statut() == StatutOrdre.OUVERT)
                .findFirst();
    }

    @Override
    public List<OrdreReparation> findAll() {
        return List.copyOf(parId.values());
    }
}
