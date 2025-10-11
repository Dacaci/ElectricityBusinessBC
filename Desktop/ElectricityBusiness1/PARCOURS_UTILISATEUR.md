# 🚀 Parcours Utilisateur - Electricity Business

## 📋 Guide complet pour tester toutes les fonctionnalités

---

## ✅ Prérequis

Assurez-vous que tous les services sont démarrés :
```bash
docker-compose ps
```

Vous devriez voir :
- ✅ `db` (PostgreSQL) sur port 5433
- ✅ `mailhog` sur ports 1026, 8026
- ✅ `backend-app` (API) sur port 8080
- ✅ `signup-app` (Frontend) sur port 8081

---

## 🎯 PARCOURS 1 : Création de compte et connexion

### Étape 1.1 : Accéder à l'application
1. Ouvrez votre navigateur
2. Allez sur : **http://localhost:8081**
3. ✅ **Vérification** : Vous devez voir la page d'accueil avec les options "Se connecter" et "S'inscrire"

### Étape 1.2 : Créer un nouveau compte
1. Cliquez sur **"S'inscrire"** ou allez sur : **http://localhost:8081/register.jsp**
2. Remplissez le formulaire :
   - **Prénom** : Alice
   - **Nom** : Dubois
   - **Email** : alice.dubois@example.com
   - **Téléphone** : 0612345678
   - **Date de naissance** : 01/01/1995
   - **Adresse** : 123 Rue de la Paix
   - **Code postal** : 75001
   - **Ville** : Paris
   - **Mot de passe** : Password123!
   - **Confirmer** : Password123!

3. Cliquez sur **"S'inscrire"**
4. ✅ **Vérification** : Vous êtes redirigé vers la page de vérification

### Étape 1.3 : Vérifier l'email (simulation)
1. Ouvrez MailHog : **http://localhost:8026**
2. ✅ **Vérification** : Vous devez voir un email de vérification
3. Cliquez sur le lien de vérification dans l'email
4. ✅ **Vérification** : Votre compte est activé

### Étape 1.4 : Se connecter
1. Retournez sur : **http://localhost:8081/login.jsp**
2. Connectez-vous avec :
   - **Email** : alice.dubois@example.com
   - **Mot de passe** : Password123!
3. Cliquez sur **"Se connecter"**
4. ✅ **Vérification** : Vous êtes redirigé vers le dashboard

---

## 🗺️ PARCOURS 2 : Gestion des lieux

### Étape 2.1 : Voir la liste des lieux existants
1. Dans le menu, cliquez sur **"Lieux"** ou allez sur : **http://localhost:8081/locations.jsp**
2. ✅ **Vérification** : Vous voyez la liste des lieux avec :
   - Station Centre Ville - Place de la République, 75001 Paris
   - Station Aéroport CDG - Terminal 2F, 95700 Roissy-en-France

### Étape 2.2 : Créer un nouveau lieu
1. Cliquez sur **"+ Ajouter un lieu"** ou allez sur : **http://localhost:8081/add-location.jsp**
2. Remplissez le formulaire :
   - **Nom du lieu** : Station Gare du Nord
   - **Adresse** : Place Napoléon III, 75010 Paris
   - **Latitude** : 48.8809
   - **Longitude** : 2.3553
   - **Description** : Station de recharge à proximité de la Gare du Nord

3. Cliquez sur **"Ajouter le lieu"**
4. ✅ **Vérification** : 
   - Message de succès affiché
   - Redirection vers la page des lieux
   - Votre nouveau lieu apparaît dans la liste

### Étape 2.3 : Modifier un lieu
1. Sur la page des lieux, cliquez sur **"✏️ Modifier"** pour "Station Gare du Nord"
2. Modifiez la description : "Station de recharge rapide 24h/24 près de la Gare du Nord"
3. Cliquez sur **"Mettre à jour"**
4. ✅ **Vérification** : La description est mise à jour

### Étape 2.4 : Rechercher un lieu
1. Dans la barre de recherche, tapez : "Gare"
2. ✅ **Vérification** : Seul "Station Gare du Nord" s'affiche

