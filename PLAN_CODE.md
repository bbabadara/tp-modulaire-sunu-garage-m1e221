# SunuGarage — Plan de code et stratégie de versionnement

> À lire après `DECOUPAGE.md`. Ce document décrit l'ordre de construction, la structure cible,
> la stratégie Git (branches, commits réguliers, tags) et les critères de fin de chaque étape.

## 1. Stack et versions

| Élément | Choix |
|---|---|
| Langage | Java 21 |
| Build | Maven 3.10 |
| Framework | Spring Boot **3.5.3** |
| Modularité | Spring Modulith **1.4.1** (`spring-modulith-bom`) |
| Web | `spring-boot-starter-web` |
| Tests | `spring-boot-starter-test`, `spring-modulith-starter-test` |
| Persistance | **En mémoire** (`ConcurrentHashMap`) derrière des ports ; pas de JPA/H2 |
| Base package | `sn.sunugarage` |
| Artefact | `sn.sunugarage:sunu-garage:1.0.0-SNAPSHOT` |

## 2. Arborescence cible

```
sunu-garage/
├── pom.xml
├── README.md
├── DECOUPAGE.md
├── PLAN_CODE.md
├── .gitignore
└── src
    ├── main
    │   ├── java/sn/sunugarage
    │   │   ├── SunuGarageApplication.java
    │   │   ├── accueil
    │   │   │   ├── AccueilApi.java              # porte d'entrée
    │   │   │   ├── ClientDto.java               # DTO exposé
    │   │   │   ├── VehiculeDto.java
    │   │   │   ├── ClientEnregistre.java        # événements
    │   │   │   ├── VehiculeEnregistre.java
    │   │   │   ├── VehiculeSortable.java
    │   │   │   ├── package-info.java            # allowedDependencies = "facturation"
    │   │   │   └── internal
    │   │   │       ├── domain
    │   │   │       │   ├── Client.java
    │   │   │       │   ├── Vehicule.java
    │   │   │       │   └── StatutSortie.java
    │   │   │       ├── application
    │   │   │       │   └── AccueilService.java  # implements AccueilApi
    │   │   │       └── infrastructure
    │   │   │           ├── ClientRepository.java
    │   │   │           ├── VehiculeRepository.java
    │   │   │           ├── FacturePayeeListener.java
    │   │   │           └── web/AccueilController.java
    │   │   ├── stock
    │   │   │   ├── StockApi.java
    │   │   │   ├── PieceDto.java
    │   │   │   ├── PieceSortieDto.java
    │   │   │   ├── StockInsuffisantException.java
    │   │   │   ├── PieceAjoutee.java
    │   │   │   ├── PieceSortie.java
    │   │   │   ├── StockSousSeuil.java
    │   │   │   └── internal
    │   │   │       ├── domain/{Piece.java, Stock.java}
    │   │   │       ├── application/StockService.java
    │   │   │       └── infrastructure/{PieceRepository.java, web/StockController.java}
    │   │   ├── atelier
    │   │   │   ├── AtelierApi.java
    │   │   │   ├── OrdreDto.java
    │   │   │   ├── LignePieceDto.java
    │   │   │   ├── OrdreIntrouvableException.java
    │   │   │   ├── OrdreDejaOuvertException.java
    │   │   │   ├── OrdreClotureException.java
    │   │   │   ├── OrdreOuvert.java
    │   │   │   ├── OrdreCloture.java
    │   │   │   ├── package-info.java            # allowedDependencies = accueil, stock, facturation
    │   │   │   └── internal
    │   │   │       ├── domain/{OrdreReparation.java, StatutOrdre.java, LigneMainOeuvre.java, LignePiece.java}
    │   │   │       ├── application/AtelierService.java
    │   │   │       └── infrastructure/{OrdreRepository.java, web/AtelierController.java}
    │   │   └── facturation
    │   │       ├── FacturationApi.java
    │   │       ├── FactureRequest.java          # DTO construit par Atelier
    │   │       ├── LignePieceFacture.java
    │   │       ├── FactureDto.java
    │   │       ├── FactureIntrouvableException.java
    │   │       ├── FactureDejaPayeeException.java
    │   │       ├── FactureEmise.java
    │   │       ├── FacturePayee.java
    │   │       └── internal
    │   │           ├── domain/{Facture.java, StatutFacture.java}
    │   │           ├── application/FacturationService.java
    │   │           └── infrastructure/{FactureRepository.java, web/FacturationController.java}
    │   └── resources/application.yml
    └── test
        └── java/sn/sunugarage
            ├── ModulesTest.java                 # le gardien
            ├── ScenarioE2ETest.java             # scénario complet
            ├── accueil/AccueilModuleTest.java
            ├── stock/StockModuleTest.java
            ├── atelier/AtelierModuleTest.java
            └── facturation/FacturationModuleTest.java
```

