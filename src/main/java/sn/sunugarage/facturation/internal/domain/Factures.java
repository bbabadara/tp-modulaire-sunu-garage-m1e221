package sn.sunugarage.facturation.internal.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Factures {

    void save(Facture facture);

    Optional<Facture> findById(UUID factureId);

    Optional<Facture> findByOrdreId(UUID ordreId);

    List<Facture> findAll();
}
