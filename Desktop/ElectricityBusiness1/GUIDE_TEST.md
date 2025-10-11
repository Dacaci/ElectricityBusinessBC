# 🧪 Guide de Test - Electricity Business

Ce guide vous permet de tester toutes les fonctionnalités de l'application.

## 🚀 Prérequis

Assurez-vous que :
- ✅ Docker est démarré : `docker ps`
- ✅ Backend API tourne sur port 8080
- ✅ Frontend Tomcat tourne sur port 8081
- ✅ PostgreSQL accessible sur port 5433

## 📝 Liste de contrôle des tests

### 1. Tests d'authentification

#### 1.1 Inscription
1. Ouvrir : `http://localhost:8081/signup-plain/register.jsp`
2. Remplir le formulaire :
   - Prénom : Jean
   - Nom : Dupont
   - Email : jean.dupont@test.com
   - Téléphone : 0612345678
   - Date de naissance : 01/01/1990
   - Adresse : 1 rue de Paris
   - Code postal : 75001
   - Ville : Paris
   - Mot de passe : Password123
   - Confirmer mot de passe : Password123
3. Cliquer "S'inscrire"
4. ✅ **Résultat attendu** : Message de succès + redirection

#### 1.2 Validation email
1. Ouvrir MailHog : `http://localhost:8025`
2. Cliquer sur l'email reçu
3. Copier le code de validation
4. Ouvrir : `http://localhost:8081/signup-plain/verify.jsp`
5. Entrer email et code
6. ✅ **Résultat attendu** : Compte activé

#### 1.3 Connexion
1. Ouvrir : `http://localhost:8081/signup-plain/login.jsp`
2. Email : jean.dupont@test.com
3. Mot de passe : Password123
4. ✅ **Résultat attendu** : Redirection vers dashboard

### 2. Tests des lieux de recharge

#### 2.1 Créer un lieu
1. Cliquer sur "📍 Lieux"
2. Cliquer "➕ Nouveau lieu"
3. Remplir :
   - Libellé : Parking Centre Commercial
   - Adresse : 10 Avenue des Champs-Élysées, 75008 Paris
   - Latitude : 48.8698
   - Longitude : 2.3078
   - Description : Parking couvert niveau -1
4. ✅ **Résultat attendu** : Lieu créé avec succès

#### 2.2 Lister les lieux
1. Cliquer sur "📍 Lieux"
2. ✅ **Résultat attendu** : Liste des lieux avec votre nouveau lieu

#### 2.3 Modifier un lieu
1. Dans la liste, cliquer "✏️ Modifier"
2. Changer la description
3. Sauvegarder
4. ✅ **Résultat attendu** : Modifications enregistrées

#### 2.4 Supprimer un lieu (après avoir supprimé ses bornes)
1. Supprimer d'abord toutes les bornes associées
2. Cliquer "🗑️ Supprimer"
3. Confirmer
4. ✅ **Résultat attendu** : Lieu supprimé

### 3. Tests des bornes de recharge

#### 3.1 Créer une borne
1. Cliquer sur "🔌 Bornes"
2. Cliquer "➕ Nouvelle borne"
3. **VÉRIFIER** : Le menu déroulant des lieux se remplit automatiquement
4. Remplir :
   - Nom : Borne Rapide A1
   - Lieu : Sélectionner le lieu créé
   - Tarif horaire : 2.50
   - Type : TYPE2S (pré-rempli)
5. ✅ **Résultat attendu** : Borne créée

#### 3.2 Lister les bornes
1. Cliquer sur "🔌 Bornes"
2. ✅ **Résultat attendu** : Liste des bornes avec détails

#### 3.3 Modifier une borne
1. Cliquer "✏️ Modifier"
2. Changer le tarif : 3.00
3. Sauvegarder
4. ✅ **Résultat attendu** : Tarif mis à jour

#### 3.4 Désactiver/Activer une borne
1. Cliquer "🔴 Désactiver" ou "🟢 Activer"
2. ✅ **Résultat attendu** : Statut changé

### 4. Tests de la carte

#### 4.1 Visualiser les bornes sur la carte
1. Cliquer sur "🗺️ Carte"
2. **VÉRIFIER** : Les stations s'affichent sur la carte (marqueurs bleus)
3. ✅ **Résultat attendu** : Carte chargée avec marqueurs

#### 4.2 Voir les détails d'une borne
1. Cliquer sur un marqueur
2. **VÉRIFIER** : Popup avec :
   - Nom de la borne
   - Adresse
   - Tarif
   - Type de prise
   - Bouton "Réserver"
3. ✅ **Résultat attendu** : Détails complets affichés

#### 4.3 Géolocalisation
1. Cliquer "Ma Position"
2. Autoriser la géolocalisation
3. ✅ **Résultat attendu** : Marqueur vert à votre position

#### 4.4 Recherche de proximité
1. Cliquer "Recherche Proximité"
2. ✅ **Résultat attendu** : Bornes proches affichées

### 5. Tests des réservations

#### 5.1 Créer une réservation (depuis la carte)
1. Sur la carte, cliquer sur une borne
2. Cliquer "Réserver"
3. **VÉRIFIER** : Formulaire pré-rempli avec l'ID de la borne
4. Choisir :
   - Date/heure début : Demain 10:00
   - Date/heure fin : Demain 12:00
5. Soumettre
6. ✅ **Résultat attendu** : Réservation créée

#### 5.2 Créer une réservation (depuis le menu)
1. Cliquer "📅 Réservations"
2. Cliquer "➕ Nouvelle réservation"
3. Remplir le formulaire
4. ✅ **Résultat attendu** : Réservation créée

