package sn.sunugarage.accueil.internal.domain;

import java.util.UUID;

public class Client {

    private final UUID id;
    private final String nom;
    private final String telephone;

    public Client(UUID id, String nom, String telephone) {
        this.id = id;
        this.nom = nom;
        this.telephone = telephone;
    }

    public UUID id() {
        return id;
    }

    public String nom() {
        return nom;
    }

    public String telephone() {
        return telephone;
    }
}
