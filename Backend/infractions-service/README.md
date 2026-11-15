# infractions-service (Rentify)

Microservice **Infractions** : Spring Boot 3.3, Java 17, Maven, MySQL, RabbitMQ, OpenFeign + Resilience4j, Eureka.
Responsable : Nourane Chargui (Membre 4).

## Rôle
Déclare les infractions (amendes) reçues pour un véhicule et identifie automatiquement le conducteur
à partir du contrat actif à la date de l'infraction. Il ne stocke que les identifiants du véhicule,
du contrat et du conducteur (les données appartiennent à Flotte et à Contrats).

## Modèle de données (base `infractions_db`)
**Infraction** (racine d'agrégat, table `infractions`)

| Champ | Type | Description |
|---|---|---|
| id | UUID | identifiant de l'infraction |
| immatriculation | texte | plaque lue sur l'avis |
| vehiculeId | UUID | identifiant du véhicule (service Flotte) |
| dateHeure | date-heure | moment de l'infraction |
| lieu | texte | lieu de l'infraction |
| montant | nombre | montant de l'amende |
| statut | texte | DECLAREE, CONDUCTEUR_IDENTIFIE, NON_IMPUTABLE |

**IdentificationConducteur** (entité de l'agrégat, 0..1 par infraction, table `identifications_conducteur`)

| Champ | Type | Description |
|---|---|---|
| id | UUID | identifiant |
| contratId | UUID | contrat actif à la date (service Contrats) |
| conducteurId | texte | identifiant du conducteur |
| dateIdentification | date-heure | moment de l'identification |

## Lancer
1. Avec MySQL (la base `infractions_db` est créée automatiquement) : `mvn spring-boot:run`
2. Sans rien installer (base H2 en mémoire) : `mvn spring-boot:run -Dspring-boot.run.profiles=h2`

Port : **8084** · Santé : http://localhost:8084/actuator/health · Swagger : http://localhost:8084/swagger-ui.html

## API REST prévue
| Méthode | URL | Description | Codes |
|---|---|---|---|
| POST | /api/infractions | Déclarer et identifier le conducteur | 201, 400, 404 |
| GET | /api/infractions/{id}/conducteur | Conducteur identifié | 200, 404 |
| POST | /api/infractions/{id}/identification | Relancer l'identification | 200, 404, 409 |
| PATCH | /api/infractions/{id}/conducteur | Corriger le conducteur manuellement | 200, 400, 404 |

## Interactions
- **Appelle Flotte** (synchrone, OpenFeign) : `GET /api/vehicules/par-immatriculation/{plaque}` → vehiculeId.
- **Appelle Contrats** (synchrone, OpenFeign) : `GET /api/contrats/actif?vehiculeId=&dateHeure=` → contrat et conducteur.
  Si Contrats ne répond pas : l'infraction reste DECLAREE, l'identification est relancée plus tard.
- **Publie** (RabbitMQ, échange `rentify.events`, clé `infraction.conducteur-identifie`) : `ConducteurIdentifie`
  `{infractionId, contratId, conducteurId, montant}` → Facturation crée la pénalité.
