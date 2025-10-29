<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vérification réussie - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251026">
    <style>
        .success-container {
            max-width: 500px;
            margin: 50px auto;
            padding: 40px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
        }
        
        .success-icon {
            font-size: 80px;
            margin-bottom: 20px;
        }
        
        .success-title {
            color: #27ae60;
            margin-bottom: 15px;
        }
        
        .success-message {
            color: #7f8c8d;
            margin-bottom: 30px;
        }
        
        .progress-indicator {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            margin-top: 20px;
            color: #3498db;
        }
        
        .progress-dot {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #3498db;
            animation: pulse 1.5s infinite;
        }
        
        .progress-dot:nth-child(2) {
            animation-delay: 0.3s;
        }
        
        .progress-dot:nth-child(3) {
            animation-delay: 0.6s;
        }
        
        @keyframes pulse {
            0%, 100% {
                opacity: 1;
            }
            50% {
                opacity: 0.3;
            }
        }
    </style>
    <script>
        // Rediriger automatiquement après 3 secondes
        setTimeout(function() {
            window.location.href = 'login.jsp';
        }, 3000);
    </script>
</head>
<body>
    <div class="success-container">
        <div class="success-icon" style="font-size: 80px; color: #27ae60;">✓</div>
        <h1 class="success-title">Vérification réussie !</h1>
        <p class="success-message">Votre compte est maintenant actif. Vous allez être redirigé vers la page de connexion...</p>
        <p class="progress-indicator">
            Redirection en cours
            <span class="progress-dot"></span>
            <span class="progress-dot"></span>
            <span class="progress-dot"></span>
        </p>
        <p style="margin-top: 30px;">
            <a href="login.jsp" style="color: #3498db; text-decoration: none;">Cliquez ici si la redirection ne fonctionne pas</a>
        </p>
    </div>
</body>
</html>

