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
            padding: 20px;
        }
        .container { 
            background: white; 
            padding: 40px; 
            border-radius: 12px; 
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 700px;
        }
        .header {
            text-align: center;
            margin-bottom: 40px;
        }
        h2 { 
            color: #333; 
            margin: 0;
            font-weight: 600;
            font-size: 28px;
        }
        .form-group { 
            display: flex;
            align-items: center;
            margin-bottom: 24px;
            gap: 20px;
        }
        label { 
            min-width: 180px;
            color: #333; 
            font-weight: 500;
            font-size: 14px;
            text-align: right;
        }
        .required { 
            color: #e74c3c; 
            margin-left: 2px;
        }
        input[type="text"], 
        input[type="email"], 
        input[type="password"], 
        input[type="tel"], 
        input[type="date"],
        select { 
            flex: 1;
            padding: 12px 16px; 
            border: 2px solid #e1e8ed; 
            border-radius: 8px; 
            font-size: 16px;
            transition: border-color 0.3s ease;
            box-sizing: border-box;
            font-family: inherit;
        }
        input:focus, select:focus {
            outline: none;
            border-color: #1E40AF;
            box-shadow: 0 0 0 3px rgba(30, 64, 175, 0.1);
        }
        /* Date picker avec icône calendrier */
        .date-input-wrapper {
            flex: 1;
            position: relative;
        }
        .date-input-wrapper input[type="date"] {
            width: 100%;
            padding-right: 40px;
        }
        .date-input-wrapper::after {
            content: "📅";
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            pointer-events: none;
            font-size: 18px;
        }
        /* Dropdown pour la ville */
        .city-wrapper {
            flex: 1;
            position: relative;
        }
        .city-wrapper select {
            width: 100%;
            appearance: none;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23333' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 12px center;
            padding-right: 40px;
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
            transition: transform 0.2s ease, box-shadow 0.2s ease;
            margin-top: 10px;
        }
        button:hover { 
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
        }
        .links { 
            text-align: center; 
            margin-top: 30px; 
            padding-top: 20px;
            border-top: 1px solid #e1e8ed;
            color: #666;
            font-size: 14px;
        }
        .links a { 
            color: #1E40AF; 
            text-decoration: none;
            font-weight: 500;
        }
        .links a:hover {
            text-decoration: underline;
        }
        .message {
            padding: 12px 16px;
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
        @media (max-width: 600px) {
            .form-group {
                flex-direction: column;
                align-items: flex-start;
            }
            label {
                min-width: auto;
                text-align: left;
            }
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
                <label for="phone">Téléphone <span class="required">*</span></label>
                <input type="tel" id="phone" name="phone" required
                       placeholder="+33 6 12 34 56 78" value="<%= request.getParameter("phone") != null ? request.getParameter("phone") : "" %>">
            </div>
            
            <div class="form-group full-width">
                <label for="dateOfBirth">Date de naissance <span class="required">*</span></label>
                <input type="date" id="dateOfBirth" name="dateOfBirth" required
                       value="<%= request.getParameter("dateOfBirth") != null ? request.getParameter("dateOfBirth") : "" %>">
            </div>
            
            <div class="form-group full-width">
                <label for="address">Adresse <span class="required">*</span></label>
                <input type="text" id="address" name="address" required
                       placeholder="Ex: 123 Rue de la Paix" value="<%= request.getParameter("address") != null ? request.getParameter("address") : "" %>">
            </div>
            
            <div class="form-grid">
                <div class="form-group">
                    <label for="postalCode">Code postal <span class="required">*</span></label>
                    <input type="text" id="postalCode" name="postalCode" required
                           placeholder="Ex: 75001" value="<%= request.getParameter("postalCode") != null ? request.getParameter("postalCode") : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="city">Ville <span class="required">*</span></label>
                    <input type="text" id="city" name="city" required
                           placeholder="Ex: Paris" value="<%= request.getParameter("city") != null ? request.getParameter("city") : "" %>">
                </div>
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
            
            <%-- Consentement RGPD --%>
            <div class="form-group full-width" style="margin-top: 20px;">
                <div style="display: flex; align-items: flex-start; gap: 12px;">
                    <input type="checkbox" id="rgpdConsent" name="rgpdConsent" required 
                           style="width: 20px; height: 20px; margin-top: 2px; flex-shrink: 0; cursor: pointer;">
                    <label for="rgpdConsent" style="text-align: left; min-width: auto; flex: 1; font-size: 14px; line-height: 1.5; cursor: pointer;">
                        J'accepte la <a href="<%= System.getenv("FRONTEND_URL") != null ? System.getenv("FRONTEND_URL") : "https://electricity-business-frontend.onrender.com" %>/privacy-policy" target="_blank" style="color: #1E40AF; text-decoration: underline;">politique de confidentialité</a> 
                        et consens au traitement de mes données personnelles conformément au RGPD. 
                        <span class="required">*</span>
                    </label>
                </div>
            </div>
            
            <button type="submit">S'inscrire</button>
        </form>
        
        <div class="links">
            Déjà inscrit ? <a href="<%= request.getContextPath() %>/login">Se connecter</a>
        </div>
    </div>
    
    <script>
        // Gestion du dropdown "Autre" pour la ville
        document.getElementById('city').addEventListener('change', function() {
            const cityOtherInput = document.getElementById('cityOther');
            if (this.value === 'Autre') {
                cityOtherInput.style.display = 'block';
                cityOtherInput.required = true;
            } else {
                cityOtherInput.style.display = 'none';
                cityOtherInput.required = false;
                cityOtherInput.value = '';
            }
        });
        
        // Si "Autre" est déjà sélectionné au chargement
        if (document.getElementById('city').value === 'Autre') {
            document.getElementById('cityOther').style.display = 'block';
        }
        
        // Gestion de la soumission : utiliser cityOther si "Autre" est sélectionné
        document.querySelector('form').addEventListener('submit', function(e) {
            const citySelect = document.getElementById('city');
            const cityOtherInput = document.getElementById('cityOther');
            
            // Si "Autre" est sélectionné, remplacer la valeur du select par celle de l'input
            if (citySelect.value === 'Autre') {
                if (!cityOtherInput.value || cityOtherInput.value.trim() === '') {
                    e.preventDefault();
                    alert('Veuillez saisir votre ville');
                    return false;
                }
                // Créer un input caché avec la valeur de la ville
                const hiddenInput = document.createElement('input');
                hiddenInput.type = 'hidden';
                hiddenInput.name = 'city';
                hiddenInput.value = cityOtherInput.value.trim();
                citySelect.name = 'citySelect'; // Renommer l'ancien pour qu'il ne soit pas envoyé
                this.appendChild(hiddenInput);
            }
            
            // Validation des mots de passe
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



