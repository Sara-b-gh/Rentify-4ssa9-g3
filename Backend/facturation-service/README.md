# facturation-service

Microservice **Facturation** de la plateforme Rentify (Membre 5).

## Responsabilités
- Émettre une facture à la clôture d'un contrat
- Enregistrer les paiements d'une facture
- Rembourser une facture
- Enregistrer les pénalités liées aux infractions

## Technologies
- Java 17, Spring Boot 3.3.5
- Spring Data JPA, base MySQL (profil `h2` pour démarrer sans MySQL)
- RabbitMQ (événements asynchrones)
- OpenFeign + Resilience4j (appel vers Flotte)
- Eureka Client, Spring Cloud Config

## Modèle de données
- **Facture** : id, contratId, montantTotal, dateEmission, statut (`calculerTotal()`)
- **Paiement** : id, montant, mode, datePaiement (une facture a plusieurs paiements)
- **Pénalité** : id, infractionId, conducteurId, montant (une facture a plusieurs pénalités)

## API REST

| Méthode | Endpoint | Description | Codes |
|---|---|---|---|
| GET | `/api/factures/{id}` | Détail d'une facture | 200, 404 |
| GET | `/api/factures/par-contrat/{contratId}` | Facture d'un contrat | 200, 404 |
| POST | `/api/factures/{id}/paiements` | Enregistrer un paiement | 201, 400, 404, 409 |
| POST | `/api/factures/{id}/remboursement` | Rembourser | 200, 404, 409 |

## Interactions
- **Consomme (asynchrone)** : `ContratCloture` (Contrats) et `ConducteurIdentifie` (Infractions)
- **Publie** : aucun événement
- **Appelle (synchrone)** : Flotte, pour obtenir le tarif

## Lancer le service
1. Lancer MySQL, ou utiliser le profil `h2` (Active profiles : `h2`).
2. Lancer `FacturationServiceApplication`.