package sn.sunugarage.facturation.internal.domain;

import sn.sunugarage.facturation.FactureDejaPayeeException;
import sn.sunugarage.facturation.LignePieceFacture;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public class Facture {

    public static final long TAUX_HORAIRE_XOF = 10_000L;

    private final UUID id;
    private final UUID ordreId;
    private final UUID vehiculeId;
    private final BigDecimal heuresMainOeuvre;
    private final List<LignePieceFacture> pieces;
    private StatutFacture statut;

    public Facture(
            UUID id,
            UUID ordreId,
            UUID vehiculeId,
            BigDecimal heuresMainOeuvre,
            List<LignePieceFacture> pieces) {
        if (heuresMainOeuvre == null || heuresMainOeuvre.signum() < 0) {
            throw new IllegalArgumentException("Les heures de main-d'oeuvre sont invalides");
        }
        this.id = id;
        this.ordreId = ordreId;
        this.vehiculeId = vehiculeId;
        this.heuresMainOeuvre = heuresMainOeuvre;
        this.pieces = List.copyOf(pieces);
        this.statut = StatutFacture.EMISE;
    }

    public void payer() {
        if (statut == StatutFacture.PAYEE) {
            throw new FactureDejaPayeeException(id);
        }
        this.statut = StatutFacture.PAYEE;
    }

    public long total() {
        long mainOeuvre = heuresMainOeuvre
                .multiply(BigDecimal.valueOf(TAUX_HORAIRE_XOF))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        long totalPieces = pieces.stream()
                .mapToLong(ligne -> Math.multiplyExact((long) ligne.quantite(), ligne.prixUnitaire()))
                .sum();

        return Math.addExact(mainOeuvre, totalPieces);
    }

    public UUID id() {
        return id;
    }

    public UUID ordreId() {
        return ordreId;
    }

    public UUID vehiculeId() {
        return vehiculeId;
    }

    public BigDecimal heuresMainOeuvre() {
        return heuresMainOeuvre;
    }

    public List<LignePieceFacture> pieces() {
        return pieces;
    }

    public StatutFacture statut() {
        return statut;
    }
}
