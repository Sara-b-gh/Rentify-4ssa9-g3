# utilisateurs-service — Microservice Utilisateurs (Rentify)

Responsable : toute l'équipe

Contexte DDD : Utilisateurs sécurisés par Keycloak (comptes, rôles, permis de conduire) — classe centrale référencée par ID

- Port : **8087**
- Base MySQL : **utilisateurs_db** (créée automatiquement)
- Sécurité : **Keycloak** (voir GUIDE_KEYCLOAK.md) — token JWT obligatoire sauf endpoints publics
- Identifiants : tous de type `Long` (auto-incrément)
- Références vers les autres services : **par identifiant uniquement** (aucune classe partagée)
- Swagger UI : http://localhost:8087/swagger-ui.html
- Santé : http://localhost:8087/actuator/health

## Lancer

1. Démarrer **MySQL** dans XAMPP.
2. Ouvrir le projet dans IntelliJ (JDK 21), recharger Maven.
3. Lancer `UtilisateursServiceApplication`.
   - Sans MySQL : variable d'environnement `SPRING_PROFILES_ACTIVE=h2`.
4. Ouvrir Swagger.

## Endpoints

| Méthode | URL | Rôle | Codes |
|---|---|---|---|
| GET | `/api/utilisateurs` | Liste des utilisateurs | 200 |
| GET | `/api/utilisateurs/{id}` | Détail | 200 / 404 |
| POST | `/api/utilisateurs` | Création | 201 / 400 / 404 / 409 / 503 |
| PUT | `/api/utilisateurs/{id}` | Modification | 200 / 400 / 404 / 409 |
| DELETE | `/api/utilisateurs/{id}` | Suppression | 204 / 404 |
| GET | `/api/utilisateurs/moi` | Mon profil (token requis) | 200 / 401 / 404 |
| POST | `/api/utilisateurs/inscription` | Inscription publique d'un client (rôle CLIENT) | 201 / 400 / 409 / 503 |
| PUT | `/api/utilisateurs/{id}/role?role=AGENT` | Changer le rôle (ADMIN) | 200 / 404 |
| PUT | `/api/utilisateurs/{id}/desactiver` | Désactiver le compte (ADMIN) | 200 / 404 / 409 |
| GET | `/api/permis` | Liste des permiss | 200 |
| GET | `/api/permis/{id}` | Détail | 200 / 404 |
| POST | `/api/permis` | Création | 201 / 400 / 404 / 409 / 503 |
| PUT | `/api/permis/{id}` | Modification | 200 / 400 / 404 / 409 |
| DELETE | `/api/permis/{id}` | Suppression | 204 / 404 |
| GET | `/api/permis/par-utilisateur/{utilisateurId}` | Permis d'un utilisateur (utilisé par Contrats) | 200 / 404 |

## Exemples de corps JSON

**POST /api/utilisateurs**
```json
{
  "nom": "Ben Ali",
  "prenom": "Sara",
  "email": "sara@exemple.tn",
  "telephone": "20123456",
  "role": "CLIENT",
  "motDePasse": "secret123"
}
```

**POST /api/permis**
```json
{
  "utilisateurId": 1,
  "numero": "P-123456",
  "dateObtention": "2018-05-10",
  "dateExpiration": "2030-05-10"
}
```

## Scénario de test (Swagger)

1. Suivre **GUIDE_KEYCLOAK** : démarrer Keycloak (port 8180), importer `keycloak/rentify-realm.json`, obtenir un token ADMIN, le coller dans **Authorize** de Swagger.
2. Sans token : POST `/api/utilisateurs` → 401 ; avec un token CLIENT → 403.
3. Avec le token ADMIN : POST `/api/utilisateurs` (Sara Ben Ali, CLIENT, motDePasse) → 201 ; `keycloakId` rempli ; le compte apparaît dans la console Keycloak.
4. POST `/api/utilisateurs` avec le même email → 409 ; avec un email invalide (`abc`) → 400 ; sans motDePasse → 400.
5. Se connecter avec le compte de Sara (token) puis GET `/api/utilisateurs/moi` → son profil.
6. PUT `/api/utilisateurs/1/role?role=AGENT` → 200 (rôle changé aussi dans Keycloak).
7. POST `/api/permis` (utilisateurId 1, expiration 2030) → 201, `valide` vaut true ; refaire → 409.
8. PUT `/api/utilisateurs/1/desactiver` → 200 ; Sara ne peut plus se connecter ; refaire → 409.
9. Arrêter Keycloak puis POST `/api/utilisateurs` → 503.

## Tests unitaires

`mvn test` (ou IntelliJ : clic droit sur `src/test` puis Run).

## Pousser sur Git

```
git pull
git add Backend/utilisateurs-service
git commit -m "feat(utilisateurs): microservice Utilisateurs"
git push
```
