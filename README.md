# CivicScope – API REST

API REST de consultation et de gestion des **régions** et des **élections** du Sénégal, développée avec Spring Boot.

> **Projet académique – EPSI (WEB-TP1, WEB-TP2).** Réalisé dans le cadre du cours de développement web, il met en pratique l'architecture en couches (contrôleur / service / repository / modèle), la conception d'une API REST, la gestion des codes de statut HTTP et la persistance avec Spring Data JPA.

## Technologies

| Outil                  | Version                |
| ---------------------- | ---------------------- |
| Java                   | 21                     |
| Spring Boot            | 4.1.1                  |
| Spring Web MVC         | (starter `webmvc`)     |
| Spring Data JPA        | (starter `data-jpa`)   |
| PostgreSQL (driver)    | runtime                |
| Maven (wrapper `mvnw`) | 3.10.0                 |
| Postman                | tests manuels de l'API |

> Les **élections** sont **stockées dans PostgreSQL** via JPA (entité `Election`, interface `ElectionRepository`). Au démarrage, `spring.jpa.hibernate.ddl-auto=validate` vérifie que l'entité correspond à la table, sans rien modifier : la table et ses données viennent du fichier `civicscope_data.sql` (voir les prérequis). Les **régions** restent **stockées en mémoire** dans la couche service (14 régions pré-chargées).

## Lancer le projet

### Prérequis

- JDK 21
- PostgreSQL en local avec une base `civiscope`
- Le fichier `civicscope_data.sql` importé dans la base `civiscope` (crée la table `election` et ses données, y compris `PRES2019` et `PRES2024`)
- Git

### Étapes

```bash
# 1. Cloner le dépôt
git clone https://github.com/angekevin-agre/civicscope-api-rest.git
cd civicscope-api-rest

# 2. Créer la configuration du profil "local" à partir de l'exemple
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
#    puis renseigner l'utilisateur et le mot de passe PostgreSQL dans ce fichier

# 3. Démarrer l'application
./mvnw spring-boot:run          # Linux / macOS / Git Bash
mvnw.cmd spring-boot:run        # Windows (cmd / PowerShell)
```

L'API est alors disponible sur `http://localhost:8080`. Le profil `local` est activé par défaut (`spring.profiles.active=local` dans `application.properties`).

> `application-local.properties` contient des identifiants : il est **exclu du dépôt** par le `.gitignore`. Seul le fichier `application-local.properties.example` est versionné.

Vérification rapide : `GET http://localhost:8080/ping` renvoie `pong`.

## Routes

### Régions – `/regions`

| Méthode | Route             | Description                 | Codes HTTP                                                                  |
| ------- | ----------------- | --------------------------- | --------------------------------------------------------------------------- |
| GET     | `/regions`        | Liste toutes les régions    | `200`                                                                       |
| GET     | `/regions/{code}` | Détail d'une région         | `200`, `404` si inconnue                                                    |
| POST    | `/regions`        | Crée une région             | `201`, `400` si `code` ou `nom` manquant/vide, `409` si le code existe déjà |
| PUT     | `/regions/{code}` | Modifie le nom d'une région | `200`, `400` si `nom` vide, `404` si inconnue                               |
| DELETE  | `/regions/{code}` | Supprime une région         | `204`, `404` si inconnue                                                    |

### Élections – `/elections`

