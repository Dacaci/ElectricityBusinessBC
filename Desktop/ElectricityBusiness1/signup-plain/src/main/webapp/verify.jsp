<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vérification du compte - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251026">
    <style>
        .verify-container {
            max-width: 500px;
            margin: 50px auto;
            padding: 40px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .verify-header {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .verify-header h1 {
            color: #2c3e50;
            margin-bottom: 10px;
        }
        
        .verify-header p {
            color: #7f8c8d;
            margin: 0;
        }
        
        .form-group {
            margin-bottom: 25px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #34495e;
            font-weight: 500;
        }
        
        .form-group input {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e0e0e0;
            border-radius: 5px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus {
            outline: none;
            border-color: #3498db;
        }
        
        .code-input {
            text-align: center;
            font-size: 24px;
            letter-spacing: 8px;
            font-weight: bold;
            color: #2c3e50;
        }
        
        .btn-verify {
            width: 100%;
            padding: 15px;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.3s;
        }
        
        .btn-verify:hover {
            background: #2980b9;
        }
        
        .btn-verify:disabled {
            background: #bdc3c7;
            cursor: not-allowed;
        }
        
        .link-back {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #3498db;
            text-decoration: none;
        }
        
        .link-back:hover {
            text-decoration: underline;
        }
        
        .status-message {
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            text-align: center;
            font-weight: 500;
        }
        
        .status-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .status-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .progress-indicator {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            margin-top: 20px;
        }
        
        .progress-dot {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #e0e0e0;
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
        // Auto-focus sur le champ code
        document.addEventListener('DOMContentLoaded', function() {
            const emailInput = document.querySelector('input[name="email"]');
            const codeInput = document.querySelector('input[name="code"]');
            
            if (emailInput && codeInput) {
                // Si l'email est déjà rempli, focus sur le code
                if (emailInput.value) {
                    codeInput.focus();
                }
            }
        });
    </script>
</head>
<body>
    <div class="verify-container">
        <div class="verify-header">
            <h1>Vérification de votre compte</h1>
            <p>Entrez le code à 6 chiffres reçu par email</p>
        </div>
        
        <form method="post" action="verify">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" name="email" id="email" required placeholder="votre@email.com">
            </div>
            
            <div class="form-group">
                <label for="code">Code de vérification</label>
                <input type="text" name="code" id="code" required placeholder="123456" maxlength="6" class="code-input" autocomplete="off">
            </div>
            
            <button type="submit" class="btn-verify">Valider le code</button>
</form>
        
        <a href="register.jsp" class="link-back">← Retour à l'inscription</a>
    </div>
</body>
</html>
