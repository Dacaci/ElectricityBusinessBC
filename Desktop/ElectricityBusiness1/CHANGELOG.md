# 📝 Changelog - Electricity Business

Toutes les modifications notables du projet sont documentées ici.

## [1.0.0] - 2025-10-08

### ✨ Ajouts

**Backend (API REST)**
- ✅ API REST complète avec Spring Boot 3.2.0
- ✅ Authentification JWT avec Spring Security
- ✅ Gestion CRUD des utilisateurs, lieux, bornes et réservations
- ✅ Génération de reçus PDF avec Apache PDFBox
- ✅ Export Excel des réservations avec Apache POI
- ✅ Documentation Swagger/OpenAPI interactive
- ✅ Base de données PostgreSQL avec migrations Flyway
- ✅ Recherche de bornes par géolocalisation (formule de Haversine)
- ✅ Filtrage et recherche avancée

**Frontend (JSP/Servlet)**
- ✅ Interface utilisateur complète en JSP
- ✅ Carte interactive avec Leaflet.js
- ✅ Pages de gestion pour lieux, bornes et réservations
- ✅ Système d'authentification intégré
- ✅ Design responsive et moderne
- ✅ Messages d'erreur et validations

**Fonctionnalités métier**
- ✅ Inscription avec validation par email
- ✅ Connexion/Déconnexion sécurisée
- ✅ Création et gestion de lieux de recharge
- ✅ Création et gestion de bornes de recharge
- ✅ Système de réservation complet
- ✅ Confirmation/Refus/Annulation de réservations
- ✅ Carte pour trouver les bornes disponibles
- ✅ Téléchargement de reçus PDF
- ✅ Export Excel avec filtres

**Qualité et tests**
- ✅ Tests unitaires pour les services
- ✅ Tests d'intégration bout-en-bout
- ✅ Validation des données avec Bean Validation
- ✅ Gestion des erreurs HTTP appropriée

**Documentation**
- ✅ README complet avec guide d'utilisation
- ✅ Guide d'installation détaillé
- ✅ Documentation API avec Swagger
- ✅ Commentaires dans le code

**Infrastructure**
- ✅ Docker Compose pour PostgreSQL, MailHog et Tomcat
- ✅ Scripts de migration Flyway
- ✅ Configuration par profils (dev, test, prod)

### 🔧 Améliorations

- Simplification de la configuration Spring Security
- Optimisation des requêtes JPA
- Amélioration des messages d'erreur
- Refactoring du code pour meilleure lisibilité

### 🐛 Corrections

- Correction de l'erreur SQL `function lower(bytea) does not exist`
- Correction du problème de bean PasswordEncoder en double
- Correction de la validation des annotations JPA
- Résolution des problèmes de déploiement Docker sur Windows

### 📦 Dépendances

**Backend**
- Spring Boot 3.2.0
- Spring Security 6.2.0
- Spring Data JPA
- PostgreSQL Driver
- Flyway 9.x
- Lombok
- Apache PDFBox 2.0.30
- Apache POI 5.2.5
- JWT (jjwt) 0.11.5
- Springdoc OpenAPI 2.5.0

**Frontend**
- Jakarta Servlet API 6.0
- JSTL 3.0
- Leaflet.js 1.9.4

## [0.9.0] - 2025-10-07

### ✨ Phase de développement

- Mise en place de l'architecture backend
- Création des entités JPA
- Développement des contrôleurs REST
- Implémentation du frontend JSP
- Configuration de la base de données

## [0.5.0] - 2025-10-06

### ✨ Phase initiale

- Initialisation du projet
- Configuration Maven
- Setup Docker Compose
- Création du schéma de base de données

---

## Format

Ce changelog suit le format [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/).

### Types de modifications

- `Ajouts` : Nouvelles fonctionnalités
- `Améliorations` : Modifications de fonctionnalités existantes
- `Corrections` : Corrections de bugs
- `Suppressions` : Fonctionnalités supprimées
- `Sécurité` : Corrections de vulnérabilités

---

**Dernière mise à jour** : 8 octobre 2025






