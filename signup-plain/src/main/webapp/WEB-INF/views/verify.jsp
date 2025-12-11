<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vérification du compte - Electricity Business</title>
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
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 500px;
            margin: 20px;
        }
        h2 {
            color: #1e293b;
            text-align: center;
            margin-bottom: 10px;
            font-weight: 400;
        }
        p {
            color: #64748b;
            text-align: center;
            margin-bottom: 30px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            color: #1e293b;
            margin-bottom: 8px;
            font-weight: 500;
            font-size: 14px;
        }
        input {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            box-sizing: border-box;
            transition: border-color 0.3s ease;
        }
        input:focus {
            outline: none;
            border-color: #1E40AF;
            box-shadow: 0 0 0 3px rgba(30, 64, 175, 0.1);
        }
        .btn {
            width: 100%;
            padding: 14px;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            border: none;
            transition: all 0.2s;
        }
        .btn-primary {
            background: #1E40AF;
            color: white;
        }
        .btn-primary:hover {
            background: #1e293b;
        }
        .btn-secondary {
            background: #94a3b8;
            color: white;
            margin-top: 10px;
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
        .links {
            text-align: center;
            margin-top: 25px;
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
    </style>
</head>
<body>
    <div class="container">
        <h2>Vérification du compte</h2>
        <p>Entrez le code de vérification reçu par email</p>
        
        <%-- Messages d'erreur/succès JSP --%>
        <% if (request.getAttribute("error") != null) { %>
            <div class="message error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="message success"><%= request.getAttribute("success") %></div>
        <% } %>
        
        <%-- Formulaire de vérification (POST vers Servlet) --%>
        <form method="POST" action="<%= request.getContextPath() %>/verify-servlet">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required 
                       value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : (request.getParameter("email") != null ? request.getParameter("email") : "") %>"
                       <%= request.getParameter("email") != null ? "readonly" : "" %>>
            </div>
            
            <div class="form-group">
                <label for="verificationCode">Code de vérification</label>
                <input type="text" id="verificationCode" name="verificationCode" required 
                       placeholder="Ex: ABC123" maxlength="6"
                       style="text-transform: uppercase;">
            </div>
            
            <button type="submit" class="btn btn-primary">Vérifier mon compte</button>
            <button type="button" class="btn btn-secondary" onclick="window.location.href='<%= request.getContextPath() %>/register-servlet'">
                Retour à l'inscription
            </button>
        </form>
        
        <div class="links">
            Déjà vérifié ? <a href="<%= request.getContextPath() %>/login">Se connecter</a>
        </div>
    </div>
    
    <script>
        // Auto-uppercase du code
        document.getElementById('verificationCode').addEventListener('input', function(e) {
            e.target.value = e.target.value.toUpperCase();
        });
    </script>
</body>
</html>



