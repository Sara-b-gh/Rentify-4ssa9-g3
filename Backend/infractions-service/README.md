# infractions-service (Rentify)

Microservice **Infractions** : Spring Boot 3.3, Java 17, Maven, MySQL, RabbitMQ, OpenFeign + Resilience4j, Eureka.
Responsable : Nourane Chargui (Membre 4).

## Rôle
Déclare les infractions (amendes) reçues pour un véhicule et identifie automatiquement le conducteur
à partir du contrat actif à la date de l'infraction. Il ne stocke que les identifiants du véhicule,
du contrat et du conducteur (les données appartiennent à Flotte et à Contrats).

## Organisation du code
| Dossier | Contenu |
|---|---|
| `domain` | `Infraction` (racine d'agrégat), `IdentificationConducteur` (0..1), `StatutInfraction` |
| `repository` | `InfractionRepository` (base `infractions_db`) |
| `service` | `InfractionService` : déclarer, identifier, relancer, corriger |
| `web` | `InfractionController` : API REST |
| `client` | `FlotteClient`, `ContratsClient` (OpenFeign) + fallbacks Resilience4j |
| `event` | `ConducteurIdentifie`, `InfractionEventPublisher`, `RabbitConfig` |
| `dto` | requêtes, réponses, objets reçus de Flotte et Contrats |
| `exception` | erreurs → codes HTTP (400, 404, 409, 503) |
| `mock` | faux Flotte et faux Contrats (profil `local`) pour tester seul |

## Modèle de données (base `infractions_db`)
**Infraction** (racine d'agrégat) : id (UUID), immatriculation, vehiculeId (UUID, Flotte), dateHeure, lieu, montant,
statut (DECLAREE, CONDUCTEUR_IDENTIFIE, NON_IMPUTABLE).
**IdentificationConducteur** (0..1 par infraction) : id, contratId (UUID, Contrats), conducteurId (userId), dateIdentification.

## Lancer
- Avec MySQL : `mvn spring-boot:run`
- Sans MySQL : profil `h2`
- Seule, avec les faux Flotte et Contrats : profils `h2,local` (VM option `-Dspring.profiles.active=h2,local`)

Port : **8084** · Santé : http://localhost:8084/actuator/health · Swagger : http://localhost:8084/swagger-ui.html

## API REST
| Méthode | URL | Description | Codes |
|---|---|---|---|
| POST | /api/infractions | Déclarer et identifier le conducteur | 201, 400, 404, 503 |
| GET | /api/infractions?statut= | Lister les infractions | 200 |
| GET | /api/infractions/{id} | Détail | 200, 404 |
| GET | /api/infractions/{id}/conducteur | Conducteur identifié | 200, 404 |
| POST | /api/infractions/{id}/identification | Relancer l'identification | 200, 404, 409, 503 |
| PATCH | /api/infractions/{id}/conducteur | Corriger le conducteur manuellement | 200, 400, 404 |

Exemple :
```
POST /api/infractions
{"immatriculation":"123TU4567","dateHeure":"2026-11-10T14:30:00","lieu":"Avenue Habib Bourguiba, Tunis","montant":60}
```

## Interactions
- **Appelle Flotte** (synchrone, OpenFeign) : `GET /api/vehicules/par-immatriculation/{plaque}` → vehiculeId.
  Plaque inconnue → 404 ; Flotte en panne → 503 (rien n'est enregistré).
- **Appelle Contrats** (synchrone, OpenFeign) : `GET /api/contrats/actif?vehiculeId=&dateHeure=` → contrat et conducteurs.
  Aucun contrat → NON_IMPUTABLE ; Contrats en panne → l'infraction est enregistrée en DECLAREE, on relance plus tard.
- **Publie** (RabbitMQ, échange `rentify.events`, clé `infraction.conducteur-identifie`) : `ConducteurIdentifie`
  `{infractionId, contratId, conducteurId, montant}` → Facturation crée la pénalité.
  RabbitMQ arrêté → l'infraction reste enregistrée, l'échec est journalisé.

## Tester avec le profil `local` (faux services)
| Plaque | Résultat |
|---|---|
| 123TU4567 (date en novembre 2026) | CONDUCTEUR_IDENTIFIE (conducteur ahmed-001) |
| 200TU1000 | NON_IMPUTABLE (voiture non louée) |
| 999TU9999 | DECLAREE (Contrats en panne simulée) |
| autre | 404 (véhicule inconnu) |

Tests unitaires : `mvn test` (règles métier de `Infraction`).
