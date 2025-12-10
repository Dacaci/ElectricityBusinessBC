<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Electricity Business</title>
    <style>
        body { 
            font-family: 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            margin: 0; 
            background: linear-gradient(135deg, #1e293b 0%, #1E40AF 100%);
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
        h2 { 
            color: #333; 
            margin: 0;
            font-weight: 400;
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
        input[type="text"], input[type="email"], input[type="password"], input[type="tel"] { 
            width: 100%; 
            padding: 12px 16px; 
            border: 2px solid #e1e8ed; 
            border-radius: 8px; 
            font-size: 16px;
            transition: border-color 0.3s ease;
            box-sizing: border-box;
        }
        input:focus {
            outline: none;
            border-color: #1E40AF;
            box-shadow: 0 0 0 3px rgba(30, 64, 175, 0.1);
        }
        button { 
            width: 100%; 
            padding: 16px; 
            background: linear-gradient(135deg, #1E40AF 0%, #1e293b 100%);
            color: white; 
            border: none; 
            border-radius: 8px; 
            font-size: 16px; 
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s ease;
        }
        button:hover { 
            transform: translateY(-2px);
        }
        .links { 
            text-align: center; 
            margin-top: 30px; 
            padding-top: 20px;
            border-top: 1px solid #e1e8ed;
        }
        .links a { 
            color: #1E40AF; 
            text-decoration: none;
        }
        .links a:hover {
            text-decoration: underline;
        }
        .message {
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
        }
        .error {
            background: #fee;
            color: #c00;
            border: 1px solid #e74c3c;
        }
        .success {
            background: #efe;
            color: #060;
            border: 1px solid #27ae60;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>Inscription</h2>
            <p style="color: #666; margin: 10px 0 0 0;">Rejoignez Electricity Business</p>
        </div>
        
        <%-- Messages d'erreur/succès (JSP au lieu de Thymeleaf) --%>
        <% if (request.getAttribute("error") != null) { %>
            <div class="message error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="message success"><%= request.getAttribute("success") %></div>
        <% } %>
        
        <%-- Formulaire d'inscription (action vers le Servlet) --%>
        <form method="POST" action="<%= request.getContextPath() %>/register-servlet">
            <div class="form-grid">
                <div class="form-group">
                    <label for="firstName">Prénom <span class="required">*</span></label>
                    <input type="text" id="firstName" name="firstName" required 
                           placeholder="Ex: Jean" value="<%= request.getParameter("firstName") != null ? request.getParameter("firstName") : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="lastName">Nom <span class="required">*</span></label>
                    <input type="text" id="lastName" name="lastName" required 
                           placeholder="Ex: Dupont" value="<%= request.getParameter("lastName") != null ? request.getParameter("lastName") : "" %>">
                </div>
            </div>
            
            <div class="form-group full-width">
                <label for="email">Email <span class="required">*</span></label>
                <input type="email" id="email" name="email" required 
                       placeholder="votre.email@exemple.com" value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
            </div>
            
            <div class="form-group full-width">
                <label for="phone">Téléphone</label>
                <input type="tel" id="phone" name="phone" 
                       placeholder="+33 6 12 34 56 78" value="<%= request.getParameter("phone") != null ? request.getParameter("phone") : "" %>">
            </div>
            
            <div class="form-grid">
                <div class="form-group">
                    <label for="password">Mot de passe <span class="required">*</span></label>
                    <input type="password" id="password" name="password" required 
                           placeholder="Minimum 8 caractères">
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">Confirmer <span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required 
                           placeholder="Confirmer le mot de passe">
                </div>
            </div>
            
            <button type="submit">S'inscrire</button>
        </form>
        
        <div class="links">
            Déjà inscrit ? <a href="<%= request.getContextPath() %>/login">Se connecter</a>
        </div>
    </div>
    
    <script>
        // Validation côté client (JavaScript vanilla)
        document.querySelector('form').addEventListener('submit', function(e) {
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

