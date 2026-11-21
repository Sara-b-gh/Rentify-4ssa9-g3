---
title: "Guide Keycloak — microservice Utilisateurs"
---

# 1. Rôle de Keycloak dans Rentify

- **Keycloak** gère l'authentification : login, mot de passe, rôles (CLIENT, AGENT, ADMIN) et émission du **token JWT**.
- Le **microservice Utilisateurs** valide ce token sur chaque requête, crée/désactive les comptes dans Keycloak (API d'administration) et garde le **profil métier** (nom, téléphone, permis) dans `utilisateurs_db`.
- Le lien entre les deux est `keycloakId` (le champ `sub` du token).
- Les autres microservices continuent de référencer l'utilisateur **par son ID** (`clientId`...).

Schéma : Client → (login) → **Keycloak** → token JWT → Client → (Bearer token) → **Utilisateurs** → vérifie la signature avec Keycloak.

# 2. Installer et démarrer Keycloak (Windows)

1. Télécharger le zip « Keycloak » sur keycloak.org/downloads (version 24 ou plus récente) et le dézipper dans `C:\keycloak`.
2. Créer le dossier `C:\keycloak\data\import` et y copier le fichier **`keycloak/rentify-realm.json`** du projet `utilisateurs-service`.
3. Ouvrir une invite de commandes (cmd) puis :

```
cd C:\keycloak\bin
set KC_BOOTSTRAP_ADMIN_USERNAME=admin
set KC_BOOTSTRAP_ADMIN_PASSWORD=admin
set KEYCLOAK_ADMIN=admin
set KEYCLOAK_ADMIN_PASSWORD=admin
kc.bat start-dev --http-port=8180 --import-realm
```

4. Attendre le message « Listening on: http://0.0.0.0:8180 ». Laisser cette fenêtre ouverte.
5. Ouvrir http://localhost:8180 (compte `admin` / `admin`). Si Keycloak demande de créer un administrateur, le faire.

Le port **8180** évite les conflits avec les autres outils (XAMPP, services Spring).

# 3. Ce que l'import crée (realm `rentify`)

| Élément | Valeur |
|---|---|
| Realm | `rentify` (sélecteur en haut à gauche de la console) |
| Rôles | `CLIENT`, `AGENT`, `ADMIN` |
| Client de test | `rentify-swagger` (public, permet de demander un token avec login et mot de passe) |
| Client du service | `utilisateurs-service` (secret `rentify-secret`, compte de service autorisé à gérer les utilisateurs) |
| Administrateur de test | `admin@rentify.tn` / `admin123` (rôle ADMIN) |

À vérifier dans la console : *Realm roles*, *Clients*, *Users*.

# 4. Obtenir un token (Postman)

- Méthode : **POST** `http://localhost:8180/realms/rentify/protocol/openid-connect/token`
- Body : **x-www-form-urlencoded**

| Clé | Valeur |
|---|---|
| grant_type | password |
| client_id | rentify-swagger |
| username | admin@rentify.tn |
| password | admin123 |

Copier la valeur de `access_token` dans la réponse. Le token expire au bout de **5 minutes** : en redemander un au besoin.

# 5. Utiliser le token dans Swagger

1. Lancer MySQL, Keycloak, puis `UtilisateursServiceApplication`.
2. Ouvrir http://localhost:8087/swagger-ui.html
3. Cliquer sur **Authorize**, coller **uniquement** le token (sans le mot « Bearer »), valider.
4. Les requêtes suivantes portent le token automatiquement.

# 6. Qui a le droit de faire quoi

| Endpoint | Accès |
|---|---|
| `POST /api/utilisateurs/inscription` | public (crée un CLIENT) |
| `GET /api/utilisateurs/{id}`, `GET /api/permis/par-utilisateur/{id}` | lecture interne (appelée par les autres services), à protéger via la Gateway plus tard |
| `GET /api/utilisateurs/moi` | tout utilisateur connecté |
| `GET /api/utilisateurs`, `/api/permis/**` | AGENT ou ADMIN |
| `POST/PUT/DELETE /api/utilisateurs/**` | ADMIN |
| Swagger, `/actuator/health` | public |

Codes : **401** token absent ou expiré, **403** rôle insuffisant.

# 7. Test de bout en bout du service

1. Sans token : `POST /api/utilisateurs` → 401.
2. Token ADMIN : `POST /api/utilisateurs` (Sara, CLIENT, motDePasse `secret123`) → 201 ; le compte apparaît dans Keycloak (*Users*).
3. Demander un token pour `sara@exemple.tn` / `secret123` : `GET /api/utilisateurs/moi` → son profil ; `POST /api/utilisateurs` → **403**.
4. Token ADMIN : `PUT /api/utilisateurs/1/role?role=AGENT` → 200 (rôle changé dans Keycloak aussi).
5. `PUT /api/utilisateurs/1/desactiver` → 200 ; Sara ne peut plus obtenir de token.
6. Arrêter Keycloak puis `POST /api/utilisateurs` → **503**.

# 8. Problèmes fréquents

| Symptôme | Solution |
|---|---|
| 401 alors que le token est récent | Le token vient d'un autre realm ou d'une autre adresse : utiliser `localhost` (pas `127.0.0.1`) et le realm `rentify`. |
| 503 « Keycloak refuse le client » | Vérifier le secret `rentify-secret` et, dans *Clients > utilisateurs-service > Service account roles*, les rôles `manage-users`, `view-users`, `query-users`, `view-realm`. |
| 503 « Keycloak est indisponible » | Keycloak n'est pas démarré ou le port n'est pas 8180. |
| Le realm `rentify` n'apparaît pas | L'import se fait au démarrage : vérifier le dossier `data\import`. Si le realm existait déjà, le supprimer puis redémarrer. |
| Le service ne démarre pas (issuer) | Il contacte Keycloak à la première requête ; lancer Keycloak avant de tester. |

# 9. À dire à l'oral

« Keycloak est notre serveur d'identité : il gère les comptes, les mots de passe et les rôles, et délivre un token JWT. Le microservice Utilisateurs est un resource server : il valide le token et autorise chaque endpoint selon le rôle. Il crée les comptes dans Keycloak via son API d'administration et garde seulement le profil métier, lié par keycloakId. Les autres microservices ne connaissent l'utilisateur que par son ID. »
