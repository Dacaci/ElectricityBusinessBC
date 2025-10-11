<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ajouter un Lieu - Electricity Business</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: white;
            min-height: 100vh;
        }
        
        .container {
            width: 100%;
            padding: 20px;
        }
        
        .header {
            background: white;
            color: #333;
            padding: 30px;
            text-align: center;
            border-bottom: 1px solid #ddd;
        }
        
        .header h1 {
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        
        .header p {
            font-size: 1.1em;
            opacity: 0.9;
        }
        
        .nav {
            background: #f8f9fa;
            padding: 15px 30px;
            border-bottom: 1px solid #e9ecef;
        }
        
        .nav a {
            color: #495057;
            text-decoration: none;
            margin-right: 20px;
            padding: 8px 16px;
            border-radius: 5px;
            transition: background-color 0.3s;
        }
        
        .nav a:hover, .nav a.active {
            background-color: #4CAF50;
            color: white;
        }
        
        .content {
            padding: 40px;
        }
        
        .form-container {
            background: #f8f9fa;
            padding: 30px;
            border-radius: 12px;
            border: 1px solid #e9ecef;
        }
        
        .form-group {
            margin-bottom: 25px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #2c3e50;
        }
        
        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #4CAF50;
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        .btn {
            background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.3s;
            box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
        }
        
        .btn-secondary {
            background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
            box-shadow: 0 4px 15px rgba(108, 117, 125, 0.3);
        }
        
        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: flex-end;
            margin-top: 30px;
        }
        
        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }
        
        .success {
            background: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
        }
        
        .loading {
            text-align: center;
            padding: 20px;
            color: #6c757d;
        }
        
        .required {
            color: #dc3545;
        }
        
        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
            }
            
            .form-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>➕ Ajouter un Lieu</h1>
            <p>Créez un nouveau lieu de recharge électrique</p>
        </div>
        
        <div class="nav">
            <a href="dashboard.jsp">🏠 Tableau de bord</a>
            <a href="locations.jsp">📍 Lieux</a>
            <a href="stations.jsp">🔌 Bornes</a>
            <a href="reservations.jsp">📅 Réservations</a>
            <a href="map.jsp">🗺️ Carte</a>
            <a href="logout">🚪 Déconnexion</a>
        </div>
        
        <div class="content">
            <div id="messageContainer"></div>
            
            <div class="form-container">
                <form id="locationForm">
                    <div class="form-group">
                        <label for="label">Nom du lieu <span class="required">*</span></label>
                        <input type="text" id="label" name="label" required maxlength="255" 
                               placeholder="Ex: Parking du centre commercial">
                    </div>
                    
                    <div class="form-group">
                        <label for="address">Adresse complète <span class="required">*</span></label>
                        <textarea id="address" name="address" required maxlength="500" 
                                  placeholder="Ex: 123 Rue de la Paix, 75001 Paris, France"></textarea>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="latitude">Latitude <span class="required">*</span></label>
                            <input type="number" id="latitude" name="latitude" step="any" required 
                                   placeholder="Ex: 48.8566">
                        </div>
                        
                        <div class="form-group">
                            <label for="longitude">Longitude <span class="required">*</span></label>
                            <input type="number" id="longitude" name="longitude" step="any" required 
                                   placeholder="Ex: 2.3522">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="description">Description (optionnel)</label>
                        <textarea id="description" name="description" maxlength="1000" 
                                  placeholder="Informations supplémentaires sur le lieu..."></textarea>
                    </div>
                    
                    <div class="form-actions">
                        <a href="locations.jsp" class="btn btn-secondary">❌ Annuler</a>
                        <button type="submit" class="btn">✅ Créer le lieu</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        document.getElementById('locationForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const locationData = {
                label: formData.get('label'),
                address: formData.get('address'),
                latitude: formData.get('latitude'), // Garder en String pour BigDecimal
                longitude: formData.get('longitude'), // Garder en String pour BigDecimal
                description: formData.get('description') || null,
                ownerId: 1 // Pour simplifier
            };
            
            // Validation côté client
            if (!locationData.label || !locationData.address || 
                isNaN(locationData.latitude) || isNaN(locationData.longitude)) {
                showError('Veuillez remplir tous les champs obligatoires');
                return;
            }
            
            try {
                showLoading(true);
                
                // Pour simplifier, on utilise l'utilisateur 1
                const response = await fetch('http://localhost:8080/api/locations?ownerId=1', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(locationData)
                });
                
                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Erreur lors de la création du lieu');
                }
                
                showSuccess('Lieu créé avec succès !');
                setTimeout(() => {
                    window.location.href = 'locations.jsp';
                }, 2000);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la création: ' + error.message);
                showLoading(false);
            }
        });
        
        // Fonction pour obtenir les coordonnées GPS automatiquement
        function getCurrentLocation() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    function(position) {
                        document.getElementById('latitude').value = position.coords.latitude;
                        document.getElementById('longitude').value = position.coords.longitude;
                    },
                    function(error) {
                        console.log('Erreur de géolocalisation:', error);
                    }
                );
            }
        }
        
        // Ajouter un bouton pour la géolocalisation
        document.addEventListener('DOMContentLoaded', function() {
            const latField = document.getElementById('latitude');
            const lngField = document.getElementById('longitude');
            
            const geoButton = document.createElement('button');
            geoButton.type = 'button';
            geoButton.className = 'btn btn-secondary';
            geoButton.style.marginTop = '5px';
            geoButton.innerHTML = '📍 Utiliser ma position actuelle';
            geoButton.onclick = getCurrentLocation;
            
            lngField.parentNode.appendChild(geoButton);
        });
        
        function showLoading(show) {
            const submitBtn = document.querySelector('button[type="submit"]');
            if (show) {
                submitBtn.innerHTML = '⏳ Création en cours...';
                submitBtn.disabled = true;
            } else {
                submitBtn.innerHTML = '✅ Créer le lieu';
                submitBtn.disabled = false;
            }
        }
        
        function showError(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = `<div class="error">${message}</div>`;
        }
        
        function showSuccess(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = `<div class="success">${message}</div>`;
        }
    </script>
</body>
</html>