| Méthode | Route             | Description                                                                                                                                | Codes HTTP                                                                                                                         |
| ------- | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------- |
| GET     | `/elections`      | Liste les élections, de la plus récente à la plus ancienne. Paramètres optionnels : `?annee=2024`, `?type=PRESIDENTIELLE`, `?limit=N`      | `200` (liste vide si aucun résultat), `400` si `type` inconnu, `limit` non numérique ou `limit <= 0`                                |
| GET     | `/elections/{id}` | Détail d'une élection (`id` : UUID)                                                                                                        | `200`, `404` si inconnue                                                                                                           |
| POST    | `/elections`      | Crée une élection (l'`id` est généré)                                                                                                      | `201`, `400` si `code`, `date` ou `typeElection` manquant, `409` si le code existe déjà ou si l'année, le type et le tour existent |
| PUT     | `/elections/{id}` | Modifie une élection                                                                                                                       | `200`, `400` si `code`, `date` ou `typeElection` manquant, `404` si inconnue, `409` si la règle d'unicité est violée                |
| DELETE  | `/elections/{id}` | Supprime une élection                                                                                                                      | `204`, `404` si inconnue                                                                                                           |

**Règles métier**

- `GET /elections?limit=N` limite le nombre de résultats directement dans la requête SQL (tri par `date` décroissante). Sans `limit`, tous les résultats sont renvoyés.
- **Une seule élection par année, type et tour** : créer ou modifier une élection du même `typeElection` et `tourElection` dans la même année qu'une autre renvoie `409`. Par exemple, avec `PRES2024` en base, `PRES2024-BIS` (2024-12-01, `PRESIDENTIELLE`, `PREMIER_TOUR`) est refusée, alors que `PRES2024-T2` (`SECOND_TOUR`) ou `LEG2024` (`LEGISLATIVE`) sont acceptées. Lors d'un `PUT`, l'élection modifiée n'entre pas en conflit avec elle-même.

**Valeurs possibles**

- `typeElection` : `PRESIDENTIELLE`, `LEGISLATIVE`, `MUNICIPALE`, `DEPARTEMENTALE`, `REGIONALE`, `LOCALE`, `REFERENDUM`
- `tourElection` : `TOUR_UNIQUE`, `PREMIER_TOUR`, `SECOND_TOUR`

### Autre

| Méthode | Route   | Description                            | Codes HTTP |
| ------- | ------- | -------------------------------------- | ---------- |
| GET     | `/ping` | Test de disponibilité (renvoie `pong`) | `200`      |

## Exemples de réponses JSON

**`GET /regions/01`** → `200 OK`

```json
{
  "code": "01",
  "nom": "DAKAR"
}
```

**`POST /regions`** avec `{"code": "15", "nom": "TEST"}` → `201 Created`

```json
{
  "code": "15",
  "nom": "TEST"
}
```

**`GET /elections/f41a37a7-1ea7-484f-b629-869f18f9637d`** → `200 OK`

```json
{
  "id": "f41a37a7-1ea7-484f-b629-869f18f9637d",
  "code": "PRES2024",
  "date": "2024-03-24",
  "typeElection": "PRESIDENTIELLE",
  "tourElection": "PREMIER_TOUR",
  "codePays": "SN"
}
```

**`GET /elections?annee=2024`** → `200 OK`

```json
[
  {
    "id": "f41a37a7-1ea7-484f-b629-869f18f9637d",
    "code": "PRES2024",
    "date": "2024-03-24",
    "typeElection": "PRESIDENTIELLE",
    "tourElection": "PREMIER_TOUR",
    "codePays": "SN"
  }
]
```

**`GET /elections?limit=1`** → `200 OK` (l'élection la plus récente)

```json
[
  {
    "id": "f41a37a7-1ea7-484f-b629-869f18f9637d",
    "code": "PRES2024",
    "date": "2024-03-24",
    "typeElection": "PRESIDENTIELLE",
    "tourElection": "PREMIER_TOUR",
    "codePays": "SN"
  }
]
```

**`POST /elections`** → `201 Created` (l'`id` est généré par le serveur)

```json
{
  "id": "0b8f3c1e-5d2a-4c7e-9a41-6f2d8e3b7a10",
  "code": "LEG2022",
  "date": "2022-07-31",
  "typeElection": "LEGISLATIVE",
  "tourElection": "TOUR_UNIQUE",
  "codePays": "SN"
}
```

**Cas d'erreur** (`404`, `409`, `400`) : la réponse est générée par Spring à partir du statut HTTP levé par la couche service (`ResponseStatusException`).

## Structure du projet

```
civicscope-api-rest/
├── postman/                                  # Collections Postman exportées (TP1 et TP2)
├── src/
│   ├── main/
│   │   ├── java/fr/epsi/civicscope/
│   │   │   ├── CivicscopeApplication.java    # Point d'entrée Spring Boot
│   │   │   ├── controller/                   # Couche HTTP (routes, statuts)
│   │   │   │   ├── RegionController.java
│   │   │   │   ├── ElectionController.java
│   │   │   │   └── PingController.java
│   │   │   ├── service/                      # Logique métier et validations
│   │   │   │   ├── RegionService.java
│   │   │   │   └── ElectionService.java
│   │   │   ├── repository/                   # Accès aux données (Spring Data JPA)
│   │   │   │   └── ElectionRepository.java
│   │   │   └── model/                        # Objets métier, entité JPA et énumérations
│   │   │       ├── Region.java
│   │   │       ├── Election.java             # Entité JPA (table election)
│   │   │       ├── TypeElection.java
│   │   │       └── TourElection.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-local.properties.example
│   └── test/java/fr/epsi/civicscope/         # Tests Spring Boot
├── pom.xml
└── mvnw / mvnw.cmd                           # Maven Wrapper
```

## Tests avec Postman

Deux collections se trouvent dans le dossier [`postman/`](postman/) :

- **« CivicScope - AGRE Ange-Kevin »** (TP1, données en mémoire) : 24 requêtes (11 sur les régions, 13 sur les élections) couvrant les cas nominaux et les cas d'erreur (`200`, `201`, `204`, `400`, `404`, `409`). Elle cible les anciennes routes `/elections/{code}`, remplacées par `/elections/{id}` au TP2.
- **« CivicScope TP2 - AGRE Ange-Kevin »** (TP2, élections en base) : un scénario en 10 étapes (11 requêtes) sur `/elections` : liste, lecture par `id` (`200`, `404`), création (`201`), doublon de code et règle « année, type, tour » (`409`), corps invalide (`400`), `?limit=2`, modification, suppression puis relecture (`204`, `404`).

Le code HTTP attendu figure dans le nom de chaque requête.

1. Importer `civicscope_data.sql` dans la base `civiscope`, puis démarrer l'API (`./mvnw spring-boot:run`).
2. Dans Postman : **Import** → sélectionner le fichier du dossier `postman/`.
3. Exécuter les requêtes **dans l'ordre** (ou via le _Collection Runner_) : certaines dépendent des précédentes (par exemple, la création de `LEG2024` avant son doublon, sa modification puis sa suppression).

> Collection TP1 : les données étant en mémoire, redémarrer l'application remet l'API dans son état initial avant de relancer la collection.
> Collection TP2 : les élections sont en base. Le scénario crée puis supprime `LEG2024`, mais en cas d'arrêt en cours de route, supprimer cette élection avant de relancer.

## Tests automatisés

```bash
./mvnw test
```

Le test `CivicscopeApplicationTests` charge le contexte Spring : le profil `local` et une base PostgreSQL accessible sont donc nécessaires.

## Auteur

**AGRE Ange-Kevin** – étudiant à l'EPSI
