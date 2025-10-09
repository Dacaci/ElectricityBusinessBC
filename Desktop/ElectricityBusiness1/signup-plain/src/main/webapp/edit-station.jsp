<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modifier une Borne - Electricity Business</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .header {
            background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
            color: #212529;
            padding: 30px;
            text-align: center;
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
            background-color: #ffc107;
            color: #212529;
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
        .form-group select {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #ffc107;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        .btn {
            background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
            color: #212529;
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.3s;
            box-shadow: 0 4px 15px rgba(255, 193, 7, 0.3);
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(255, 193, 7, 0.4);
        }
        
        .btn-secondary {
            background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
            color: white;
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
            <h1>✏️ Modifier une Borne</h1>
            <p>Modifiez les informations de votre borne de recharge</p>
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
                <p>Chargement des informations de la borne...</p>
            </div>
            
            <div id="formContainer" class="form-container" style="display: none;">
                <form id="stationForm">
                    <div class="form-group">
                        <label for="name">Nom de la borne <span class="required">*</span></label>
                        <input type="text" id="name" name="name" required maxlength="255" 
                               placeholder="Ex: Borne principale - Parking A">
                    </div>
                    
                    <div class="form-group">
                        <label for="locationId">Lieu de recharge <span class="required">*</span></label>
                        <select id="locationId" name="locationId" required>
                            <option value="">Sélectionnez un lieu...</option>
                        </select>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="hourlyRate">Tarif horaire (€/h) <span class="required">*</span></label>
                            <input type="number" id="hourlyRate" name="hourlyRate" step="0.01" min="0" required 
                                   placeholder="Ex: 2.50">
                        </div>
                        
                        <div class="form-group">
                            <label for="plugType">Type de prise</label>
                            <input type="text" id="plugType" name="plugType" value="TYPE2S" readonly 
                                   style="background-color: #f8f9fa; color: #6c757d;">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>
                            <input type="checkbox" id="isActive" name="isActive"> 
                            Borne active (disponible pour les réservations)
                        </label>
                    </div>
                    
                    <div class="form-actions">
                        <a href="stations.jsp" class="btn btn-secondary">❌ Annuler</a>
                        <button type="submit" class="btn">✅ Sauvegarder</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        let stationId = null;
        let locations = [];
        
        // Récupérer l'ID de la borne depuis l'URL
        const urlParams = new URLSearchParams(window.location.search);
        stationId = urlParams.get('id');
        
        if (!stationId) {
            showError('ID de la borne manquant');
            setTimeout(() => {
                window.location.href = 'stations.jsp';
            }, 2000);
        } else {
            loadData();
        }
        
        async function loadData() {
            try {
                showLoading(true);
                
                // Charger la borne et les lieux en parallèle
                const [stationResponse, locationsResponse] = await Promise.all([
                    fetch(`http://localhost:8080/api/stations/${stationId}`),
                    fetch('http://localhost:8080/api/locations')
                ]);
                
                if (!stationResponse.ok) {
                    throw new Error('Borne non trouvée');
                }
                
                if (!locationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des lieux');
                }
                
                const station = await stationResponse.json();
                locations = await locationsResponse.json();
                
                populateLocationSelect();
                populateForm(station);
                showLoading(false);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors du chargement: ' + error.message);
                showLoading(false);
            }
        }
        
        function populateLocationSelect() {
            const select = document.getElementById('locationId');
            select.innerHTML = '<option value="">Sélectionnez un lieu...</option>';
            
            locations.forEach(location => {
                const option = document.createElement('option');
                option.value = location.id;
                option.textContent = `${location.label} - ${location.address}`;
                select.appendChild(option);
            });
        }
        
        function populateForm(station) {
            document.getElementById('name').value = station.name || '';
            document.getElementById('locationId').value = station.locationId || '';
            document.getElementById('hourlyRate').value = station.hourlyRate || '';
            document.getElementById('isActive').checked = station.isActive || false;
            
            document.getElementById('formContainer').style.display = 'block';
        }
        
        document.getElementById('stationForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const stationData = {
                name: formData.get('name'),
                locationId: parseInt(formData.get('locationId')),
                hourlyRate: parseFloat(formData.get('hourlyRate')),
                plugType: 'TYPE2S', // Toujours TYPE2S
                isActive: formData.get('isActive') === 'on'
            };
            
            // Validation côté client
            if (!stationData.name || !stationData.locationId || 
                isNaN(stationData.hourlyRate) || stationData.hourlyRate < 0) {
                showError('Veuillez remplir tous les champs obligatoires correctement');
                return;
            }
            
            try {
                showLoading(true);
                
                const response = await fetch(`http://localhost:8080/api/stations/${stationId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(stationData)
                });
                
                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Erreur lors de la modification');
                }
                
                showSuccess('Borne modifiée avec succès !');
                setTimeout(() => {
                    window.location.href = 'stations.jsp';
                }, 2000);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la modification: ' + error.message);
                showLoading(false);
            }
        });
        
        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('formContainer').style.display = show ? 'none' : 'block';
            
            if (show) {
                const submitBtn = document.querySelector('button[type="submit"]');
                if (submitBtn) {
                    submitBtn.innerHTML = '⏳ Sauvegarde en cours...';
                    submitBtn.disabled = true;
                }
            } else {
                const submitBtn = document.querySelector('button[type="submit"]');
                if (submitBtn) {
                    submitBtn.innerHTML = '✅ Sauvegarder';
                    submitBtn.disabled = false;
                }
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





