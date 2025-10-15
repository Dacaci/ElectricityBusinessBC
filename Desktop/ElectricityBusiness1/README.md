# 🔌 Electricity Business - Documentation

**Electricity Business** est une plateforme web de réservation de bornes de recharge électrique entre particuliers.

## 📋 Table des matières

- [Présentation](#présentation)
- [Fonctionnalités](#fonctionnalités)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [Architecture technique](#architecture-technique)
- [API Documentation](#api-documentation)
- [Tests](#tests)
- [Contribution](#contribution)

## 🎯 Présentation

Electricity Business permet aux particuliers de :
- **Louer** leurs bornes de recharge électrique
- **Réserver** des bornes chez d'autres particuliers
- **Gérer** leurs réservations et leurs équipements

## ✨ Fonctionnalités

### Authentification
- ✅ Inscription avec validation par email
- ✅ Connexion sécurisée avec JWT
- ✅ Déconnexion

### Gestion des lieux
- ✅ Ajouter un lieu de recharge
- ✅ Modifier un lieu existant
- ✅ Supprimer un lieu (si aucune borne associée)

### Gestion des bornes
- ✅ Ajouter une borne de recharge (Type 2S)
- ✅ Modifier les informations d'une borne
- ✅ Définir les tarifs horaires
- ✅ Supprimer une borne (si aucune réservation)

### Réservations
- ✅ Effectuer une réservation
- ✅ Confirmer une réservation (propriétaire)
- ✅ Refuser une réservation (propriétaire)
- ✅ Annuler une réservation
- ✅ Marquer une réservation comme terminée
- ✅ Voir les réservations en cours
- ✅ Voir les réservations passées

### Fonctionnalités avancées
- ✅ Carte interactive pour trouver des bornes disponibles
- ✅ Recherche de bornes par localisation
- ✅ Génération de reçus PDF
- ✅ Export Excel des réservations
- ✅ Filtrage et recherche avancée

## 🚀 Installation

### Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- Docker et Docker Compose
- PostgreSQL 15+
- Tomcat 10+

### Étapes d'installation

1. **Cloner le projet**
```bash
git clone <url-du-repo>
cd ElectricityBusiness1
```

2. **Démarrer les services Docker**
```bash
docker-compose up -d
```

Cela démarre :
- PostgreSQL (port 5432)
- MailHog pour les emails (port 1025/8025)
- Tomcat pour le frontend (port 8081)

3. **Compiler et lancer le backend**
```bash
cd backend/eb-backend
mvn clean package
java -jar target/eb-backend-0.0.1-SNAPSHOT.jar
```

Le backend démarre sur `http://localhost:8080`

4. **Déployer le frontend**
```bash
cd signup-plain
mvn clean package
# Le WAR est généré dans target/signup-plain.war
# Copier dans Tomcat ou utiliser Docker
```

## 📱 Utilisation

### Accès à l'application

- **Frontend** : `http://localhost:8081/signup-plain/`
- **API Backend** : `http://localhost:8080/api`
- **Documentation API (Swagger)** : `http://localhost:8080/swagger-ui.html`
- **MailHog** : `http://localhost:8025`

### Guide utilisateur

#### 1. Inscription

1. Accédez à la page d'inscription
2. Remplissez le formulaire avec vos informations :
   - Nom, prénom
   - Email (validation requise)
   - Téléphone
   - Date de naissance (18 ans minimum)
   - Adresse complète
   - Mot de passe (8 caractères min, 1 majuscule, 1 chiffre)
3. Validez et consultez vos emails
4. Cliquez sur le lien de validation

#### 2. Connexion

1. Utilisez votre email et mot de passe
2. Vous êtes redirigé vers votre tableau de bord

#### 3. Ajouter un lieu de recharge

1. Dans le menu, cliquez sur "📍 Lieux"
2. Cliquez sur "➕ Nouveau lieu"
3. Remplissez :
   - Libellé du lieu
   - Adresse complète
   - Coordonnées GPS (latitude/longitude)
   - Description (optionnel)
4. Validez

#### 4. Ajouter une borne

1. Dans le menu, cliquez sur "🔌 Bornes"
2. Cliquez sur "➕ Nouvelle borne"
3. Remplissez :
   - Nom de la borne
   - Lieu associé
   - Tarif horaire (€/heure)
   - Type de prise (Type 2S)
4. Validez

#### 5. Effectuer une réservation

**Option 1 : Via la carte**
1. Cliquez sur "🗺️ Carte"
2. Autorisez la géolocalisation
3. Cliquez sur une borne sur la carte
4. Cliquez sur "Réserver"

**Option 2 : Via le menu**
1. Cliquez sur "📅 Réservations"
2. Cliquez sur "➕ Nouvelle réservation"
3. Sélectionnez la borne
4. Choisissez date et heure de début/fin
5. Validez

#### 6. Gérer les réservations

**En tant que locataire :**
- Voir vos réservations
- Annuler une réservation
- Télécharger le reçu PDF

**En tant que propriétaire :**
- Voir les demandes de réservation
- Confirmer ou refuser une réservation
- Marquer une réservation comme terminée

#### 7. Exporter les données

1. Dans "📅 Réservations"
2. Utilisez les filtres (statut, période)
3. Cliquez sur "Export Excel" pour télécharger

## 🏗️ Architecture technique

### Backend (Spring Boot)

```
backend/eb-backend/
├── src/main/java/com/eb/eb_backend/
│   ├── config/          # Configuration Spring Security, OpenAPI
│   ├── controller/      # Contrôleurs REST
│   ├── dto/            # Data Transfer Objects
│   ├── entity/         # Entités JPA
│   ├── repository/     # Repositories JPA
│   ├── security/       # JWT, Filtres de sécurité
│   └── service/        # Logique métier
└── src/main/resources/
    ├── application.yml # Configuration
    └── db/migration/   # Scripts Flyway
```

### Frontend (JSP/Servlet)

```
signup-plain/src/main/webapp/
├── css/               # Feuilles de style
├── js/                # Scripts JavaScript
├── *.jsp             # Pages JSP
└── WEB-INF/          # Configuration web.xml
```

### Base de données

- **PostgreSQL** avec migrations Flyway
- Tables principales :
  - `users` : Utilisateurs
  - `locations` : Lieux de recharge
  - `stations` : Bornes de recharge
  - `reservations` : Réservations

## 📚 API Documentation

### Endpoints principaux

#### Authentification
```
POST   /api/auth/register    - Inscription
POST   /api/auth/login       - Connexion
POST   /api/auth/verify      - Validation email
POST   /api/auth/refresh     - Rafraîchir token JWT
```

#### Utilisateurs
```
GET    /api/users            - Liste des utilisateurs
GET    /api/users/{id}       - Détails d'un utilisateur
PUT    /api/users/{id}       - Modifier un utilisateur
DELETE /api/users/{id}       - Supprimer un utilisateur
```

#### Lieux
```
GET    /api/locations        - Liste des lieux
POST   /api/locations        - Créer un lieu
GET    /api/locations/{id}   - Détails d'un lieu
PUT    /api/locations/{id}   - Modifier un lieu
DELETE /api/locations/{id}   - Supprimer un lieu
```

#### Bornes
```
GET    /api/stations         - Liste des bornes
POST   /api/stations         - Créer une borne
GET    /api/stations/{id}    - Détails d'une borne
PUT    /api/stations/{id}    - Modifier une borne
DELETE /api/stations/{id}    - Supprimer une borne
GET    /api/stations/map     - Toutes les bornes (pour carte)
GET    /api/stations/nearby  - Bornes proches (lat, lng, radius)
```

#### Réservations
```
GET    /api/reservations              - Liste des réservations
POST   /api/reservations              - Créer une réservation
GET    /api/reservations/{id}         - Détails d'une réservation
PUT    /api/reservations/{id}/confirm - Confirmer une réservation
PUT    /api/reservations/{id}/cancel  - Annuler une réservation
PUT    /api/reservations/{id}/complete- Terminer une réservation
DELETE /api/reservations/{id}         - Supprimer une réservation
GET    /api/reservations/{id}/receipt.pdf - Télécharger reçu PDF
GET    /api/reservations/export.xlsx  - Export Excel
```

### Swagger UI

Documentation interactive disponible sur :
`http://localhost:8080/swagger-ui.html`

## 🧪 Tests

### Tests unitaires

```bash
cd backend/eb-backend
mvn test
```

### Tests d'intégration

```bash
mvn verify
```

## 🤝 Contribution

### Structure de commit

```
feat: Nouvelle fonctionnalité
fix: Correction de bug
docs: Documentation
style: Formatage
refactor: Refactoring
test: Tests
chore: Maintenance
```

### Workflow

1. Créer une branche : `git checkout -b feature/ma-fonctionnalite`
2. Commiter : `git commit -m "feat: description"`
3. Pusher : `git push origin feature/ma-fonctionnalite`
4. Créer une Pull Request

## 📝 Licence

Projet académique - Tous droits réservés

## 👥 Auteurs

- **Développeur Backend** : API REST Spring Boot
- **Développeur Frontend** : Interface JSP/Servlet
- **DBA** : Base de données PostgreSQL

## 📞 Support

Pour toute question :
- Email : support@electricitybusiness.com
- Documentation : [Wiki du projet](#)
- Issues : [GitHub Issues](#)

---

**Version** : 1.0.0
**Date** : Octobre 2025