Règle : tout ce qui n'est pas `*Api`, DTO, événement ou `package-info` va dans `internal`
(`domain` → règles métier, `application` → orchestration, `infrastructure` → adapters web/repo).

## 3. Contrats entre modules

### 3.1 APIs publiques

| Module | Méthodes exposées |
|---|---|
| **AccueilApi** | `UUID enregistrerClient(String nom, String telephone)` · `UUID enregistrerVehicule(String immatriculation, UUID clientId)` · `boolean vehiculeExiste(UUID vehiculeId)` · `VehiculeDto vehicule(UUID vehiculeId)` · `void marquerSortable(UUID vehiculeId)` |
| **StockApi** | `void ajouterPiece(String reference, String libelle, int quantite, long prixUnitaire, int seuilAlerte)` · `int disponible(String reference)` · `PieceSortieDto sortirPiece(String reference, int quantite)` · `List<PieceDto> piecesSousSeuil()` |
| **AtelierApi** | `UUID ouvrirOrdre(UUID vehiculeId)` · `void ajouterMainOeuvre(UUID ordreId, BigDecimal heures)` · `void ajouterPiece(UUID ordreId, String reference, int quantite)` · `void cloturer(UUID ordreId)` · `OrdreDto ordre(UUID ordreId)` · `boolean ordreOuvert(UUID vehiculeId)` |
| **FacturationApi** | `UUID emettreFacture(FactureRequest request)` · `void payer(UUID factureId)` · `FactureDto factureDe(UUID ordreId)` · `FactureDto facture(UUID factureId)` |

### 3.2 Sens des dépendances

```
Atelier ──direct──> Accueil
Atelier ──direct──> Stock
Atelier ──direct──> Facturation
Accueil ──événement FacturePayee──> Facturation
```

- `AtelierService.cloturer` fabrique un `FactureRequest` (DTO public de `facturation`) et l'envoie à `FacturationApi.emettreFacture`.
- `Accueil` contient un `FacturePayeeListener` annoté `@ApplicationModuleListener` qui appelle `marquerSortable`.
- Aucun autre sens, donc **aucun cycle**.

### 3.3 Calcul de la facture (règle dans Facturation)

```
total = heuresMainOeuvre × 10 000 + Σ(quantite × prixUnitaireFige)
```

Les `prixUnitaireFige` viennent de `StockApi.sortirPiece` au moment de la pose : c'est le prix du jour.
`Atelier` ne calcule aucun montant ; il transmet les faits (heures, pièces, prix figés).

## 4. Lots de travail et commits

Cadence : **un commit par intention**, au minimum toutes les 20–30 min.
Chaque commit compile et passe `mvn test`, sauf le commit volontaire de l'étape 7.
Convention : **Conventional Commits** avec portée = module (`feat(accueil): ...`).

### Lot 0 — Amorçage (branche `chore/bootstrap`)

| # | Action | Commit |
|---|---|---|
| 0.1 | `git init`, `.gitignore` (target, .idea, *.iml, .DS_Store), `pom.xml`, `application.yml`, classe main | `chore: initialise le projet Spring Boot + Spring Modulith` |
| 0.2 | Créer les 4 packages vides + `ModulesTest` vide | `test: ajoute le gardien ModulesTest (packages vides)` |
| 0.3 | Vérifier : `mvn test` vert | `chore: vérifie que le gardien passe sur le squelette` |

### Lot 1 — Documentation (branche `docs/decoupage`)

| # | Action | Commit |
|---|---|---|
| 1.1 | `DECOUPAGE.md` | `docs: ajoute le tableau des modules et le schéma des dépendances` |
| 1.2 | `PLAN_CODE.md` + squelette `README.md` | `docs: ajoute le plan de code et la stratégie Git` |

### Lot 2 — Accueil (branche `feature/accueil`)

| # | Action | Commit |
|---|---|---|
| 2.1 | Domaine `Client`, `Vehicule`, `StatutSortie` | `feat(accueil): ajoute le domaine client et véhicule` |
| 2.2 | Ports + repositories mémoire | `feat(accueil): ajoute les repositories en mémoire` |
| 2.3 | `AccueilApi` + `AccueilService` + DTO | `feat(accueil): expose l'enregistrement client et véhicule` |
| 2.4 | `package-info.java` + événements | `feat(accueil): declare la frontiere et publie les evenements` |
| 2.5 | `AccueilController` + `AccueilModuleTest` | `feat(accueil): ajoute l'API REST et les tests du module` |
| 2.6 | `FacturePayeeListener` (sera actif au lot 5) | `feat(accueil): ecoute FacturePayee pour liberer le vehicule` |

