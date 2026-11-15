# contrats-service

Microservice **Contrats** de Rentify : ouverture et clôture des contrats de location, conducteurs, états des lieux.

## Responsabilités
- Ouvrir / clôturer un contrat, gérer les conducteurs et les états des lieux (départ, retour).
- Répondre à « quel contrat était actif pour ce véhicule à cette date ? » (utilisé par Infractions).

## Données (MySQL `contrats_db`)
`Contrat`, `Conducteur`, `EtatDesLieux`. Liens vers les autres services par identifiant uniquement.

## API
| Méthode | Endpoint | Codes |
|---|---|---|
| POST | /api/contrats | 201, 400, 401, 403, 404, 409 |
| GET | /api/contrats/{id} | 200, 404 |
| GET | /api/contrats?clientId= | 200 |
| GET | /api/contrats/actif?vehiculeId=&dateHeure= | 200, 404 |
| POST | /api/contrats/{id}/conducteurs | 201, 400, 404 |
| POST | /api/contrats/{id}/retour | 200, 404, 409 |

## Interactions
- Synchrone (OpenFeign) : Réservation, Utilisateurs. Appelé par Infractions.
- Asynchrone (RabbitMQ, exchange `rentify.events`) : publie `ContratOuvert`, `ContratCloture`.

## Lancer
1. MySQL : `docker run --name mysql-contrats -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=contrats_db -p 3307:3306 -d mysql:8`
2. Lancer `ContratsServiceApplication`, puis ouvrir http://localhost:8083/actuator/health
