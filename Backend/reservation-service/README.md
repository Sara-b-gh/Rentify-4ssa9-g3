# reservation-service (Rentify)

Microservice **Réservation** : Spring Boot 3.3, Java 17, Maven, MySQL, RabbitMQ, OpenFeign + Resilience4j, Eureka.
Responsable : Omaima (Membre 2).

## Rôle
Gère le cycle de vie d'une réservation : créer, modifier les dates, annuler.
Il ne stocke que l'identifiant du client et du véhicule (les données appartiennent à d'autres services).

## Modèle de données (table `reservations`, base `reservation_db`)
| Champ | Type | Description |
|---|---|---|
| id | UUID | identifiant de la réservation |
| clientId | texte | identifiant du client |
| vehiculeId | UUID | identifiant du véhicule (service Flotte) |
| dateDebut, dateFin | date | période réservée |
| montantEstime | nombre | nombre de jours × prix journalier |
| statut | texte | CONFIRMEE, ANNULEE, TERMINEE |

## Prérequis
- Java 17 et Maven
- MySQL (XAMPP) démarré, ou le profil `h2` pour tester sans MySQL

## Lancer
1. Avec MySQL (la base `reservation_db` est créée automatiquement) : `mvn spring-boot:run`
2. Sans rien installer (base H2 en mémoire) : `mvn spring-boot:run -Dspring-boot.run.profiles=h2`

Variables d'environnement (toutes optionnelles) :
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `FLOTTE_URL` (par défaut http://localhost:8081),
`RABBIT_ENABLED` (false par défaut, mettre true quand RabbitMQ est lancé), `RABBIT_HOST`, `EUREKA_ENABLED` (false par défaut).

Port : **8082**
- Santé : http://localhost:8082/actuator/health
- Swagger : http://localhost:8082/swagger-ui.html

## API REST
| Méthode | URL | Codes |
|---|---|---|
| POST | /api/reservations | 201, 400, 409, 503 |
| GET | /api/reservations | 200 |
| GET | /api/reservations/{id} | 200, 404 |
| PUT | /api/reservations/{id} | 200, 400, 404, 409, 503 |
| DELETE | /api/reservations/{id} | 204, 404, 409 |

Exemple :
```
POST /api/reservations
{"clientId":"client-1","vehiculeId":"3fa85f64-5717-4562-b3fc-2c963f66afa6","dateDebut":"2026-12-01","dateFin":"2026-12-05"}
```

## Interactions
- **Appelle Flotte** (synchrone, OpenFeign) : `GET /api/vehicules/{id}/disponibilite?debut=&fin=` → `{"disponible":true,"prixJournalier":50.0}`.
  Si Flotte ne répond pas (délai de 3 s) : réservation refusée (503), rien n'est enregistré.
- **Publie** (RabbitMQ, échange `rentify.events`) : `reservation.confirmee`, `reservation.annulee`.
- **Consomme** : `utilisateur.desactive` → annule les réservations futures du client.
- **Appelé par** : le service Contrats.

## Tester
Le service Flotte n'étant pas encore disponible, un faux Flotte (`FlotteMock.java`, port 8081) répond « disponible, 50 par jour ».
Tests unitaires : `mvn test`