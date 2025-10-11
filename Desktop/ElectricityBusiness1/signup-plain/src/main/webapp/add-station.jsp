<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ajouter une Borne - Electricity Business</title>
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
            background-color: #2196F3;
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
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
        
        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #2196F3;
        }
        
        .location-link {
            color: #007bff;
            text-decoration: none;
            font-size: 14px;
        }
        
        .location-link:hover {
            text-decoration: underline;
        }
        
        .checkbox-label {
            display: flex;
            align-items: center;
            gap: 8px;
            font-weight: normal;
            cursor: pointer;
        }
        
        .checkbox-label input[type="checkbox"] {
            width: auto;
            margin: 0;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        .btn {
            background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
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
            box-shadow: 0 4px 15px rgba(33, 150, 243, 0.3);
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(33, 150, 243, 0.4);
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
        
        .info-box {
            background: #e3f2fd;
            border: 1px solid #bbdefb;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
            color: #1565c0;
        }
        
        .info-box h4 {
            margin-bottom: 8px;
            font-size: 1.1em;
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
            <h1>➕ Ajouter une Borne</h1>
            <p>Créez une nouvelle borne de recharge électrique</p>
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
            
            <div class="info-box">
                <h4>ℹ️ Information importante</h4>
                <p>Toutes les bornes utilisent le type de prise TYPE2S, compatible avec la plupart des véhicules électriques.</p>
            </div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement des lieux disponibles...</p>
            </div>
            
            <div id="formContainer" class="form-container" style="display: none;">
                <form id="stationForm">
                    <div class="form-group">
                        <label for="locationId">Lieu <span class="required">*</span></label>
                        <select id="locationId" name="locationId" required>
                            <option value="">Sélectionnez un lieu...</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="name">Nom <span class="required">*</span></label>
                        <input type="text" id="name" name="name" placeholder="Borne 3" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="power">Puissance <span class="required">*</span></label>
                        <select id="power" name="power" required>
                            <option value="">Sélectionnez une puissance...</option>
                            <option value="3.7">3,7 KVA</option>
                            <option value="7.4" selected>7,4 KVA</option>
                            <option value="11">11 KVA</option>
                            <option value="22">22 KVA</option>
                            <option value="50">50 KVA</option>
                            <option value="150">150 KVA</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="city">Ville <span class="required">*</span></label>
                        <input type="text" id="city" name="city" placeholder="Sélectionnez une ville..." required>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="latitude">Latitude <span class="required">*</span></label>
                            <input type="text" id="latitude" name="latitude" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="longitude">Longitude <span class="required">*</span></label>
                            <input type="text" id="longitude" name="longitude" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <a href="#" id="useLocation" class="location-link">Utiliser ma position</a>
                    </div>
                    
                    <div class="form-group">
                        <label for="instructions">Instruction</label>
                        <textarea id="instructions" name="instructions" rows="4" placeholder="Tournez à droite dès que vous avez passé le portail d'entrée.&#10;La borne se situe à 20 mètres de distance par rapport au portail d'entrée."></textarea>
                    </div>
                    
                    <div class="form-group">
                        <label class="checkbox-label">
                            <input type="checkbox" id="onFoot" name="onFoot">
                            Sur pied
                        </label>
                    </div>
                    
                    <div class="form-actions">
                        <a href="stations.jsp" class="btn btn-secondary">❌ Annuler</a>
                        <button type="submit" class="btn">✅ Créer la borne</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        let locations = [];
        
        // Charger les lieux au chargement de la page
        document.addEventListener('DOMContentLoaded', function() {
            loadLocations();
            setupLocationButton();
        });
        
        async function loadLocations() {
            try {
                showLoading(true);
                const response = await fetch('http://localhost:8080/api/locations');
                
                if (!response.ok) {
                    throw new Error('Erreur lors du chargement des lieux');
                }
                
                const data = await response.json();
                // L'API retourne une structure paginée {content: [...], ...}
                locations = data.content || data;
                
                if (locations.length === 0) {
                    showError('Aucun lieu disponible. Veuillez d\'abord créer un lieu de recharge.');
                    return;
                }
                
                populateLocationSelect();
                showLoading(false);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors du chargement des lieux: ' + error.message);
                showLoading(false);
            }
        }
        
        function populateLocationSelect() {
            const select = document.getElementById('locationId');
            select.innerHTML = '<option value="">Sélectionnez un lieu...</option>';
            
            console.log('Locations récupérées:', locations);
            console.log('Nombre de locations:', locations.length);
            
            if (!locations || locations.length === 0) {
                select.innerHTML += '<option value="" disabled>Aucun lieu disponible - Créez-en un d\'abord</option>';
                document.getElementById('formContainer').style.display = 'block';
                return;
            }
            
            locations.forEach(location => {
                const option = document.createElement('option');
                option.value = location.id;
                const label = location.label || 'Sans nom';
                const address = location.address || 'Sans adresse';
                // Utiliser la concaténation au lieu de template literals
                option.textContent = label + ' - ' + address;
                console.log('Option créée:', option.textContent);
                select.appendChild(option);
            });
            
            document.getElementById('formContainer').style.display = 'block';
        }
        
        document.getElementById('stationForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const stationData = {
                name: formData.get('name'),
                locationId: parseInt(formData.get('locationId')),
                power: parseFloat(formData.get('power')),
                city: formData.get('city'),
                latitude: formData.get('latitude'),
                longitude: formData.get('longitude'),
                instructions: formData.get('instructions'),
                onFoot: formData.get('onFoot') === 'on',
                plugType: 'TYPE2S', // Toujours TYPE2S
                hourlyRate: 2.0, // Tarif par défaut
                isActive: true // Toujours actif
            };
            
            // Validation côté client
            if (!stationData.name || !stationData.locationId || !stationData.power || 
                !stationData.city || !stationData.latitude || !stationData.longitude) {
                showError('Veuillez remplir tous les champs obligatoires correctement');
                return;
            }
            
            try {
                showLoading(true);
                
                // Pour simplifier, on utilise l'utilisateur 1
                const response = await fetch('http://localhost:8080/api/stations?ownerId=1', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(stationData)
                });
                
                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Erreur lors de la création de la borne');
                }
                
                showSuccess('Borne créée avec succès !');
                setTimeout(() => {
                    window.location.href = 'stations.jsp';
                }, 2000);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la création: ' + error.message);
                showLoading(false);
            }
        });
        
        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('formContainer').style.display = show ? 'none' : 'block';
            
            if (show) {
                const submitBtn = document.querySelector('button[type="submit"]');
                if (submitBtn) {
                    submitBtn.innerHTML = '⏳ Création en cours...';
                    submitBtn.disabled = true;
                }
            } else {
                const submitBtn = document.querySelector('button[type="submit"]');
                if (submitBtn) {
                    submitBtn.innerHTML = '✅ Créer la borne';
                    submitBtn.disabled = false;
                }
            }
        }
        
        function setupLocationButton() {
            const useLocationBtn = document.getElementById('useLocation');
            useLocationBtn.addEventListener('click', function(e) {
                e.preventDefault();
                getCurrentLocation();
            });
        }
        
        function getCurrentLocation() {
            if (!navigator.geolocation) {
                showError('La géolocalisation n\'est pas supportée par ce navigateur');
                return;
            }
            
            showLoading(true);
            
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    const latitude = position.coords.latitude;
                    const longitude = position.coords.longitude;
                    
                    document.getElementById('latitude').value = latitude.toFixed(6);
                    document.getElementById('longitude').value = longitude.toFixed(6);
                    
                    showSuccess('Position géolocalisée avec succès !');
                    showLoading(false);
                },
                function(error) {
                    let errorMessage = 'Erreur lors de la géolocalisation : ';
                    switch(error.code) {
                        case error.PERMISSION_DENIED:
                            errorMessage += 'Permission refusée';
                            break;
                        case error.POSITION_UNAVAILABLE:
                            errorMessage += 'Position indisponible';
                            break;
                        case error.TIMEOUT:
                            errorMessage += 'Délai d\'attente dépassé';
                            break;
                        default:
                            errorMessage += 'Erreur inconnue';
                            break;
                    }
                    showError(errorMessage);
                    showLoading(false);
                }
            );
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





