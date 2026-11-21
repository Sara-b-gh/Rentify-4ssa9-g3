# facturation-service

Microservice **Facturation** de la plateforme Rentify (Membre 5).

## Responsabilités
- Émettre une facture à la clôture d'un contrat
- Enregistrer les paiements d'une facture (paiement partiel ou total)
- Rembourser une facture payée
- Ajouter une pénalité à la facture quand un conducteur est identifié pour une infraction

## Contexte borné
Facturation ne connaît que des identifiants des autres services (`contratId`, `vehiculeId`, `infractionId`, `conducteurId`), jamais leurs entités. Elle garde ses propres données et reste autonome.

## Technologies
- Java 17, Spring Boot 3.3.5
- Spring Data JPA, base MySQL (profil `h2` pour démarrer sans MySQL)
- RabbitMQ (événements asynchrones)
- OpenFeign + Resilience4j (appel vers Flotte)
- Eureka Client, Spring Cloud Config

## Base de données
Une base **par service** : `facturation_db` (MySQL).
- Une base relationnelle convient à des données financières qui exigent des transactions et de la cohérence : facture, paiements et pénalités sont liés.
- Une base propre au service évite le couplage avec les autres services : aucun accès direct aux tables des autres.
- Le profil `h2` (base en mémoire) permet de démarrer sans MySQL pour les tests.

## Modèle de données
- **Facture** : id, contratId, montantLocation, montantTotal, dateEmission, statut. La méthode `calculerTotal()` renvoie montantLocation + somme des pénalités.
    - Statuts : `EMISE`, `PARTIELLEMENT_PAYEE`, `PAYEE`, `REMBOURSEE`
- **Paiement** : id, montant, mode, datePaiement (une facture a plusieurs paiements)
- **Pénalité** : id, infractionId, conducteurId, montant (une facture a plusieurs pénalités)

## API REST (port 8085)

| Méthode | Endpoint | Description | Codes |
|---|---|---|---|
| GET | `/api/factures/{id}` | Détail d'une facture | 200, 404 |
| GET | `/api/factures/par-contrat/{contratId}` | Facture d'un contrat | 200, 404 |
| POST | `/api/factures/{id}/paiements` | Enregistrer un paiement | 201, 400, 404, 409 |
| POST | `/api/factures/{id}/remboursement` | Rembourser | 200, 404, 409 |

- **400** : montant négatif ou mode de paiement manquant
- **404** : facture inconnue
- **409** : facture déjà payée ou remboursée, montant supérieur au reste à payer, remboursement sans paiement

## Interactions

| Sens | Service | Mode | Données | Justification |
|---|---|---|---|---|
| Consomme | Contrats | Asynchrone (RabbitMQ) | `ContratCloture` | Facturation n'a pas à bloquer le service Contrats |
| Consomme | Infractions | Asynchrone (RabbitMQ) | `ConducteurIdentifie` | Même raison : les services restent découplés |
| Appelle | Flotte | Synchrone (Feign) | tarif du véhicule | Le tarif est nécessaire tout de suite pour calculer la facture |

Facturation ne publie aucun événement.

### Contrats d'échange
- Exchange RabbitMQ : `rentify.events`
- `contrat.cloture` : `contratId`, `vehiculeId`, `dateDebut`, `dateFin`
- `infraction.conducteur-identifie` : `infractionId`, `contratId`, `conducteurId`, `montant`
- Flotte (tarif) : appel `GET /api/vehicules/{id}/tarif` qui renvoie `tarifJournalier`. **À valider avec le membre Flotte** (le chemin et le champ peuvent évoluer).

## Cas de panne
- **Flotte indisponible** : le circuit breaker déclenche un fallback qui lève `TarifIndisponibleException`. Aucune facture incorrecte n'est créée. Le message RabbitMQ est rejoué (3 essais), donc il n'est pas perdu. Les lectures (`GET`) et les paiements continuent de fonctionner, car ils n'appellent pas Flotte.
- **RabbitMQ arrêté** : les événements restent dans la file et sont traités au retour du broker.
- **Événement reçu deux fois** : aucun doublon (une seule facture par contrat, une seule pénalité par infraction).

## Lancer le service
1. Avec MySQL : lancer MySQL (la base `facturation_db` est créée automatiquement), puis `FacturationServiceApplication`.
2. Sans MySQL : profil `h2` (`-Dspring.profiles.active=h2`). Une facture de test est alors créée pour le contrat `11111111-1111-1111-1111-111111111111`.
3. Le fichier `requests.http` contient les requêtes de test des 4 endpoints.

## Tests
- `FactureServiceTest` : règles métier (paiement partiel ou total, erreurs 404 et 409, remboursement, pénalités, doublons)
- `FactureEventListenerTest` : calcul du montant avec le tarif de Flotte et cas de panne de Flotte