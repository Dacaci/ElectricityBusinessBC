<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.eb.signup.user.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Electricity Business</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background-color: #f5f5f5; }
        .header { background-color: #007bff; color: white; padding: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .header h1 { margin: 0; display: inline-block; }
        .header .user-info { float: right; margin-top: 5px; }
        .header .user-info a { color: white; text-decoration: none; margin-left: 15px; }
        .header .user-info a:hover { text-decoration: underline; }
        
        .container { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .welcome-card { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 30px; }
        .welcome-card h2 { color: #333; margin-top: 0; }
        .welcome-card p { color: #666; font-size: 16px; line-height: 1.6; }
        
        .features-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; }
        .feature-card { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; }
        .feature-card h3 { color: #007bff; margin-top: 0; }
        .feature-card p { color: #666; margin-bottom: 20px; }
        .feature-card .btn { 
            display: inline-block; padding: 10px 20px; background-color: #007bff; 
            color: white; text-decoration: none; border-radius: 4px; 
        }
        .feature-card .btn:hover { background-color: #0056b3; }
        .feature-card .btn.disabled { 
            background-color: #6c757d; cursor: not-allowed; 
        }
        
        .status-badge { 
            display: inline-block; padding: 4px 8px; border-radius: 12px; 
            font-size: 12px; font-weight: bold; text-transform: uppercase;
        }
        .status-active { background-color: #d4edda; color: #155724; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Electricity Business</h1>
        <div class="user-info">
            <span>Bienvenue, <%= ((User)request.getAttribute("user")).getEmail() %></span>
            <span class="status-badge status-active">Compte Actif</span>
            <a href="logout">Déconnexion</a>
        </div>
    </div>
    
    <div class="container">
        <div class="welcome-card">
            <h2>Connexion réussie !</h2>
            <p>
                Félicitations ! Votre compte a été activé avec succès. Vous pouvez maintenant accéder 
                à toutes les fonctionnalités de la plateforme Electricity Business.
            </p>
            <p>
                <strong>ID Utilisateur :</strong> <%= request.getAttribute("userId") %><br>
                <strong>Email :</strong> <%= request.getAttribute("userEmail") %><br>
                <strong>Statut :</strong> <span class="status-badge status-active">ACTIVE</span>
            </p>
        </div>
        
        <div class="features-grid">
            <div class="feature-card">
                <h3>Gestion des Stations</h3>
                <p>Gérez vos stations de recharge électrique, ajoutez de nouvelles bornes et surveillez leur statut.</p>
                <a href="#" class="btn disabled">Bientôt disponible</a>
            </div>
            
            <div class="feature-card">
                <h3>Monitoring</h3>
                <p>Surveillez en temps réel l'utilisation de vos stations et consultez les statistiques détaillées.</p>
                <a href="#" class="btn disabled">Bientôt disponible</a>
            </div>
            
            <div class="feature-card">
                <h3>Facturation</h3>
                <p>Gérez la tarification et consultez les revenus générés par vos stations de recharge.</p>
                <a href="#" class="btn disabled">Bientôt disponible</a>
            </div>
            
            <div class="feature-card">
                <h3>Maintenance</h3>
                <p>Planifiez et suivez les opérations de maintenance de vos équipements.</p>
                <a href="#" class="btn disabled">Bientôt disponible</a>
            </div>
        </div>
        
        <div class="welcome-card">
            <h3>Prochaines étapes</h3>
            <p>
                Le système d'inscription et de connexion est maintenant opérationnel. 
                Les fonctionnalités métier (gestion des stations, monitoring, etc.) seront 
                développées dans le backend Spring Boot avec une API REST moderne.
            </p>
            <p>
                <strong>Architecture :</strong><br>
                • <strong>Module d'inscription</strong> (Servlet) : Inscription → Validation → Connexion<br>
                • <strong>API REST</strong> (Spring Boot) : Gestion des stations, JWT, Angular
            </p>
        </div>
    </div>
</body>
</html>