#### 5.3 Lister les réservations
1. Cliquer "📅 Réservations"
2. **VÉRIFIER** : Liste des réservations avec :
   - ID
   - Utilisateur
   - Borne
   - Lieu
   - Dates
   - Statut (badge coloré)
   - Montant
   - Actions
3. ✅ **Résultat attendu** : Liste complète

#### 5.4 Confirmer une réservation
1. Trouver une réservation "En attente"
2. Cliquer "✅ Confirmer"
3. ✅ **Résultat attendu** : Statut changé à "Confirmée"

#### 5.5 Refuser une réservation
1. Créer une nouvelle réservation
2. Cliquer "❌ Refuser"
3. Confirmer
4. ✅ **Résultat attendu** : Statut changé à "Annulée"

#### 5.6 Terminer une réservation
1. Trouver une réservation "Confirmée"
2. Cliquer "✅ Terminer"
3. ✅ **Résultat attendu** : Statut changé à "Terminée"

#### 5.7 Filtrer les réservations
1. Utiliser le filtre "Statut"
2. Sélectionner "Confirmée"
3. ✅ **Résultat attendu** : Seules les réservations confirmées

#### 5.8 Filtrer par période
1. Utiliser le filtre "Période"
2. Sélectionner "À venir"
3. ✅ **Résultat attendu** : Seules les réservations futures

#### 5.9 Télécharger un reçu PDF
1. Cliquer "📄 PDF" sur une réservation
2. ✅ **Résultat attendu** : PDF téléchargé avec détails

#### 5.10 Exporter en Excel
1. En haut de la page, cliquer un bouton d'export (si disponible)
2. ✅ **Résultat attendu** : Fichier .xlsx téléchargé

### 6. Tests de l'API (Swagger)

#### 6.1 Accéder à Swagger
1. Ouvrir : `http://localhost:8080/swagger-ui.html`
2. ✅ **Résultat attendu** : Documentation interactive

#### 6.2 Tester un endpoint
1. Développer "GET /api/stations"
2. Cliquer "Try it out"
3. Cliquer "Execute"
4. ✅ **Résultat attendu** : Réponse 200 avec données JSON

### 7. Tests de validation

#### 7.1 Email invalide
1. Essayer de s'inscrire avec "test@invalid"
2. ✅ **Résultat attendu** : Erreur de validation

#### 7.2 Date de naissance (< 18 ans)
1. Essayer avec une date < 18 ans
2. ✅ **Résultat attendu** : Erreur "Vous devez avoir 18 ans minimum"

#### 7.3 Mot de passe faible
1. Essayer avec "123"
2. ✅ **Résultat attendu** : Erreur de complexité

#### 7.4 Tarif négatif
1. Essayer de créer une borne avec tarif -1
2. ✅ **Résultat attendu** : Erreur de validation

#### 7.5 Réservation dans le passé
1. Essayer de réserver hier
2. ✅ **Résultat attendu** : Erreur "Date dans le futur requise"

### 8. Tests d'erreurs

#### 8.1 Supprimer un lieu avec bornes
1. Essayer de supprimer un lieu qui a des bornes
2. ✅ **Résultat attendu** : Message d'erreur approprié

#### 8.2 Supprimer une borne avec réservations
1. Essayer de supprimer une borne avec réservations
2. ✅ **Résultat attendu** : Message d'erreur approprié

#### 8.3 Réservation sur période occupée
1. Créer une réservation
2. Essayer d'en créer une autre sur la même période
3. ✅ **Résultat attendu** : Conflit détecté (si implémenté)

### 9. Tests de performance

#### 9.1 Chargement de la carte avec beaucoup de données
1. Créer 10+ lieux et 20+ bornes
2. Ouvrir la carte
3. ✅ **Résultat attendu** : Chargement < 5 secondes

#### 9.2 Filtrage rapide
1. Créer 50+ réservations
2. Appliquer des filtres
3. ✅ **Résultat attendu** : Réponse instantanée

### 10. Tests de sécurité

#### 10.1 Accès sans connexion
1. Se déconnecter
2. Essayer d'accéder à `/api/users`
3. ✅ **Résultat attendu** : Accès refusé (si sécurité activée)

#### 10.2 Injection SQL
1. Essayer de mettre `' OR '1'='1` dans un champ
2. ✅ **Résultat attendu** : Requête bloquée/échappée

## 📊 Tableau de bord des tests

| Catégorie | Tests | Passés | Échoués |
|-----------|-------|--------|---------|
| Authentification | 3 | - | - |
| Lieux | 4 | - | - |
| Bornes | 4 | - | - |
| Carte | 4 | - | - |
| Réservations | 10 | - | - |
| API Swagger | 2 | - | - |
| Validation | 5 | - | - |
| Erreurs | 3 | - | - |
| Performance | 2 | - | - |
| Sécurité | 2 | - | - |
| **TOTAL** | **39** | **-** | **-** |

## 🐛 Rapport de bugs

Si vous trouvez un bug, notez :
1. **Page** : Où le bug se produit
2. **Action** : Ce que vous faisiez
3. **Attendu** : Ce qui devrait se passer
4. **Obtenu** : Ce qui s'est passé
5. **Console** : Messages d'erreur (F12 > Console)

## ✅ Checklist rapide (5 minutes)

Pour un test rapide :
- [ ] S'inscrire et se connecter
- [ ] Créer 1 lieu
- [ ] Créer 1 borne
- [ ] Voir la borne sur la carte
- [ ] Créer 1 réservation
- [ ] Confirmer la réservation
- [ ] Télécharger le PDF
- [ ] Se déconnecter

---

**Dernière mise à jour** : 8 octobre 2025





