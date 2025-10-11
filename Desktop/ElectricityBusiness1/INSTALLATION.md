# 🔧 Guide d'installation - Electricity Business

Ce guide vous permet d'installer et de démarrer rapidement le projet Electricity Business.

## ⚡ Installation rapide (5 minutes)

### Prérequis

Assurez-vous d'avoir installé :
- ✅ **Java 17** : `java -version`
- ✅ **Maven 3.6+** : `mvn -version`
- ✅ **Docker Desktop** : `docker --version`
- ✅ **Git** : `git --version`

### Étape 1 : Cloner le projet

```bash
git clone <url-du-repo>
cd ElectricityBusiness1
```

### Étape 2 : Démarrer Docker

```bash
docker-compose up -d
```

**Ce qui démarre :**
- 🐘 PostgreSQL sur `localhost:5432`
- 📧 MailHog sur `localhost:8025` (interface web) et `localhost:1025` (SMTP)
- 🚀 Tomcat sur `localhost:8081`

**Vérifiez que tout est démarré :**
```bash
docker ps
```

Vous devriez voir 3 conteneurs actifs.

### Étape 3 : Compiler et démarrer le backend

```bash
# Compiler
mvn -f backend/eb-backend/pom.xml clean package -DskipTests

# Démarrer
java -jar backend/eb-backend/target/eb-backend-0.0.1-SNAPSHOT.jar
```

**Le backend démarre sur :** `http://localhost:8080`

**Vérifiez :**
```bash
curl http://localhost:8080/api/stations
```

### Étape 4 : Compiler le frontend

```bash
# Compiler le WAR
mvn -f signup-plain/pom.xml clean package

# Copier dans Tomcat
docker cp signup-plain/target/signup-plain.war electricitybusiness1-signup-app-1:/usr/local/tomcat/webapps/

# Redémarrer Tomcat
docker restart electricitybusiness1-signup-app-1
```

### Étape 5 : Accéder à l'application

Attendez 10 secondes que Tomcat redémarre, puis :

- 🌐 **Application** : http://localhost:8081/signup-plain/
- 📖 **API Docs (Swagger)** : http://localhost:8080/swagger-ui.html
- 📧 **MailHog** : http://localhost:8025

## 🎯 Premier test

### 1. S'inscrire

1. Ouvrez http://localhost:8081/signup-plain/register.jsp
2. Remplissez le formulaire
3. Cliquez sur "S'inscrire"
4. Ouvrez MailHog sur http://localhost:8025
5. Consultez l'email de validation
6. Validez votre compte

### 2. Se connecter

1. Ouvrez http://localhost:8081/signup-plain/login.jsp
2. Entrez email et mot de passe
3. Vous êtes redirigé vers le tableau de bord

### 3. Créer un lieu de recharge

1. Cliquez sur "📍 Lieux"
2. Cliquez sur "➕ Nouveau lieu"
3. Remplissez :
   - Libellé : "Mon garage"
   - Adresse : "1 rue de Paris, 75001 Paris"
   - Latitude : 48.8566
   - Longitude : 2.3522
4. Validez

### 4. Créer une borne

1. Cliquez sur "🔌 Bornes"
2. Cliquez sur "➕ Nouvelle borne"
3. Remplissez :
   - Nom : "Borne Garage"
   - Lieu : Sélectionnez votre lieu
   - Tarif : 0.50 €/h
   - Type : TYPE2S
4. Validez

### 5. Voir sur la carte

1. Cliquez sur "🗺️ Carte"
2. Autorisez la géolocalisation
3. Votre borne apparaît sur la carte !

## 🛠️ Dépannage

### Le backend ne démarre pas

**Erreur : Port 8080 déjà utilisé**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

**Erreur : Base de données inaccessible**
```bash
# Vérifier que PostgreSQL tourne
docker ps | grep postgres

# Redémarrer PostgreSQL
docker restart electricitybusiness1-db-1
```

### Le frontend ne s'affiche pas

**404 Not Found**
```bash
# Vérifier que le WAR est déployé
docker exec electricitybusiness1-signup-app-1 ls /usr/local/tomcat/webapps/

# Recopier le WAR si nécessaire
docker cp signup-plain/target/signup-plain.war electricitybusiness1-signup-app-1:/usr/local/tomcat/webapps/
docker restart electricitybusiness1-signup-app-1
```

### Les emails ne partent pas

**Vérifier MailHog**
```bash
# Accéder à l'interface web
open http://localhost:8025

# Vérifier les logs
docker logs electricitybusiness1-mailhog-1
```

### Docker ne démarre pas

**Pas assez de mémoire**
```bash
# Augmenter la mémoire allouée à Docker Desktop
# Settings > Resources > Memory : 4 GB minimum
```

**Ports déjà utilisés**
```bash
# Modifier docker-compose.yml pour utiliser d'autres ports
# Par exemple : "5433:5432" au lieu de "5432:5432"
```

## 🔄 Redémarrage complet

Si quelque chose ne fonctionne pas, redémarrez tout :

```bash
# 1. Arrêter le backend
# CTRL+C dans le terminal du backend

# 2. Arrêter Docker
docker-compose down

# 3. Nettoyer (ATTENTION : supprime les données)
docker-compose down -v

# 4. Redémarrer Docker
docker-compose up -d

# 5. Attendre 10 secondes

# 6. Redémarrer le backend
java -jar backend/eb-backend/target/eb-backend-0.0.1-SNAPSHOT.jar
```

## 📦 Configuration avancée

### Modifier la configuration

**Backend** : `backend/eb-backend/src/main/resources/application.yml`

```yaml
server:
  port: 8080  # Modifier le port

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/eb  # Modifier l'URL
    username: eb_user
    password: eb_password
    
jwt:
  secret: votre-secret-jwt-ici  # Changer en production
  expiration: 86400000  # 24h en millisecondes
```

**Frontend** : `signup-plain/src/main/webapp/WEB-INF/web.xml`

### Variables d'environnement

Vous pouvez utiliser des variables d'environnement :

```bash
export DB_URL=jdbc:postgresql://localhost:5432/eb
export DB_USER=eb_user
export DB_PASSWORD=eb_password
export JWT_SECRET=mon-secret-jwt

java -jar backend/eb-backend/target/eb-backend-0.0.1-SNAPSHOT.jar
```

## 🚀 Déploiement en production

### 1. Utiliser PostgreSQL externe

Modifiez `application.yml` :
```yaml
spring:
  datasource:
    url: jdbc:postgresql://votre-serveur-postgres:5432/eb_production
```

### 2. Sécuriser le JWT

```yaml
jwt:
  secret: ${JWT_SECRET}  # Utiliser une variable d'environnement
  expiration: 3600000    # 1h en production
```

### 3. Désactiver le mode debug

```yaml
logging:
  level:
    root: WARN
    com.eb: INFO
```

### 4. Activer HTTPS

Générer un certificat SSL et configurer Tomcat.

## 📞 Besoin d'aide ?

- 📖 Consultez le [README.md](README.md)
- 🐛 Signalez un bug : [GitHub Issues](#)
- 💬 Support : support@electricitybusiness.com

---

**Dernière mise à jour** : Octobre 2025





