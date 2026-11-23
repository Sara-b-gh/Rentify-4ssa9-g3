# facturation-service — Microservice Facturation (Rentify)

Contexte DDD : Facturation (factures, paiements, pénalités liées aux infractions)

- Port : **8085**
- Base MySQL : **facturation_db** (créée automatiquement)
- Identifiants : tous de type `Long` (auto-incrément)
- Références vers les autres services : **par identifiant uniquement** (aucune classe partagée)
- Swagger UI : http://localhost:8085/swagger-ui.html
- Santé : http://localhost:8085/actuator/health

## Lancer

1. Démarrer **MySQL** dans XAMPP.
2. Ouvrir le projet dans IntelliJ (JDK 21), recharger Maven.
3. Lancer `FacturationServiceApplication`.
   - Sans MySQL : variable d'environnement `SPRING_PROFILES_ACTIVE=h2`.
4. Ouvrir Swagger.

## Services appelés (à lancer avant de tester)

- **contrats-service** (port 8083)
- **reservation-service** (port 8082)
- **infractions-service** (port 8084)

Si l'un d'eux est arrêté, ce service répond **503**.

## Endpoints

| Méthode | URL | Rôle | Codes |
|---|---|---|---|
| GET | `/api/factures` | Liste des factures | 200 |
| GET | `/api/factures/{id}` | Détail | 200 / 404 |
| POST | `/api/factures` | Création | 201 / 400 / 404 / 409 / 503 |
| DELETE | `/api/factures/{id}` | Suppression | 204 / 404 |
| GET | `/api/factures/{id}/reste` | Total payé et reste à payer | 200 / 404 |
| GET | `/api/paiements` | Liste des paiements | 200 |
| GET | `/api/paiements/{id}` | Détail | 200 / 404 |
| POST | `/api/paiements` | Création | 201 / 400 / 404 / 409 / 503 |
| GET | `/api/paiements/par-facture/{factureId}` | Paiements d'une facture | 200 |
| GET | `/api/penalites` | Liste des pénalités | 200 |
| GET | `/api/penalites/{id}` | Détail | 200 / 404 |
| POST | `/api/penalites` | Création | 201 / 400 / 404 / 409 / 503 |

## Exemples de corps JSON

**POST /api/factures**
```json
{
  "contratId": 1
}
```

**POST /api/paiements**
```json
{
  "factureId": 1,
  "montant": 100.0,
  "mode": "CARTE"
}
```

**POST /api/penalites**
```json
{
  "factureId": 1,
  "infractionId": 1
}
```

## Scénario de test (Swagger)

1. Prérequis : **Contrats** (8083) avec un contrat **clôturé** (id 1), **Réservation** (8082) et **Infractions** (8084) lancés, avec une infraction identifiée sur ce contrat.
2. POST `/api/factures` avec `{"contratId": 1}` → 201 ; `clientId` et `montantTotal` viennent des autres services (statut EMISE).
3. POST `/api/factures` avec le même contrat → 409 ; avec un contrat non clôturé → 409 ; avec un contrat inconnu → 404.
4. POST `/api/penalites` avec `{"factureId": 1, "infractionId": 1}` → 201 ; le montant vient de l'infraction et s'ajoute au total de la facture.
5. POST `/api/penalites` avec une infraction non identifiée → 409 ; la même infraction → 409.
6. POST `/api/paiements` (factureId 1, montant 100, CARTE) → 201 ; GET `/api/factures/1/reste` → reste à payer.
7. POST `/api/paiements` avec un montant trop grand → 409 ; avec le montant exact du reste → 201 et facture PAYEE.
8. Arrêter **Contrats** puis POST `/api/factures` → 503.

## Tests unitaires

`mvn test` (ou IntelliJ : clic droit sur `src/test` puis Run).

## Pousser sur Git

```
git pull
git add Backend/facturation-service
git commit -m "feat(facturation): microservice Facturation"
git push
```
