<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ajouter un Véhicule - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251022v5">
</head>
<body>
    <div class="header">
        <h1>Electricity Business</h1>
        <div class="user-info">
            <span id="welcomeMessage">Bienvenue</span>
            <span class="status-badge status-active">Actif</span>
            <a href="#" onclick="logout(); return false;">Déconnexion</a>
        </div>
    </div>
    
    <nav class="navigation">
        <a href="dashboard.jsp" class="nav-link">Tableau de bord</a>
        <a href="add-location.jsp" class="nav-link">Ajouter un lieu</a>
        <a href="locations.jsp" class="nav-link">Mes lieux</a>
        <a href="add-station.jsp" class="nav-link">Ajouter une borne</a>
        <a href="stations.jsp" class="nav-link">Mes bornes</a>
        <a href="add-reservation.jsp" class="nav-link">Réserver</a>
        <a href="reservations.jsp" class="nav-link">Mes réservations</a>
        <a href="vehicles.jsp" class="nav-link active">Mes véhicules</a>
        <a href="map.jsp" class="nav-link">Carte</a>
    </nav>

    <div class="container">
        <div class="content">
            <h2>Ajouter un Véhicule Électrique</h2>
            
            <div id="messageContainer"></div>

            <form id="vehicleForm" class="form-container">
                <div class="form-group">
                    <label for="licensePlate">Plaque d'immatriculation</label>
                    <input type="text" id="licensePlate" name="licensePlate" class="form-control" required 
                           placeholder="Ex: AB-123-CD">
                </div>

                <div class="form-group">
                    <label for="brand">Marque</label>
                    <input type="text" id="brand" name="brand" class="form-control" required 
                           placeholder="Ex: Tesla, Renault, Peugeot">
                </div>

                <div class="form-group">
                    <label for="model">Modèle</label>
                    <input type="text" id="model" name="model" class="form-control" required 
                           placeholder="Ex: Model 3, Zoe, e-208">
                </div>

                <div class="form-group">
                    <label for="year">Année</label>
                    <input type="number" id="year" name="year" class="form-control" 
                           min="2000" max="2030" placeholder="Ex: 2023">
                </div>

                <div class="form-group">
                    <label for="batteryCapacity">Capacité de la batterie (kWh)</label>
                    <input type="number" id="batteryCapacity" name="batteryCapacity" class="form-control" 
                           step="0.1" min="0" placeholder="Ex: 50">
                </div>

                <div class="form-group">
                    <label>Types de prises compatibles</label>
                    <div id="plugTypesContainer" class="checkbox-group"></div>
                    <div id="plugTypesLoading" class="loading">Chargement des types de prises...</div>
                </div>

                <div class="actions">
                    <a href="vehicles.jsp" class="btn btn-secondary">Annuler</a>
                    <button type="submit" class="btn btn-primary">Ajouter le véhicule</button>
                </div>
            </form>
        </div>
    </div>

    <script src="js/jwt-utils.js"></script>
    <script>
        let CURRENT_USER_ID = null;
        let plugTypes = [];

        if (!requireAuth()) {
            // Redirection automatique
        } else {
            CURRENT_USER_ID = getCurrentUserId();
            const user = getAuthUser();
            if (user) {
                document.getElementById('welcomeMessage').textContent = 'Bienvenue, ' + user.firstName + ' ' + user.lastName;
            }
            
            loadPlugTypes();
            document.getElementById('vehicleForm').addEventListener('submit', handleSubmit);
        }

        async function loadPlugTypes() {
            try {
                const response = await fetch('http://localhost:8080/api/plug-types');
                if (!response.ok) {
                    throw new Error('Erreur lors du chargement des types de prises');
                }
                
                plugTypes = await response.json();
                displayPlugTypes();
            } catch (error) {
                console.error('Erreur:', error);
                document.getElementById('plugTypesLoading').style.display = 'none';
                showError('Erreur lors du chargement des types de prises');
            }
        }

        function displayPlugTypes() {
            const container = document.getElementById('plugTypesContainer');
            const loading = document.getElementById('plugTypesLoading');
            
            loading.style.display = 'none';
            
            if (!plugTypes || plugTypes.length === 0) {
                container.innerHTML = '<p class="text-muted">Aucun type de prise disponible</p>';
                return;
            }
            
            let html = '';
            plugTypes.forEach(plugType => {
                html += '<div class="checkbox-item">';
                html += '<input type="checkbox" id="plug_' + plugType.id + '" name="plugTypes" value="' + plugType.id + '">';
                html += '<label for="plug_' + plugType.id + '">' + plugType.name;
                if (plugType.maxPower) {
                    html += ' (' + plugType.maxPower + ' kW)';
                }
                html += '</label>';
                html += '</div>';
            });
            
            container.innerHTML = html;
        }

        async function handleSubmit(event) {
            event.preventDefault();
            
            const licensePlate = document.getElementById('licensePlate').value.trim();
            const brand = document.getElementById('brand').value.trim();
            const model = document.getElementById('model').value.trim();
            const year = document.getElementById('year').value;
            const batteryCapacity = document.getElementById('batteryCapacity').value;
            
            // Récupérer les types de prises sélectionnés
            const selectedPlugs = [];
            document.querySelectorAll('input[name="plugTypes"]:checked').forEach(checkbox => {
                selectedPlugs.push(parseInt(checkbox.value));
            });
            
            const vehicleData = {
                licensePlate: licensePlate,
                brand: brand,
                model: model,
                year: year ? parseInt(year) : null,
                batteryCapacity: batteryCapacity ? parseFloat(batteryCapacity) : null,
                compatiblePlugIds: selectedPlugs
            };
            
            try {
                const response = await fetch('http://localhost:8080/api/vehicles?userId=' + CURRENT_USER_ID, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(vehicleData)
                });
                
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || 'Erreur lors de l\'ajout du véhicule');
                }
                
                showSuccess('Véhicule ajouté avec succès !');
                setTimeout(() => {
                    window.location.href = 'vehicles.jsp';
                }, 1500);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de l\'ajout du véhicule: ' + error.message);
            }
        }

        function showError(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="error">' + message + '</div>';
            setTimeout(() => { container.innerHTML = ''; }, 5000);
        }

        function showSuccess(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="success">' + message + '</div>';
            setTimeout(() => { container.innerHTML = ''; }, 3000);
        }

        function logout() {
            localStorage.clear();
            sessionStorage.clear();
            window.location.replace('/login.jsp?message=logout');
        }
    </script>
    
    <style>
        .checkbox-group {
            display: flex;
            flex-direction: column;
            gap: 10px;
            margin-top: 10px;
        }
        
        .checkbox-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .checkbox-item input[type="checkbox"] {
            width: auto;
            margin: 0;
        }
        
        .checkbox-item label {
            margin: 0;
            cursor: pointer;
        }
        
        .text-muted {
            color: #6b7280;
            font-size: 14px;
        }
    </style>
</body>
</html>

