package sn.sunugarage.accueil.internal.infrastructure;

import org.springframework.stereotype.Repository;
import sn.sunugarage.accueil.internal.domain.Client;
import sn.sunugarage.accueil.internal.domain.Clients;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryClients implements Clients {

    private final Map<UUID, Client> parId = new ConcurrentHashMap<>();

    @Override
    public void save(Client client) {
        parId.put(client.id(), client);
    }

    @Override
    public Optional<Client> findById(UUID clientId) {
        return Optional.ofNullable(parId.get(clientId));
    }
}
