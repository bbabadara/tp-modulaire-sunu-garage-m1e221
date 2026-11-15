package sn.sunugarage.facturation.internal.infrastructure;

import org.springframework.stereotype.Repository;
import sn.sunugarage.facturation.internal.domain.Facture;
import sn.sunugarage.facturation.internal.domain.Factures;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryFactures implements Factures {

    private final Map<UUID, Facture> parId = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> parOrdre = new ConcurrentHashMap<>();

    @Override
    public void save(Facture facture) {
        parId.put(facture.id(), facture);
        parOrdre.put(facture.ordreId(), facture.id());
    }

    @Override
    public Optional<Facture> findById(UUID factureId) {
        return Optional.ofNullable(parId.get(factureId));
    }

    @Override
    public Optional<Facture> findByOrdreId(UUID ordreId) {
        return Optional.ofNullable(parOrdre.get(ordreId)).map(parId::get);
    }
}