---

## 🔌 PARCOURS 3 : Gestion des bornes de recharge

### Étape 3.1 : Voir les bornes existantes
1. Cliquez sur **"Bornes"** ou allez sur : **http://localhost:8081/stations.jsp**
2. ✅ **Vérification** : Vous voyez la liste des bornes avec leurs détails (nom, lieu, tarif, type de prise)

### Étape 3.2 : Ajouter une nouvelle borne
1. Cliquez sur **"+ Ajouter une borne"** ou allez sur : **http://localhost:8081/add-station.jsp**
2. Remplissez le formulaire :
   - **Nom de la borne** : Borne Rapide Nord 1
   - **Sélectionner un lieu** : Station Gare du Nord (que vous venez de créer)
   - **Puissance (kW)** : 50
   - **Type de prise** : CCS_COMBO
   - **Tarif horaire (€)** : 0.35
   - **Description** : Borne de recharge rapide CCS Combo 50kW

3. Cliquez sur **"Ajouter la borne"**
4. ✅ **Vérification** :
   - Message de succès
   - La borne apparaît dans la liste des bornes
   - Le lieu "Station Gare du Nord" est bien affiché

### Étape 3.3 : Ajouter plusieurs bornes au même lieu
1. Ajoutez une deuxième borne :
   - **Nom** : Borne Rapide Nord 2
   - **Lieu** : Station Gare du Nord
   - **Puissance** : 50
   - **Type de prise** : TYPE2S
   - **Tarif** : 0.30

2. ✅ **Vérification** : Vous avez maintenant 2 bornes au même lieu

### Étape 3.4 : Modifier une borne
1. Cliquez sur **"✏️ Modifier"** pour "Borne Rapide Nord 1"
2. Changez le tarif à : 0.40
3. Cliquez sur **"Mettre à jour"**
4. ✅ **Vérification** : Le tarif est mis à jour

### Étape 3.5 : Filtrer les bornes
1. Utilisez le filtre "Type de prise" : CCS_COMBO
2. ✅ **Vérification** : Seules les bornes CCS Combo s'affichent

---

## 🗺️ PARCOURS 4 : Visualisation sur la carte

### Étape 4.1 : Accéder à la carte
1. Cliquez sur **"Carte"** ou allez sur : **http://localhost:8081/map.jsp**
2. ✅ **Vérification** : 
   - La carte s'affiche avec tous les lieux
   - Des marqueurs sont visibles pour chaque borne

### Étape 4.2 : Voir les détails d'une borne
1. Cliquez sur un marqueur sur la carte
2. ✅ **Vérification** : Une popup s'ouvre avec :
   - Nom de la borne
   - Lieu
   - Adresse
   - Tarif horaire
   - Type de prise
   - Bouton "Réserver"

### Étape 4.3 : Réserver depuis la carte
1. Dans la popup, cliquez sur **"Réserver"**
2. ✅ **Vérification** : Vous êtes redirigé vers la page de réservation avec la borne pré-sélectionnée

---

## 📅 PARCOURS 5 : Créer et gérer des réservations

### Étape 5.1 : Créer une réservation
1. Allez sur : **http://localhost:8081/add-reservation.jsp**
2. Remplissez le formulaire :
   - **Borne** : Borne Rapide Nord 1
   - **Date de début** : Aujourd'hui + 1 jour à 10:00
   - **Date de fin** : Aujourd'hui + 1 jour à 12:00
   - **Véhicule** : Tesla Model 3
   - **Commentaires** : Recharge pour départ en week-end

3. Cliquez sur **"Créer la réservation"**
4. ✅ **Vérification** :
   - Message de succès
   - Montant total calculé (2 heures × tarif)
   - Redirection vers la liste des réservations

### Étape 5.2 : Voir mes réservations
1. Allez sur : **http://localhost:8081/reservations.jsp**
2. ✅ **Vérification** : Vous voyez votre réservation avec :
   - Numéro de réservation
   - Borne et lieu
   - Dates et heures
   - Statut : PENDING (En attente)
   - Montant total

