# Rentify-4ssa9-g3

Rentify – Plateforme de gestion et de location de véhicules basée sur une architecture microservices. L’application permet de gérer la flotte de véhicules, les réservations, les contrats de location, les infractions, les paiements et la maintenance.

---

## Architecture Globale
Le système est composé de plusieurs microservices indépendants, communiquant via REST (OpenFeign) et des événements asynchrones (RabbitMQ).

* **Point d'entrée :** API Gateway (Spring Cloud Gateway)
* **Découverte et Configuration :** Netflix Eureka & Config Server
* **Sécurité :** Keycloak (OAuth2, JWT, Rôles: CLIENT, AGENT, ADMIN)
* **Base de données :** MySQL (Une base distincte par microservice)
* **Frontend :** Angular

---

## Les Microservices & Équipe

| Microservice | Résponsabilité | Base de données | Responsable |
| :--- | :--- | :--- | :--- |
| **Flotte** | Véhicules, catégories, disponibilité | MySQL | Membre 1 |
| **Réservation** | Réservations, modification, annulation | MySQL | Membre 2 |
| **Contrats** | Contrats, conducteurs, états des lieux | MySQL | Membre 3 |
| **Infractions** | Gestion des infractions et conducteurs | MySQL | Membre 4 |
| **Facturation** | Factures, paiements, pénalités | MySQL | Membre 5 |
| **Maintenance** | Entretien, réparations et interventions | MySQL | Membre 6 |
| **Utilisateurs** | Profils, permis, rôles (Keycloak) | MySQL | Toute l'équipe |

---

## Lancement du Projet (Docker Compose)
Pour lancer l'infrastructure complète et l'ensemble des services :

```bash
docker-compose up --build