### Lot 3 — Stock (branche `feature/stock`)

| # | Action | Commit |
|---|---|---|
| 3.1 | Domaine `Piece` + règle de décrément/refus | `feat(stock): ajoute le domaine piece et la regle de stock` |
| 3.2 | Repositories mémoire | `feat(stock): ajoute le repository de pieces` |
| 3.3 | `StockApi`/`StockService` (`ajouterPiece`, `disponible`, `sortirPiece`, `piecesSousSeuil`) | `feat(stock): expose l'ajout, la sortie et les alertes de stock` |
| 3.4 | `StockController` + `StockModuleTest` | `feat(stock): ajoute l'API REST et les tests du module` |
| 3.5 | Événements `PieceAjoutee`, `PieceSortie`, `StockSousSeuil` | `feat(stock): publie les evenements de stock` |

### Lot 4 — Atelier (branche `feature/atelier`)

| # | Action | Commit |
|---|---|---|
| 4.1 | Domaine `OrdreReparation`, `LigneMainOeuvre`, `LignePiece`, `StatutOrdre` | `feat(atelier): ajoute le domaine des ordres de reparation` |
| 4.2 | Repository mémoire | `feat(atelier): ajoute le repository d'ordres` |
| 4.3 | `package-info.java` + `AtelierApi` | `feat(atelier): declare les dependances autorisees et l'API` |
| 4.4 | `ouvrirOrdre` (véhicule existe + un seul ordre ouvert) | `feat(atelier): ouvre un ordre en verifiant vehicule et unicite` |
| 4.5 | `ajouterMainOeuvre` + refus si clôturé | `feat(atelier): ajoute la main-d'oeuvre et bloque les ordres clotures` |
| 4.6 | `ajouterPiece` via `StockApi.sortirPiece` (prix figé) | `feat(atelier): sort les pieces du stock et fige le prix` |
| 4.7 | `cloturer` via `FacturationApi.emettreFacture` + événement | `feat(atelier): cloture l'ordre et demande la facture` |
| 4.8 | `AtelierController` + `AtelierModuleTest` | `feat(atelier): ajoute l'API REST et les tests du module` |

### Lot 5 — Facturation (branche `feature/facturation`)

| # | Action | Commit |
|---|---|---|
| 5.1 | Domaine `Facture`, `StatutFacture` + calcul du total | `feat(facturation): ajoute le domaine facture et le calcul du total` |
| 5.2 | Repository mémoire | `feat(facturation): ajoute le repository de factures` |
| 5.3 | `FacturationApi`/`Service` (`emettreFacture`, `payer`, `factureDe`) | `feat(facturation): expose l'emission et le paiement des factures` |
| 5.4 | Événements `FactureEmise`, `FacturePayee` | `feat(facturation): publie les evenements facture` |
| 5.5 | `FacturationController` + `FacturationModuleTest` | `feat(facturation): ajoute l'API REST et les tests du module` |

### Lot 6 — Scénario de bout en bout (branche `feature/scenario-e2e`)

| # | Action | Commit |
|---|---|---|
| 6.1 | `ScenarioE2ETest` déroule les 7 étapes et vérifie chaque résultat | `test(e2e): deroule le scenario SunuGarage de bout en bout` |
| 6.2 | `README.md` complet (tableau, schéma, commandes) | `docs: complete le README avec le scenario et les commandes` |
| 6.3 | Vérifier `mvn test` complet vert | `test: valide l'ensemble des tests avant demonstration` |

### Lot 7 — Casser une frontière (branche `demo/frontiere-cassee`)

| # | Action | Commit |
|---|---|---|
| 7.1 | Violation volontaire : `AtelierService` lit `stock.internal.domain.Piece` (ou appelle un type internal) | `test(demo): viole volontairement la frontiere atelier -> stock` |
| 7.2 | Lancer `mvn -Dtest=ModulesTest test`, copier le message d'erreur dans le README | `docs: consigne le message d'echec de ModulesTest` |
| 7.3 | Correction : passer par `StockApi.disponible` / `sortirPiece` | `fix(atelier): respecte la frontiere via l'API publique de stock` |
| 7.4 | Vérifier que tout repasse au vert | `test: restaure le gardien apres correction` |

> La branche `demo/frontiere-cassee` conserve **les deux commits** (violation + correction) :
> c'est la preuve demandée à l'étape 3 du TP. Elle est fusionnée dans `develop` avec le correctif.

### Lot 8 — Livraison (branche `release/v1.0.0`)

| # | Action | Commit |
|---|---|---|
| 8.1 | Repasse finale : `mvn clean test`, README relu | `chore(release): prepare la version 1.0.0` |
| 8.2 | Fusion `release/v1.0.0` → `main` + tag `v1.0.0` | tag `v1.0.0` |

