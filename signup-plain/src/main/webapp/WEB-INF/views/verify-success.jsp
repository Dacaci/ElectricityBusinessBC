<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vérification réussie - Electricity Business</title>
    <style>
        body {
            font-family: 'Inter', Arial, sans-serif;
            background: linear-gradient(135deg, #1e293b 0%, #1E40AF 100%);
            margin: 0;
            padding: 0;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .container {
            background: white;
            padding: 60px 40px;
            border-radius: 12px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            text-align: center;
            max-width: 500px;
            margin: 20px;
        }
        .success-icon {
            font-size: 80px;
            color: #27ae60;
            margin-bottom: 20px;
        }
        h2 {
            color: #1e293b;
            margin-bottom: 15px;
            font-weight: 400;
        }
        p {
            color: #64748b;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        .btn {
            display: inline-block;
            padding: 14px 40px;
            background: #1E40AF;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
            transition: background 0.2s;
        }
        .btn:hover {
            background: #1e293b;
        }
        .redirect-info {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #e1e8ed;
            color: #94a3b8;
            font-size: 14px;
        }
        .redirect-info a {
            color: #1E40AF;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="success-icon">✓</div>
        <h2>Compte vérifié avec succès !</h2>
        <p>Votre compte a été activé. Vous pouvez maintenant vous connecter à votre espace personnel.</p>
        
        <a href="<%= request.getContextPath() %>/login" class="btn">Se connecter maintenant</a>
        
        <div class="redirect-info">
            <p>Vous serez redirigé automatiquement dans quelques secondes...</p>
            <a href="<%= request.getContextPath() %>/login">Cliquez ici si la redirection ne fonctionne pas</a>
        </div>
    </div>
    
    <script>
        // Redirection automatique après 5 secondes
        setTimeout(function() {
            window.location.href = '<%= request.getContextPath() %>/login';
        }, 5000);
    </script>
</body>
</html>



