<%@ page contentType="text/html; charset=UTF-8" %>
<%
    response.setHeader("Content-Security-Policy", "default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; connect-src 'self' http://localhost:8080; script-src 'self' 'unsafe-inline' 'unsafe-eval';");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Connexion - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251022v5">
    <style>
        body {
            background-color: #f5f7fa;
        }
        .login-container {
            max-width: 450px;
            margin: 80px auto;
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .login-header {
            text-align: center;
            margin-bottom: 30px;
        }
        .login-header h1 {
            color: #2c3e50;
            font-size: 28px;
            margin-bottom: 10px;
        }
        .login-header p {
            color: #7f8c8d;
            font-size: 14px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #2c3e50;
            font-weight: 500;
            font-size: 14px;
        }
        .form-control {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #dce4ec;
            border-radius: 6px;
            font-size: 14px;
        }
        .form-control:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
        }
        .btn {
            width: 100%;
            padding: 14px;
            font-size: 16px;
        }
        .message {
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 14px;
        }
        .message.success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .message.error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .links {
            text-align: center;
            margin-top: 20px;
        }
        .links a {
            color: #3498db;
            text-decoration: none;
            font-size: 14px;
        }
        .links a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <h1>Electricity Business</h1>
            <p>Connectez-vous à votre compte</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="message error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% if ("logout".equals(request.getParameter("message"))) { %>
            <div class="message success">
                Déconnexion réussie. À bientôt !
            </div>
        <% } %>
        
        <form id="loginForm">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" class="form-control" required 
                       value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
            </div>
            
            <div class="form-group">
                <label for="password">Mot de passe</label>
                <input type="password" id="password" name="password" class="form-control" required>
            </div>
            
            <button type="submit" class="btn btn-primary">Se connecter</button>
        </form>
        
        <div class="links">
            <a href="register.jsp">Créer un compte</a>
        </div>
    </div>

    <script src="js/jwt-utils.js"></script>
    <script>
        if (isAuthenticated()) {
            console.log('Utilisateur déjà connecté, redirection vers dashboard');
            window.location.href = '/dashboard.jsp';
        }
        
        document.getElementById('loginForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            
            try {
                const response = await fetch('http://localhost:8080/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, password })
                });
                
                if (!response.ok) {
                    const errorText = await response.text();
                    alert('Erreur de connexion: ' + (errorText || 'Identifiants invalides'));
                    return;
                }
                
                const data = await response.json();
                
                if (data.token) {
                    saveAuthData(data.token, data.user);
                    window.location.href = '/dashboard.jsp';
                } else {
                    alert('Erreur: Aucun token reçu');
                }
                
            } catch (error) {
                console.error('Erreur:', error);
                alert('Erreur de connexion: ' + error.message);
            }
        });
    </script>
</body>
</html>
