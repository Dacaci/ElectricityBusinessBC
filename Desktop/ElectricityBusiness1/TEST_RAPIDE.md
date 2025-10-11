# ⚡ Test Rapide - 10 minutes

## 🚀 Pour tester rapidement l'application

---

## ✅ Étape 1 : Vérifier que tout fonctionne (30 secondes)

Ouvrez ces 4 URLs dans votre navigateur :

1. **Frontend** : http://localhost:8081
   - ✅ Page d'accueil s'affiche

2. **API Status** : http://localhost:8080/actuator/health
   - ✅ Retourne `{"status":"UP"}`

3. **Swagger** : http://localhost:8080/swagger-ui.html
   - ✅ Documentation API visible

4. **MailHog** : http://localhost:8026
   - ✅ Interface email visible

---

## 🎯 Étape 2 : Test complet en 5 minutes

### A. Inscription (1 min)
1. Allez sur http://localhost:8081/register.jsp
2. Remplissez rapidement :
   - Email : `test@test.com`
   - Prénom : `Test`
   - Nom : `User`
   - Mot de passe : `Test123!`
   - Autres champs : valeurs au choix
3. Cliquez **S'inscrire**

### B. Vérification (30 sec)
1. Ouvrez http://localhost:8026
2. Cliquez sur le dernier email
3. Cliquez sur le lien de vérification

### C. Connexion (20 sec)
1. Retour sur http://localhost:8081/login.jsp
2. Connectez-vous avec `test@test.com` / `Test123!`
3. ✅ Vous êtes sur le dashboard

### D. Voir les lieux (30 sec)
1. Cliquez sur **"Lieux"**
2. ✅ Vous voyez 3 lieux existants

### E. Ajouter un lieu (1 min)
1. Cliquez **"+ Ajouter un lieu"**
2. Remplissez :
   - Nom : `Test Station`
   - Adresse : `1 Rue Test, Paris`
   - Latitude : `48.8566`
   - Longitude : `2.3522`
3. Cliquez **"Ajouter"**
4. ✅ Lieu créé et visible dans la liste

### F. Ajouter une borne (1 min)
1. Cliquez sur **"Bornes"**
2. Cliquez **"+ Ajouter une borne"**
3. Remplissez :
   - Nom : `Borne Test`
   - Lieu : Sélectionnez "Test Station"
   - Puissance : `50`
   - Type : `TYPE2S`
   - Tarif : `0.40`
4. Cliquez **"Ajouter"**
5. ✅ Borne créée

### G. Voir sur la carte (30 sec)
1. Cliquez sur **"Carte"**
2. ✅ Vous voyez tous les marqueurs
3. Cliquez sur un marqueur
4. ✅ Popup avec détails s'affiche

### H. Créer une réservation (1 min)
1. Dans la popup, cliquez **"Réserver"** OU allez sur "Réservations" → "Nouvelle"
2. Remplissez :
   - Borne : Sélectionnez "Borne Test"
   - Date début : Demain à 10:00
   - Date fin : Demain à 12:00
   - Véhicule : `Tesla Model 3`
3. Cliquez **"Créer"**
4. ✅ Réservation créée

### I. Gérer la réservation (30 sec)
1. Sur la page Réservations
2. Cliquez **"✓ Confirmer"**
3. ✅ Statut passe à CONFIRMED
4. Cliquez **"✓ Terminer"**
5. ✅ Statut passe à COMPLETED
6. Cliquez **"📄 Reçu"**
7. ✅ PDF se télécharge

---

## 🎉 C'est fait !

En 5 minutes, vous avez testé :
- ✅ Authentification (inscription, vérification, connexion)
- ✅ Gestion des lieux (voir, créer)
- ✅ Gestion des bornes (voir, créer)
- ✅ Carte interactive
- ✅ Réservations (créer, confirmer, terminer, reçu)

---

## 🧪 Tests supplémentaires (5 minutes)

### J. Test de recherche
1. Page Bornes → Recherchez "Test"
2. ✅ Filtre fonctionne

### K. Test de modification
1. Page Lieux → Cliquez "✏️ Modifier" sur "Test Station"
2. Changez la description
3. ✅ Sauvegarde OK

### L. Test de suppression
1. Créez un lieu "À supprimer"
2. Cliquez "🗑️ Supprimer"
3. Confirmez
4. ✅ Supprimé

### M. Test API directe
1. Ouvrez http://localhost:8080/swagger-ui.html
2. Testez `GET /api/locations`
3. ✅ Liste retournée en JSON

### N. Test des emails
1. Ouvrez http://localhost:8026
2. ✅ Voyez tous les emails envoyés (inscription, réservations)

---

## 📊 Récapitulatif

| Fonctionnalité | Status | Temps |
|---------------|--------|-------|
| 🔐 Authentification | ✅ OK | 2 min |
| 📍 Lieux | ✅ OK | 1.5 min |
| 🔌 Bornes | ✅ OK | 1 min |
| 🗺️ Carte | ✅ OK | 0.5 min |
| 📅 Réservations | ✅ OK | 1.5 min |
| 🔍 Recherche | ✅ OK | 0.5 min |
| ✏️ Modification | ✅ OK | 0.5 min |
| 🗑️ Suppression | ✅ OK | 0.5 min |
| 📄 Export PDF | ✅ OK | 0.5 min |
| 📧 Emails | ✅ OK | 0.5 min |

**TOTAL : 10 minutes pour tout tester** ⚡

---

## 🐛 Si quelque chose ne fonctionne pas

### Problème : Page blanche
```bash
# Vérifiez les logs
docker-compose logs signup-app --tail 50
```

### Problème : API ne répond pas
```bash
# Vérifiez le backend
docker-compose logs backend-app --tail 50
```

### Problème : Données manquantes
```bash
# Redémarrez tout
docker-compose restart
```

### Problème : Carte vide
- Vérifiez que les bornes ont des coordonnées
- Ouvrez la console du navigateur (F12) pour voir les erreurs

---

## 📝 Données de test disponibles

### Lieux existants :
1. **Station Centre Ville** - Place de la République, Paris (48.8566, 2.3522)
2. **Station Aéroport CDG** - Terminal 2F, Roissy (49.0097, 2.5479)
3. **Parking centre commercial** - Champs-Élysées, Paris (48.8698, 2.3078)

### Bornes existantes :
1. **Borne 1 - Type 2** @ Station Centre Ville (0.50€/h)
2. **Borne 2 - Type 2S** @ Station Centre Ville (0.55€/h)
3. **Borne Aéroport 1** @ Station Aéroport CDG (0.60€/h, Type 2S)
4. **Borne Aéroport 2** @ Station Aéroport CDG (0.60€/h, CHAdeMO)

### Utilisateurs de test :
- **test@example.com** / Password: (créez-en un nouveau)
- **test2@example.com** / Password: (existant)

---

## 🎯 Prochaines étapes

Après ce test rapide, consultez **PARCOURS_UTILISATEUR.md** pour :
- Tests plus approfondis
- Scénarios complexes
- Validation complète
- Tests de cas limites

**Bon test ! 🚀**

