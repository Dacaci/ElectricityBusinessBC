<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Electricity Business - Accueil</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }
        .container { max-width: 800px; margin: 0 auto; background: white; padding: 40px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #007bff; text-align: center; margin-bottom: 30px; }
        .subtitle { text-align: center; color: #666; margin-bottom: 40px; font-size: 18px; }
        .nav-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin: 40px 0; }
        .nav-card { 
            background: #f8f9fa; padding: 25px; border-radius: 8px; 
            text-align: center; border: 2px solid transparent; transition: all 0.3s;
        }
        .nav-card:hover { border-color: #007bff; transform: translateY(-2px); }
        .nav-card h3 { color: #333; margin-top: 0; }
        .nav-card p { color: #666; margin-bottom: 20px; }
        .nav-card a { 
            display: inline-block; padding: 10px 20px; background-color: #007bff; 
            color: white; text-decoration: none; border-radius: 4px; 
        }
        .nav-card a:hover { background-color: #0056b3; }
        .status { text-align: center; margin-top: 30px; padding: 15px; background-color: #d1ecf1; border-radius: 4px; color: #0c5460; }
    </style>
</head>
<body>
    <div class="container">
        <h1>⚡ Electricity Business</h1>
        <p class="subtitle">Plateforme de gestion des stations de recharge électrique</p>
        
        <div class="nav-grid">
            <div class="nav-card">
                <h3>📝 S'inscrire</h3>
                <p>Créez votre compte pour accéder à la plateforme</p>
                <a href="register">S'inscrire</a>
            </div>
            
            <div class="nav-card">
                <h3>✅ Vérifier le compte</h3>
                <p>Activez votre compte avec le code reçu par email</p>
                <a href="verify">Vérifier</a>
            </div>
            
            <div class="nav-card">
                <h3>🔐 Se connecter</h3>
                <p>Accédez à votre tableau de bord</p>
                <a href="login">Se connecter</a>
            </div>
        </div>
        
        <div class="status">
            <strong>🎯 Cycle complet :</strong> Inscription → Validation Email → Connexion → Dashboard
        </div>
    </div>
</body>
</html>
