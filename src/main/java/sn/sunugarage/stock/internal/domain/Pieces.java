package sn.sunugarage.stock.internal.domain;

import java.util.List;
import java.util.Optional;

public interface Pieces {

    void save(Piece piece);

    Optional<Piece> findById(String reference);

    List<Piece> findAll();
}
