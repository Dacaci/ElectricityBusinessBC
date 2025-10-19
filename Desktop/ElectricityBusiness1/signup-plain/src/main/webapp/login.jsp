<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Connexion - Electricity Business</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }
        .container { max-width: 400px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h2 { color: #333; text-align: center; margin-bottom: 30px; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 5px; color: #555; font-weight: bold; }
        input[type="email"], input[type="password"] { 
            width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 4px; 
            box-sizing: border-box; font-size: 16px;
        }
        button { 
            width: 100%; padding: 12px; background-color: #007bff; color: white; 
            border: none; border-radius: 4px; font-size: 16px; cursor: pointer;
        }
        button:hover { background-color: #0056b3; }
        .error { color: #dc3545; background-color: #f8d7da; padding: 10px; border-radius: 4px; margin-bottom: 20px; }
        .links { text-align: center; margin-top: 20px; }
        .links a { color: #007bff; text-decoration: none; margin: 0 10px; }
        .links a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Connexion</h2>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% if ("logout".equals(request.getParameter("message"))) { %>
            <div style="color: #28a745; background-color: #d4edda; padding: 10px; border-radius: 4px; margin-bottom: 20px;">
                Déconnexion réussie. À bientôt !
            </div>
        <% } %>
        
        <form id="loginForm">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required 
                       value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
            </div>
            
            <div class="form-group">
                <label for="password">Mot de passe</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <button type="submit">Se connecter</button>
        </form>
        
        
        <div id="errorMessage" class="error" style="display: none;"></div>
        <div id="successMessage" style="color: #28a745; background-color: #d4edda; padding: 10px; border-radius: 4px; margin-bottom: 20px; display: none;"></div>
        
        <div class="links">
            <a href="register">Créer un compte</a>
            <a href="verify">Vérifier un compte</a>
            <a href="index">Accueil</a>
        </div>
    </div>
    
    <!-- Scripts -->
    <script src="js/jwt-utils.js"></script>
    <script>
        document.getElementById('loginForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const errorDiv = document.getElementById('errorMessage');
            const successDiv = document.getElementById('successMessage');
            
            // Masquer les messages précédents
            errorDiv.style.display = 'none';
            successDiv.style.display = 'none';
            
            try {
                const response = await fetch('http://localhost:8080/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                });
                
                if (response.ok) {
                    const data = await response.json();
                    
                    // Sauvegarder le token et les données utilisateur
                    saveAuthData(data.token, data.user);
                    
                    successDiv.textContent = 'Connexion réussie ! Redirection...';
                    successDiv.style.display = 'block';
                    
                    // Rediriger vers le dashboard
                    setTimeout(() => {
                        window.location.href = '/dashboard.jsp';
                    }, 1000);
                    
                } else {
                    const errorData = await response.json().catch(() => ({ message: 'Erreur de connexion' }));
                    errorDiv.textContent = errorData.message || 'Email ou mot de passe incorrect';
                    errorDiv.style.display = 'block';
                }
                
            } catch (error) {
                console.error('Erreur:', error);
                errorDiv.textContent = 'Erreur de connexion au serveur';
                errorDiv.style.display = 'block';
            }
        });
        
        // Vérifier si l'utilisateur est déjà connecté
        if (isAuthenticated()) {
            console.log('Utilisateur déjà connecté, redirection vers dashboard');
            window.location.href = '/dashboard.jsp';
        } else {
            console.log('Utilisateur non connecté, affichage de la page de login');
        }
        
    </script>
</body>
</html>
