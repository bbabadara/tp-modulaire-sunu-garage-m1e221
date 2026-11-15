package sn.sunugarage.accueil.internal.domain;

import java.util.Optional;
import java.util.UUID;

public interface Clients {

    void save(Client client);

    Optional<Client> findById(UUID clientId);
}
