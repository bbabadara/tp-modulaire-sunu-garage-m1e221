package sn.sunugarage.stock.internal.domain;

import sn.sunugarage.stock.StockInsuffisantException;

public class Piece {

    private final String reference;
    private final String libelle;
    private int quantite;
    private long prixUnitaire;
    private int seuilAlerte;

    public Piece(String reference, String libelle, int quantite, long prixUnitaire, int seuilAlerte) {
        if (quantite < 0) {
            throw new IllegalArgumentException("La quantite initiale ne peut pas etre negative");
        }
        if (prixUnitaire < 0) {
            throw new IllegalArgumentException("Le prix unitaire ne peut pas etre negatif");
        }
        if (seuilAlerte < 0) {
            throw new IllegalArgumentException("Le seuil d'alerte ne peut pas etre negatif");
        }
        this.reference = reference;
        this.libelle = libelle;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.seuilAlerte = seuilAlerte;
    }

    public void ajouter(int quantite) {
        exigerQuantitePositive(quantite);
        this.quantite += quantite;
    }

    public void changerPrix(long nouveauPrix) {
        if (nouveauPrix < 0) {
            throw new IllegalArgumentException("Le prix unitaire ne peut pas etre negatif");
        }
        this.prixUnitaire = nouveauPrix;
    }

    public void sortir(int quantite) {
        exigerQuantitePositive(quantite);
        if (quantite > this.quantite) {
            throw new StockInsuffisantException(reference, this.quantite, quantite);
        }
        this.quantite -= quantite;
    }

    public boolean sousSeuil() {
        return quantite < seuilAlerte;
    }

    private void exigerQuantitePositive(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre strictement positive");
        }
    }

    public String reference() {
        return reference;
    }

    public String libelle() {
        return libelle;
    }

    public int quantite() {
        return quantite;
    }

    public long prixUnitaire() {
        return prixUnitaire;
    }

    public int seuilAlerte() {
        return seuilAlerte;
    }
}
