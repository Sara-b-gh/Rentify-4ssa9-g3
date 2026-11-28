# Maintenance Service (`maintenance-service`) — Membre 6

Microservice de gestion de la maintenance et des reparations des vehicules pour **Rentify**.

## Informations techniques
- **Port** : `8086`
- **Base de donnees MySQL** : `maintenance_db`
- **Java / Framework** : Java 21, Spring Boot
- **Identifiants** : `Long` (`id`, `vehiculeId`, `technicienId`)
- **Appel synchrone** : `flotte-service` (`http://localhost:8081`)

## Endpoints principaux
- `POST /api/interventions` : Planifier une intervention (`201 Created`, `400`, `404`, `409`, `503`)
- `GET /api/interventions?vehiculeId=` : Lister les interventions d'un vehicule (`200 OK`)
- `PATCH /api/interventions/{id}/terminer?cout=` (et `PUT`) : Terminer une intervention avec le cout reel (`200 OK`, `400`, `404`, `409`)

## Scenario de test (Swagger / Postman)
1. Demarrer MySQL (XAMPP), Keycloak (`8180`), `utilisateurs-service` (`8087`), `flotte-service` (`8081`) et `maintenance-service` (`8086`).
2. Ouvrir `http://localhost:8086/swagger-ui.html`.
3. Appeler `POST /api/interventions` avec :
```json
{
  "vehiculeId": 1,
  "technicienId": 2,
  "type": "Vidange",
  "dateDebut": "2026-12-10",
  "dateFin": "2026-12-11"
}
