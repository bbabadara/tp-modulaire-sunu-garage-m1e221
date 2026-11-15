# SunuGarage — Découpage en modules

> Étape 1 du TP Monolithe modulaire : concevoir les modules et les dépendances avant d'écrire du code.
> Sujet : atelier de réparation automobile (Projet 4 — SunuGarage).

## 1. Modules et responsabilités

| Module | Responsabilité |
|---|---|
| **Accueil** (Clients & Véhicules) | Gérer l'identité des clients, leurs véhicules (immatriculation) et l'autorisation de sortie du véhicule. |
| **Stock** (Pièces) | Tenir le stock de pièces, les prix du jour et les seuils d'alerte. |
| **Atelier** (Ordres de réparation) | Piloter le cycle de vie d'un ordre : ouverture, main-d'œuvre, pose de pièces, clôture. |
| **Facturation** | Produire la facture à la clôture, encaisser le paiement et suivre son statut. |

## 2. Rangement des fonctionnalités

Chaque fonctionnalité appartient à un seul module.

| N° | Fonctionnalité | Module |
|---|---|---|
| F1 | L'accueil enregistre un client et son véhicule (immatriculation). | **Accueil** |
| F2 | Le magasinier ajoute des pièces au stock (référence, quantité, prix). | **Stock** |
| F3 | Le magasinier consulte les pièces sous le seuil d'alerte. | **Stock** |
| F4 | Un mécanicien ouvre un ordre de réparation pour un véhicule. | **Atelier** |
| F5 | Un mécanicien ajoute des heures de main-d'œuvre à un ordre. | **Atelier** |
| F6 | Un mécanicien ajoute une pièce à un ordre, ce qui la sort du stock. | **Atelier** (appelle **Stock**) |
| F7 | Un mécanicien clôture l'ordre de réparation. | **Atelier** |
| F8 | Une facture est produite à la clôture. | **Facturation** (appelée par **Atelier**) |
| F9 | Le client paie la facture, le véhicule peut alors sortir. | **Facturation** → événement → **Accueil** |

## 3. Ce que chaque module possède, expose et annonce

| Module | Possède (ses données) | Expose (API publique) | Annonce (événements, au passé) |
|---|---|---|---|
| **Accueil** | clients, véhicules, statut de sortie | `enregistrerClient`, `enregistrerVehicule`, `vehiculeExiste`, `marquerSortable` | `ClientEnregistre`, `VehiculeEnregistre`, `VehiculeSortable` |
| **Stock** | pièces, quantités, prix, seuils d'alerte | `ajouterPiece`, `disponible(ref)`, `sortirPiece(ref, qte)` → prix du jour, `piecesSousSeuil()` | `PieceAjoutee`, `PieceSortie`, `StockSousSeuil` |
| **Atelier** | ordres, lignes de main-d'œuvre, lignes de pièces, statuts | `ouvrirOrdre`, `ajouterMainOeuvre`, `ajouterPiece`, `cloturer`, `ordreOuvert(vehiculeId)` | `OrdreOuvert`, `OrdreCloture` |
| **Facturation** | factures, paiements | `emettreFacture`, `payer`, `factureDe(ordreId)` | `FactureEmise`, `FacturePayee` |

> Les références circulent **par identifiant** (`clientId`, `vehiculeId`, `ordreId`, `reference` de pièce).
> Aucun objet du domaine d'un module ne traverse la frontière d'un autre : seuls des DTO et des identifiants passent.

## 4. Schéma des dépendances (sans cycle)

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
| **Accueil → Facturation** | **événement** `FacturePayee` | Accueil marque le véhicule « peut sortir » après paiement, sans que Facturation connaisse Accueil. Écouter l'événement crée bien une dépendance vers Facturation. |
| **Atelier → Accueil** | **appel direct** | Vérifier que le véhicule existe avant d'ouvrir un ordre : la réponse est nécessaire immédiatement. |
| **Atelier → Stock** | **appel direct** | `sortirPiece` doit vérifier la quantité disponible et renvoyer le prix du jour tout de suite (règle métier synchrone). |
| **Atelier → Facturation** | **appel direct** | La clôture doit produire la facture immédiatement, avec les lignes de l'ordre. |

Le graphe est **acyclique** :

```
Stock         (feuille)
Facturation   (feuille)
Accueil       → Facturation
Atelier       → Accueil, Stock, Facturation
```

Atelier est le seul module d'orchestration ; les autres ne dépendent que vers le bas.

## 5. Où vivent les règles métier

| Règle | Module propriétaire | Application |
|---|---|---|
| Un véhicule n'a qu'un seul ordre ouvert à la fois. | **Atelier** | `ouvrirOrdre` refuse si `ordreOuvert(vehiculeId)` existe. |
| On ne peut pas sortir plus de pièces qu'il n'y en a. | **Stock** | `sortirPiece` refuse et lève une erreur si la quantité demandée dépasse le disponible. |
| Un ordre clôturé ne peut plus être modifié. | **Atelier** | `ajouterMainOeuvre` et `ajouterPiece` refusent si le statut est `CLOTURE`. |
| Total facture = main-d'œuvre (10 000 XOF/h) + pièces au prix du jour de la pose. | **Facturation** | Calcule le total à partir des lignes transmises ; les prix des pièces sont figés par Atelier au moment de la pose. |

## 6. Déroulé du scénario de bout en bout

1. **Accueil** : `enregistrerClient` puis `enregistrerVehicule`.
2. **Stock** : `ajouterPiece(4 plaquettes, 15 000 XOF)`.
3. **Atelier** : `ouvrirOrdre(vehiculeId)` — vérifie via **Accueil** que le véhicule existe et qu'aucun ordre n'est ouvert.
4. **Atelier** : `ajouterMainOeuvre(ordreId, 2 h)` puis `ajouterPiece(ordreId, plaquettes, 2)` — **Stock** sort 2 pièces, le stock passe à 2, le prix 15 000 est figé sur la ligne.
5. **Atelier** : `cloturer(ordreId)` — appelle **Facturation**.`emettreFacture` → total = 2 × 10 000 + 2 × 15 000 = **50 000 XOF**.
6. Ajout d'une pièce sur l'ordre clôturé → **refusé** (Atelier).
7. **Facturation** : `payer(factureId)` → publie `FacturePayee` → **Accueil** marque le véhicule **« peut sortir »**.

## 7. Les deux modes de communication (obligatoires)

- **Appel direct** : Atelier → Stock (`sortirPiece`) et Atelier → Facturation (`emettreFacture`).
- **Événement** : Facturation publie `FacturePayee`, écouté par Accueil pour libérer le véhicule.
