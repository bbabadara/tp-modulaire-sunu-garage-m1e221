package sn.sunugarage.atelier.internal.domain;

import sn.sunugarage.atelier.OrdreClotureException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrdreReparation {

    private final UUID id;
    private final UUID vehiculeId;
    private final List<LigneMainOeuvre> mainOeuvre = new ArrayList<>();
    private final List<LignePiece> pieces = new ArrayList<>();
    private StatutOrdre statut;

    public OrdreReparation(UUID id, UUID vehiculeId) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.statut = StatutOrdre.OUVERT;
    }

    public void ajouterMainOeuvre(BigDecimal heures) {
        verifierOuvert();
        if (heures == null || heures.signum() <= 0) {
            throw new IllegalArgumentException("Les heures de main-d'oeuvre doivent etre strictement positives");
        }
        mainOeuvre.add(new LigneMainOeuvre(heures));
    }

    public void ajouterPiece(String reference, int quantite, long prixUnitaire) {
        verifierOuvert();
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("La reference de la piece est obligatoire");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite de piece doit etre strictement positive");
        }
        pieces.add(new LignePiece(reference, quantite, prixUnitaire));
    }

    public void cloturer() {
        verifierOuvert();
        this.statut = StatutOrdre.CLOTURE;
    }

    public void verifierOuvert() {
        if (statut == StatutOrdre.CLOTURE) {
            throw new OrdreClotureException(id);
        }
    }

    public BigDecimal totalHeures() {
        return mainOeuvre.stream()
                .map(LigneMainOeuvre::heures)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID id() {
        return id;
    }

    public UUID vehiculeId() {
        return vehiculeId;
    }

    public StatutOrdre statut() {
        return statut;
    }

    public List<LigneMainOeuvre> mainOeuvre() {
        return List.copyOf(mainOeuvre);
    }

    public List<LignePiece> pieces() {
        return List.copyOf(pieces);
    }
}
