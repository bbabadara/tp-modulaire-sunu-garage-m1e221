package sn.sunugarage.stock.internal.infrastructure;

import org.springframework.stereotype.Repository;
import sn.sunugarage.stock.internal.domain.Piece;
import sn.sunugarage.stock.internal.domain.Pieces;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPieces implements Pieces {

    private final Map<String, Piece> parReference = new ConcurrentHashMap<>();

    @Override
    public void save(Piece piece) {
        parReference.put(piece.reference(), piece);
    }

    @Override
    public Optional<Piece> findById(String reference) {
        return Optional.ofNullable(parReference.get(reference));
    }

    @Override
    public List<Piece> findAll() {
        return List.copyOf(parReference.values());
    }
}
