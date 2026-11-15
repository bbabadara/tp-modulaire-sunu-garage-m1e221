# SunuGarage — Monolithe modulaire

TP Monolithe modulaire — atelier de réparation automobile à Dakar.
Projet Spring Boot + Spring Modulith, frontières vérifiées par un test.

## Sommaire

- [Modules](#modules)
- [Schéma des dépendances](#schéma-des-dépendances)
- [Fonctionnalités](#fonctionnalités)
- [Lancer le projet](#lancer-le-projet)
- [Scénario de bout en bout](#scénario-de-bout-en-bout)
- [API REST](#api-rest)
- [Étape 3 — casser une frontière](#étape-3--casser-une-frontière)
- [Versionnement](#versionnement)

## Modules

| Module | Responsabilité | Possède | Expose | Annonce |
|---|---|---|---|---|
| **Accueil** | Gérer les clients, leurs véhicules et l'autorisation de sortie. | clients, véhicules, statut de sortie | `enregistrerClient`, `enregistrerVehicule`, `vehiculeExiste`, `vehicule`, `client`, `marquerSortable` | `ClientEnregistre`, `VehiculeEnregistre`, `VehiculeSortable` |
| **Stock** | Tenir le stock de pièces, les prix du jour et les seuils d'alerte. | pièces, quantités, prix, seuils | `ajouterPiece`, `disponible`, `piece`, `sortirPiece`, `piecesSousSeuil` | `PieceAjoutee`, `PieceSortie`, `StockSousSeuil` |
| **Atelier** | Piloter le cycle de vie des ordres de réparation. | ordres, lignes de main-d'œuvre, lignes de pièces, statuts | `ouvrirOrdre`, `ajouterMainOeuvre`, `ajouterPiece`, `cloturer`, `ordre`, `ordreOuvert` | `OrdreOuvert`, `OrdreCloture` |
| **Facturation** | Produire la facture à la clôture et encaisser le paiement. | factures, paiements | `emettreFacture`, `payer`, `facture`, `factureDe` | `FactureEmise`, `FacturePayee` |

## Schéma des dépendances

```
                    Atelier
                   /   |    \
          direct  /  direct  \  direct
                 /     |      \
                v      v       v
            Accueil  Stock  Facturation
                \              ^
                 \__ événement FacturePayee __/
```

| De → Vers | Type | Pourquoi |
|---|---|---|
| Accueil → Facturation | **événement** `FacturePayee` | Accueil marque le véhicule « peut sortir » après paiement. |
| Atelier → Accueil | **appel direct** | Vérifier que le véhicule existe avant d'ouvrir un ordre. |
| Atelier → Stock | **appel direct** | `sortirPiece` vérifie le disponible et renvoie le prix du jour immédiatement. |
| Atelier → Facturation | **appel direct** | La clôture produit la facture immédiatement avec les lignes de l'ordre. |

Graphe acyclique : `Stock` et `Facturation` sont des feuilles, `Accueil → Facturation`,
`Atelier → {Accueil, Stock, Facturation}`.

Les frontières sont déclarées dans les `package-info.java` :

- `Accueil` : `allowedDependencies = "facturation"` (écoute `FacturePayee`).
- `Atelier` : `allowedDependencies = {"accueil", "stock", "facturation"}`.
- `Stock` et `Facturation` : aucune dépendance sortante.

## Fonctionnalités

| N° | Fonctionnalité | Module |
|---|---|---|
| F1 | Enregistrer un client et son véhicule | Accueil |
| F2 | Ajouter des pièces au stock | Stock |
| F3 | Consulter les pièces sous le seuil d'alerte | Stock |
| F4 | Ouvrir un ordre de réparation | Atelier |
| F5 | Ajouter des heures de main-d'œuvre | Atelier |
| F6 | Ajouter une pièce (sortie de stock) | Atelier → Stock |
| F7 | Clôturer l'ordre | Atelier |
| F8 | Produire la facture à la clôture | Atelier → Facturation |
| F9 | Payer la facture et libérer le véhicule | Facturation → événement → Accueil |

Règles métier :

- un véhicule n'a qu'un seul ordre ouvert à la fois (**Atelier**) ;
- impossible de sortir plus de pièces qu'il n'y en a (**Stock**) ;
- un ordre clôturé n'est plus modifiable (**Atelier**) ;
- total = 10 000 XOF/heure + pièces au prix figé à la pose (**Facturation**).

## Lancer le projet

Prérequis : Java 21 et Maven 3.10+.

```bash
# Tous les tests
mvn clean test

# Le gardien des frontières
mvn -Dtest=ModulesTest test

# Le scénario de bout en bout
mvn -Dtest=ScenarioE2ETest test

# L'application (API REST sur http://localhost:8080)
mvn spring-boot:run
```

## Scénario de bout en bout

Déroulé vérifié par `ScenarioE2ETest` :

1. enregistrer le client **Awa Sow** et le véhicule **DK-123-AB** ;
2. mettre **4 plaquettes de frein** à **15 000 XOF** en stock ;
3. ouvrir un ordre pour le véhicule ;
4. ajouter **2 heures** et **2 plaquettes** → le stock passe à **2** ;
5. clôturer → la facture vaut **50 000 XOF** ;
6. ajouter une pièce sur l'ordre clôturé → **refusé** (`OrdreClotureException`) ;
7. payer → le véhicule passe **PEUT_SORTIR** (via l'événement `FacturePayee`).

## API REST

| Méthode | Chemin | Rôle |
|---|---|---|
| POST | `/api/accueil/clients` | Enregistrer un client |
| GET | `/api/accueil/clients/{id}` | Consulter un client |
| POST | `/api/accueil/vehicules` | Enregistrer un véhicule |
| GET | `/api/accueil/vehicules/{id}` | Consulter un véhicule et son statut |
| POST | `/api/stock/pieces` | Ajouter des pièces |
| GET | `/api/stock/pieces/{reference}` | Consulter une pièce |
| GET | `/api/stock/pieces/{reference}/disponible` | Quantité disponible |
| GET | `/api/stock/alertes` | Pièces sous le seuil |
| POST | `/api/atelier/ordres` | Ouvrir un ordre |
| POST | `/api/atelier/ordres/{id}/main-oeuvre` | Ajouter de la main-d'œuvre |
| POST | `/api/atelier/ordres/{id}/pieces` | Ajouter une pièce |
| POST | `/api/atelier/ordres/{id}/cloture` | Clôturer l'ordre |
| GET | `/api/atelier/ordres/{id}` | Consulter un ordre |
| GET | `/api/facturation/ordres/{ordreId}/facture` | Facture d'un ordre |
| POST | `/api/facturation/factures/{id}/paiement` | Payer une facture |

## Étape 3 — casser une frontière

### La violation volontaire

Dans `AtelierService`, on a ajouté une méthode qui référence directement une classe
**interne** du module Stock (`sn.sunugarage.stock.internal.domain.Piece`) :

```java
String violationVolontaireDeFrontiere() {
    return sn.sunugarage.stock.internal.domain.Piece.class.getName();
}
```

L'application **compile** (`mvn -DskipTests compile` passe), mais `ModulesTest` échoue.

### Message d'erreur de ModulesTest

Commande : `mvn -Dtest=ModulesTest test`

```text
org.springframework.modulith.core.Violations: - Module 'atelier' depends on module 'stock' via sn.sunugarage.atelier.internal.application.AtelierService -> sn.sunugarage.stock.internal.domain.Piece. Allowed targets: accueil, stock, facturation.
	at org.springframework.modulith.core.Violations.and(Violations.java:141)
	at org.springframework.modulith.core.ApplicationModules.detectViolations(ApplicationModules.java:505)
	at org.springframework.modulith.core.ApplicationModules.verify(ApplicationModules.java:459)
	at org.springframework.modulith.core.ApplicationModules.verify(ApplicationModules.java:443)
	at sn.sunugarage.ModulesTest.verifieLesFrontieresDesModules(ModulesTest.java:12)
```

### La correction

On supprime l'accès direct à `stock.internal`. La bonne façon de faire est de passer par
l'**API publique** du module Stock (`StockApi.sortirPiece` / `StockApi.disponible`), qui
encapsule la règle métier et ne laisse sortir qu'un DTO (`PieceSortieDto`). L'intérieur
du module Stock reste invisible depuis Atelier, et `ModulesTest` repasse au vert.

## Versionnement

- `main` : livrable stable, tags `v0.1.0`, `v0.2.0`, `v0.3.0`, `v1.0.0`.
- `develop` : intégration.
- Branches de lots : `chore/bootstrap`, `docs/decoupage`, `feature/accueil`,
  `feature/stock`, `feature/atelier`, `feature/facturation`, `feature/scenario-e2e`,
  `demo/frontiere-cassee`, `release/v1.0.0`.
- Commits au format Conventional Commits, fusion en `--no-ff` pour garder la trace des lots.

Documentation de conception : [`DECOUPAGE.md`](DECOUPAGE.md) et [`PLAN_CODE.md`](PLAN_CODE.md).
