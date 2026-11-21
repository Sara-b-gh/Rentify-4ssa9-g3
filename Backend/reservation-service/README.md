# reservation-service (Rentify)

Microservice **Réservation** – Spring Boot 3.3, Java 21, Maven, MySQL, RabbitMQ, OpenFeign + Resilience4j, Eureka.

## Lancer
1. Démarrage rapide sans rien installer (base H2 en mémoire, sans RabbitMQ) :
   `mvn spring-boot:run -Dspring-boot.run.profiles=h2`
2. Avec MySQL (la base `reservation_db` est créée automatiquement) :
   `set DB_PASSWORD=...` puis `mvn spring-boot:run`
   (variables : DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD, RABBIT_HOST, FLOTTE_URL, EUREKA_ENABLED)

Port : **8082**. Santé : http://localhost:8082/actuator/health

## API
| Méthode | URL | Codes |
|---|---|---|
| POST | /api/reservations | 201, 400, 409, 503 |
| GET | /api/reservations/{id} | 200, 404 |
| PUT | /api/reservations/{id} | 200, 400, 404, 409, 503 |
| DELETE | /api/reservations/{id} | 204, 404, 409 |

Exemple :
```
POST /api/reservations
{"clientId":"client-1","vehiculeId":"3fa85f64-5717-4562-b3fc-2c963f66afa6","dateDebut":"2026-12-01","dateFin":"2026-12-05"}
```

## Événements (RabbitMQ, échange `rentify.events`)
- publiés : `reservation.confirmee`, `reservation.annulee`
- consommé : `utilisateur.desactive` → annule les réservations futures du client

## Dépendance
Appelle Flotte : `GET /api/vehicules/{id}/disponibilite?debut=&fin=` → `{"disponible":true,"prixJournalier":50.0}`.
Si Flotte ne répond pas (timeout 3 s) : réservation refusée (503), rien n'est enregistré.


## Services appelés (à lancer avant de tester)

- **flotte-service** (port 8081) : disponibilité et prix du véhicule.
- **utilisateurs-service** (port 8087) : le client doit exister (404 sinon) et son compte doit être actif (409 sinon).

Si l'un d'eux est arrêté, la création d'une réservation répond **503**. Plus besoin de `FlotteMock` : on utilise le vrai service Flotte.

Ordre de test : Utilisateurs (créer un client) → Flotte (catégorie + véhicule) → Réservation (`clientId` et `vehiculeId` réels).