### Étape 5.3 : Filtrer les réservations
1. Utilisez le filtre "Statut" : PENDING
2. ✅ **Vérification** : Seules les réservations en attente s'affichent

3. Utilisez le filtre par date : Aujourd'hui
4. ✅ **Vérification** : Seules les réservations d'aujourd'hui s'affichent

### Étape 5.4 : Confirmer une réservation
1. Cliquez sur **"✓ Confirmer"** pour votre réservation
2. ✅ **Vérification** :
   - Le statut passe à CONFIRMED
   - Le badge devient vert
   - Les actions disponibles changent

### Étape 5.5 : Créer une deuxième réservation (pour test)
1. Créez une nouvelle réservation :
   - **Borne** : Borne Rapide Nord 2
   - **Date de début** : Aujourd'hui + 2 jours à 14:00
   - **Date de fin** : Aujourd'hui + 2 jours à 16:00

### Étape 5.6 : Annuler une réservation
1. Pour la deuxième réservation, cliquez sur **"✗ Annuler"**
2. Confirmez l'annulation
3. ✅ **Vérification** :
   - Le statut passe à CANCELLED
   - Le badge devient rouge
   - Plus d'actions disponibles

### Étape 5.7 : Compléter une réservation
1. Pour la première réservation (CONFIRMED), cliquez sur **"✓ Terminer"**
2. ✅ **Vérification** :
   - Le statut passe à COMPLETED
   - Un bouton "📄 Reçu" apparaît

### Étape 5.8 : Télécharger le reçu
1. Cliquez sur **"📄 Reçu"**
2. ✅ **Vérification** : Un fichier PDF se télécharge avec :
   - Informations de la réservation
   - Détails de la borne
   - Montant payé

---

## 📊 PARCOURS 6 : Tableau de bord

### Étape 6.1 : Voir le dashboard
1. Allez sur : **http://localhost:8081/dashboard.jsp**
2. ✅ **Vérification** : Vous voyez :
   - Statistiques générales (nombre de bornes, lieux, réservations)
   - Graphiques de l'activité
   - Réservations récentes
   - Vos informations de profil

### Étape 6.2 : Navigation rapide
1. Cliquez sur les liens rapides du dashboard
2. ✅ **Vérification** : Navigation fluide vers les différentes sections

---

## 🔍 PARCOURS 7 : Tests de validation

### Étape 7.1 : Validation des formulaires
1. Essayez de créer une borne sans sélectionner de lieu
2. ✅ **Vérification** : Message d'erreur affiché

3. Essayez de créer une réservation avec une date de fin avant la date de début
4. ✅ **Vérification** : Message d'erreur affiché

### Étape 7.2 : Test de recherche
1. Sur la page des bornes, recherchez "Rapide"
2. ✅ **Vérification** : Seules les bornes avec "Rapide" dans le nom s'affichent

3. Effacez la recherche
4. ✅ **Vérification** : Toutes les bornes réapparaissent

### Étape 7.3 : Test de pagination
1. Si vous avez plus de 10 éléments, vérifiez la pagination
2. ✅ **Vérification** : Navigation entre les pages fonctionne

### Étape 7.4 : Test de suppression
1. Créez un lieu de test : "Lieu à supprimer"
2. Cliquez sur **"🗑️ Supprimer"**
3. Confirmez la suppression
4. ✅ **Vérification** : Le lieu disparaît de la liste

---

## 🧪 PARCOURS 8 : Tests API (optionnel pour développeurs)

### Étape 8.1 : Accéder à Swagger
1. Ouvrez : **http://localhost:8080/swagger-ui.html**
2. ✅ **Vérification** : Documentation API complète visible

### Étape 8.2 : Tester un endpoint
1. Ouvrez l'endpoint `GET /api/stations`
2. Cliquez sur "Try it out"
3. Cliquez sur "Execute"
4. ✅ **Vérification** : Liste des bornes retournée en JSON

