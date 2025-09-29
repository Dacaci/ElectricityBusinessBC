<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Inscription - Electricity Business</title>
    <style>
        body { 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            margin: 0; 
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .container { 
            background: white; 
            padding: 40px; 
            border-radius: 12px; 
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 600px;
            margin: 20px;
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        .logo {
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        h2 { 
            color: #333; 
            margin: 0;
            font-weight: 300;
        }
        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }
        .form-group { 
            margin-bottom: 20px; 
        }
        .form-group.full-width {
            grid-column: 1 / -1;
        }
        label { 
            display: block; 
            margin-bottom: 8px; 
            color: #555; 
            font-weight: 500;
            font-size: 14px;
        }
        .required { color: #e74c3c; }
        input[type="text"], input[type="email"], input[type="password"], input[type="tel"], input[type="date"], select { 
            width: 100%; 
            padding: 12px 16px; 
            border: 2px solid #e1e8ed; 
            border-radius: 8px; 
            font-size: 16px;
            transition: border-color 0.3s ease;
            box-sizing: border-box;
        }
        input:focus, select:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        .date-input {
            position: relative;
        }
        .date-input::after {
            content: "📅";
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            pointer-events: none;
        }
        button { 
            width: 100%; 
            padding: 16px; 
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; 
            border: none; 
            border-radius: 8px; 
            font-size: 16px; 
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        button:hover { 
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
        }
        .links { 
            text-align: center; 
            margin-top: 30px; 
            padding-top: 20px;
            border-top: 1px solid #e1e8ed;
        }
        .links a { 
            color: #667eea; 
            text-decoration: none; 
            margin: 0 15px;
            font-weight: 500;
        }
        .links a:hover { 
            text-decoration: underline; 
        }
        .error {
            background-color: #fee;
            color: #c33;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 20px;
            border-left: 4px solid #e74c3c;
        }
        .success {
            background-color: #efe;
            color: #363;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 20px;
            border-left: 4px solid #27ae60;
        }
        @media (max-width: 768px) {
            .form-grid {
                grid-template-columns: 1fr;
            }
            .container {
                margin: 10px;
                padding: 30px 20px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="logo">⚡</div>
            <h2>Inscription</h2>
            <p style="color: #666; margin: 10px 0 0 0;">Rejoignez Electricity Business</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="success">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <form method="post" action="register" id="registerForm">
            <div class="form-grid">
                <div class="form-group">
                    <label for="firstName">Prénom <span class="required">*</span></label>
                    <input type="text" id="firstName" name="firstName" required 
                           value="<%= request.getParameter("firstName") != null ? request.getParameter("firstName") : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="lastName">Nom <span class="required">*</span></label>
                    <input type="text" id="lastName" name="lastName" required 
                           value="<%= request.getParameter("lastName") != null ? request.getParameter("lastName") : "" %>">
                </div>
            </div>
            
            <div class="form-group">
                <label for="email">Email <span class="required">*</span></label>
                <input type="email" id="email" name="email" required 
                       value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
            </div>
            
            <div class="form-group">
                <label for="phone">Téléphone</label>
                <input type="tel" id="phone" name="phone" 
                       value="<%= request.getParameter("phone") != null ? request.getParameter("phone") : "" %>">
            </div>
            
            <div class="form-group">
                <label for="dateOfBirth">Date de naissance</label>
                <div class="date-input">
                    <input type="date" id="dateOfBirth" name="dateOfBirth" 
                           value="<%= request.getParameter("dateOfBirth") != null ? request.getParameter("dateOfBirth") : "" %>">
                </div>
            </div>
            
            <div class="form-group full-width">
                <label for="address">Adresse</label>
                <input type="text" id="address" name="address" 
                       value="<%= request.getParameter("address") != null ? request.getParameter("address") : "" %>">
            </div>
            
            <div class="form-grid">
                <div class="form-group">
                    <label for="postalCode">Code postal</label>
                    <input type="text" id="postalCode" name="postalCode" 
                           value="<%= request.getParameter("postalCode") != null ? request.getParameter("postalCode") : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="city">Ville</label>
                    <input type="text" id="city" name="city" 
                           value="<%= request.getParameter("city") != null ? request.getParameter("city") : "" %>">
                </div>
            </div>
            
            <div class="form-grid">
                <div class="form-group">
                    <label for="password">Mot de passe <span class="required">*</span></label>
                    <input type="password" id="password" name="password" minlength="8" required>
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">Confirmation <span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" minlength="8" required>
                </div>
            </div>
            
            <button type="submit">Créer mon compte</button>
        </form>
        
        <div class="links">
            <a href="verify">Déjà reçu un code ? Validez ici</a>
            <a href="login">Déjà un compte ? Connectez-vous</a>
            <a href="index">Retour à l'accueil</a>
        </div>
    </div>
    
    <script>
        // Validation côté client
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Les mots de passe ne correspondent pas');
                return false;
            }
            
            if (password.length < 8) {
                e.preventDefault();
                alert('Le mot de passe doit contenir au moins 8 caractères');
                return false;
            }
        });
    </script>
</body>
</html>
