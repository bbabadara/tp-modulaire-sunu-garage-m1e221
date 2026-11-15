package sn.sunugarage.accueil.internal.domain;

import java.util.UUID;

public class Vehicule {

    private final UUID id;
    private final String immatriculation;
    private final UUID clientId;
    private StatutSortie statut;

    public Vehicule(UUID id, String immatriculation, UUID clientId) {
        this.id = id;
        this.immatriculation = immatriculation;
        this.clientId = clientId;
        this.statut = StatutSortie.EN_ATELIER;
    }

    public void marquerSortable() {
        this.statut = StatutSortie.PEUT_SORTIR;
    }

    public UUID id() {
        return id;
    }

    public String immatriculation() {
        return immatriculation;
    }

    public UUID clientId() {
        return clientId;
    }

    public StatutSortie statut() {
        return statut;
    }
}
