<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.eb.signup.user.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Electricity Business</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background-color: white; }
        .header { background-color: white; color: #333; padding: 20px; border-bottom: 1px solid #ddd; }
        .header h1 { margin: 0; display: inline-block; }
        .header .user-info { float: right; margin-top: 5px; }
        .header .user-info a { color: #007bff; text-decoration: none; margin-left: 15px; }
        .header .user-info a:hover { text-decoration: underline; }
        
        .container { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .welcome-card { background: white; padding: 30px; border: 1px solid #ddd; margin-bottom: 30px; }
        .welcome-card h2 { color: #333; margin-top: 0; }
        .welcome-card p { color: #666; font-size: 16px; line-height: 1.6; }
        
        .features-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; }
        .feature-card { background: white; padding: 25px; border: 1px solid #ddd; text-align: center; }
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
            <span>Bienvenue, <%= request.getAttribute("user") != null ? ((User)request.getAttribute("user")).getEmail() : "Utilisateur" %></span>
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
                <h3>📍 Mes Lieux de Recharge</h3>
                <p>Gérez vos lieux de recharge électrique et ajoutez de nouveaux emplacements.</p>
                <a href="locations.jsp" class="btn">Gérer les Lieux</a>
            </div>
            
            <div class="feature-card">
                <h3>🔌 Mes Bornes de Recharge</h3>
                <p>Ajoutez et gérez vos bornes de recharge avec leurs tarifs horaires.</p>
                <a href="stations.jsp" class="btn">Gérer les Bornes</a>
            </div>
            
            <div class="feature-card">
                <h3>🗺️ Carte des Stations</h3>
                <p>Trouvez les bornes de recharge disponibles près de chez vous sur la carte interactive.</p>
                <a href="map.jsp" class="btn">Voir la Carte</a>
            </div>
            
            <div class="feature-card">
                <h3>📅 Mes Réservations</h3>
                <p>Consultez vos réservations en cours et passées.</p>
                <a href="reservations.jsp" class="btn">Gérer les Réservations</a>
            </div>
            
            <div class="feature-card">
                <h3>Export des Données</h3>
                <p>Téléchargez vos réservations au format Excel pour vos archives.</p>
                <a href="http://localhost:8080/api/reservations/export.xlsx" class="btn" target="_blank">📊 Télécharger Excel</a>
            </div>
            
            <div class="feature-card">
                <h3>Reçus PDF</h3>
                <p>Générez des reçus PDF pour vos réservations confirmées.</p>
                <a href="http://localhost:8080/api/reservations/1/receipt.pdf" class="btn" target="_blank">📄 Télécharger PDF</a>
            </div>
        </div>
        
    </div>
</body>
</html>