## 5. Stratégie Git

### 5.1 Branches

```
main        ●───────────────────────────────● (tag v1.0.0)
             \                             /
develop       ●──●────●────●────●────●────●
               \  \    \    \    \    \
                \  docs  feat feat feat demo
                 chore   accueil stock atelier ...
```

| Branche | Rôle | Durée de vie |
|---|---|---|
| `main` | Livrable stable, uniquement des merges depuis `develop`/`release` | permanente |
| `develop` | Intégration continue des lots | permanente |
| `chore/bootstrap` | Amorçage du projet | courte |
| `docs/decoupage` | Documentation de conception | courte |
| `feature/accueil` | Module Accueil | courte |
| `feature/stock` | Module Stock | courte |
| `feature/atelier` | Module Atelier | courte |
| `feature/facturation` | Module Facturation | courte |
| `feature/scenario-e2e` | Test de bout en bout | courte |
| `demo/frontiere-cassee` | Violation volontaire + correction | courte (conservée pour la démo) |
| `release/v1.0.0` | Stabilisation avant tag | courte |

### 5.2 Règles

1. Une branche = un lot = une intention. Créée depuis `develop`, fusionnée dans `develop` en `--no-ff`
   (ou via Pull Request) pour garder la trace du lot.
2. Jamais de commit direct sur `main` ni sur `develop`.
3. Un commit = un changement cohérent ; message au format `type(portee): description`.
4. Chaque commit doit compiler. On lance `mvn test` avant de committer
   (sauf commit 7.1 qui doit échouer exprès).
5. Les deux membres committent : définir l'auteur avant chaque session
   ```bash
   git config user.name  "Membre A"
   git config user.email "membre.a@example.com"
   ```
   et alterner les branches selon la répartition ci-dessous.
6. Fusion dans `main` uniquement après `mvn clean test` vert et README à jour.

### 5.3 Répartition pour garantir l'historique des deux membres

| Membre | Branches |
|---|---|
| **Membre A** | `chore/bootstrap`, `docs/decoupage`, `feature/accueil`, `feature/stock` |
| **Membre B** | `feature/atelier`, `feature/facturation`, `feature/scenario-e2e`, `demo/frontiere-cassee` |

Chacun fait au moins un commit de revue/correction sur la branche de l'autre
(ex. `fix(stock): corrige un cas limite signale en revue`).

### 5.4 Commandes Git types

```bash
git init
git branch -M main
git checkout -b develop
git checkout -b chore/bootstrap
# ... commits ...
git checkout develop
git merge --no-ff chore/bootstrap

git checkout -b feature/accueil
# ... commits ...
git checkout develop
git merge --no-ff feature/accueil
```

Tags :

```bash
git tag -a v0.1.0 -m "Squelette Spring Modulith"
git tag -a v0.2.0 -m "Modules Accueil, Stock, Atelier, Facturation"
git tag -a v0.3.0 -m "Scenario de bout en bout"
git tag -a v1.0.0 -m "Livraison SunuGarage"
```

## 6. Tests et critères de fin

| Test | Commande | Critère |
|---|---|---|
| Frontières | `mvn -Dtest=ModulesTest test` | `verify()` passe sans violation |
| Modules | `mvn -Dtest='*ModuleTest' test` | chaque module testé isolément |
| Scénario | `mvn -Dtest=ScenarioE2ETest test` | 7 étapes vérifiées |
| Complet | `mvn clean test` | tout vert |
| Application | `mvn spring-boot:run` | API REST joignable sur `:8080` |

### Le scénario E2E doit vérifier

1. client + véhicule enregistrés (identifiants non nuls) ;
2. stock initial = 4 plaquettes ;
3. ordre ouvert ;
4. après 2 h + 2 plaquettes : stock = 2, prix figé = 15 000 ;
5. après clôture : facture de **50 000 XOF** liée à l'ordre ;
6. ajout de pièce sur ordre clôturé → exception / HTTP 409 ;
7. après paiement : `VehiculeDto.statut == PEUT_SORTIR`.

## 7. Risques et parades

| Risque | Parade |
|---|---|
| Cycle de dépendances | Sens unique validé par `ModulesTest` à chaque lot. |
| Prix des pièces modifié après la pose | Atelier stocke le prix renvoyé par `sortirPiece`, jamais la référence seule. |
| Ordre modifié après clôture | Vérification du statut dans le domaine `OrdreReparation`, pas dans le contrôleur. |
| Événement non traité en test | Utiliser `@ApplicationModuleListener` et, en E2E, attendre la fin des traitements (`Awaitility` ou appel synchrone vérifié). |
| Versions Spring indisponibles | Boot 3.5.3 + Modulith 1.4.1 vérifiés sur Maven Central. |
