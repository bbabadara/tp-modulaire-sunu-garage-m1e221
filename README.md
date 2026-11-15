# SunuGarage — Monolithe modulaire

TP Monolithe modulaire (Spring Boot + Spring Modulith).
Sujet : atelier de réparation automobile à Dakar.

## Documentation

- [Découpage des modules et schéma des dépendances](DECOUPAGE.md)
- [Plan de code et stratégie de versionnement](PLAN_CODE.md)

## Prérequis

- Java 21
- Maven 3.10+

## Commandes

```bash
# Tous les tests (dont ModulesTest et le scénario de bout en bout)
mvn clean test

# Le gardien des frontières uniquement
mvn -Dtest=ModulesTest test

# Le scénario de bout en bout uniquement
mvn -Dtest=ScenarioE2ETest test

# Lancer l'application (API REST sur http://localhost:8080)
mvn spring-boot:run
```

## Avancement

- [x] Lot 0 — amorçage du projet et `ModulesTest`
- [ ] Lot 1 — documentation de conception
- [ ] Lot 2 — module Accueil
- [ ] Lot 3 — module Stock
- [ ] Lot 4 — module Atelier
- [ ] Lot 5 — module Facturation
- [ ] Lot 6 — scénario de bout en bout
- [ ] Lot 7 — casser une frontière (démonstration)
- [ ] Lot 8 — livraison