### Étape 8.3 : Tester l'authentification
1. Utilisez l'endpoint `POST /api/auth/login`
2. Body :
   ```json
   {
     "email": "alice.dubois@example.com",
     "password": "Password123!"
   }
   ```
3. ✅ **Vérification** : Token JWT retourné

---

## 📧 PARCOURS 9 : Vérification des emails

### Étape 9.1 : Consulter MailHog
1. Ouvrez : **http://localhost:8026**
2. ✅ **Vérification** : Tous les emails envoyés sont visibles :
   - Emails de vérification de compte
   - Confirmations de réservation
   - Notifications d'annulation

---

## ✅ Checklist complète des fonctionnalités

### Authentification
- ✅ Inscription d'un nouvel utilisateur
- ✅ Vérification email
- ✅ Connexion
- ✅ Déconnexion

### Gestion des lieux
- ✅ Afficher la liste des lieux
- ✅ Créer un nouveau lieu
- ✅ Modifier un lieu existant
- ✅ Supprimer un lieu
- ✅ Rechercher un lieu

### Gestion des bornes
- ✅ Afficher la liste des bornes
- ✅ Créer une nouvelle borne
- ✅ Modifier une borne existante
- ✅ Supprimer une borne
- ✅ Filtrer par type de prise
- ✅ Associer une borne à un lieu

### Carte interactive
- ✅ Afficher tous les lieux sur une carte
- ✅ Voir les détails d'une borne via popup
- ✅ Réserver depuis la carte

### Réservations
- ✅ Créer une réservation
- ✅ Voir mes réservations
- ✅ Confirmer une réservation
- ✅ Annuler une réservation
- ✅ Compléter une réservation
- ✅ Télécharger un reçu PDF
- ✅ Filtrer par statut
- ✅ Filtrer par date

### Dashboard
- ✅ Voir les statistiques
- ✅ Voir les réservations récentes
- ✅ Navigation rapide

### API Backend
- ✅ Documentation Swagger
- ✅ Authentification JWT
- ✅ Endpoints REST fonctionnels
- ✅ Validation des données
- ✅ Gestion des erreurs

---

## 🐛 Problèmes potentiels et solutions

### Problème : "Failed to fetch" sur la carte
**Solution** : Vérifiez que le backend est bien démarré sur le port 8080

### Problème : Pas de lieux dans le dropdown
**Solution** : 
1. Créez d'abord un lieu
2. Actualisez la page d'ajout de borne
3. Le dropdown devrait maintenant afficher les lieux

### Problème : Les marqueurs n'apparaissent pas sur la carte
**Solution** : 
1. Vérifiez que les bornes ont bien des coordonnées (latitude/longitude)
2. Vérifiez la console du navigateur (F12) pour les erreurs

### Problème : Docker ne démarre pas
**Solution** :
```bash
docker-compose down
docker-compose up -d
```

---

## 🎉 Résultat attendu

À la fin de ce parcours, vous devriez avoir :
- ✅ 1 compte utilisateur créé et vérifié
- ✅ 3 lieux (2 existants + 1 créé)
- ✅ 2+ bornes de recharge
- ✅ 2 réservations (1 complétée, 1 annulée)
- ✅ Toutes les fonctionnalités testées et validées

---

## 📝 Notes importantes

1. **Les données de test** sont persistées dans PostgreSQL
2. **MailHog** capture tous les emails (pas d'envoi réel)
3. **Les coordonnées GPS** doivent être valides pour l'affichage sur la carte
4. **Le port 8080** est pour l'API, **8081** pour le frontend
5. **Swagger UI** est accessible pour tester directement l'API

---

## 🚀 Pour aller plus loin

1. Testez les **validations de formulaires**
2. Créez **plusieurs utilisateurs** et testez les permissions
3. Testez les **performances** avec beaucoup de données
4. Vérifiez le **responsive design** sur mobile
5. Testez les **cas limites** (dates invalides, réservations simultanées, etc.)

---

**Bon test ! 🎉**


