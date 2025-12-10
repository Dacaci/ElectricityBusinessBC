<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Véhicules - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251022v5">
</head>
<body>
    <div class="header">
        <h1>Electricity Business</h1>
        <div class="user-info">
            <span id="welcomeMessage">Bienvenue</span>
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
            <div class="section-header">
                <h2>Mes Véhicules</h2>
                <a href="add-vehicle.jsp" class="btn btn-primary">Ajouter un véhicule</a>
            </div>
            
            <div id="messageContainer"></div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement des véhicules...</p>
            </div>

            <div id="vehiclesContainer" style="display: none;">
                <div id="vehiclesList"></div>
                <div id="vehiclesEmpty" class="no-data" style="display: none;">
                    Aucun véhicule enregistré. Ajoutez votre premier véhicule électrique !
                </div>
            </div>
        </div>
    </div>

    <script src="js/config.js"></script>
    <script src="js/jwt-utils.js"></script>
    <script>
        let CURRENT_USER_ID = null;

        if (!requireAuth()) {
            // Redirection automatique
        } else {
            CURRENT_USER_ID = getCurrentUserId();
            const user = getAuthUser();
            if (user) {
                document.getElementById('welcomeMessage').textContent = 'Bienvenue, ' + user.firstName + ' ' + user.lastName;
            }
            
            loadVehicles();
        }

        async function loadVehicles() {
            showLoading(true);
            try {
                const response = await fetch(API_BASE_URL + '/api/vehicles');
                if (!response.ok) {
                    throw new Error('Erreur lors du chargement des véhicules');
                }
                
                const vehicles = await response.json();
                displayVehicles(vehicles);
                showLoading(false);
            } catch (error) {
                                showError('Erreur lors du chargement des véhicules: ' + error.message);
                showLoading(false);
            }
        }

        function displayVehicles(vehicles) {
            const container = document.getElementById('vehiclesList');
            const emptyMessage = document.getElementById('vehiclesEmpty');
            
            if (!vehicles || vehicles.length === 0) {
                emptyMessage.style.display = 'block';
                container.innerHTML = '';
                return;
            }
            
            emptyMessage.style.display = 'none';
            
            let html = '<div class="cards-grid">';
            vehicles.forEach(vehicle => {
                const plugs = vehicle.compatiblePlugNames && vehicle.compatiblePlugNames.length > 0
                    ? vehicle.compatiblePlugNames.join(', ')
                    : 'Non spécifié';
                
                html += '<div class="card">';
                html += '<div class="card-header">';
                html += '<h3>' + vehicle.brand + ' ' + vehicle.model + '</h3>';
                html += '<span class="badge">' + vehicle.licensePlate + '</span>';
                html += '</div>';
                html += '<div class="card-body">';
                if (vehicle.year) {
                    html += '<p><strong>Année :</strong> ' + vehicle.year + '</p>';
                }
                if (vehicle.batteryCapacity) {
                    html += '<p><strong>Capacité batterie :</strong> ' + vehicle.batteryCapacity + ' kWh</p>';
                }
                html += '<p><strong>Prises compatibles :</strong> ' + plugs + '</p>';
                html += '</div>';
                html += '<div class="card-actions">';
                html += '<button class="btn btn-danger" onclick="deleteVehicle(' + vehicle.id + ', \'' + vehicle.licensePlate + '\')">Supprimer</button>';
                html += '</div>';
                html += '</div>';
            });
            html += '</div>';
            
            container.innerHTML = html;
        }

        async function deleteVehicle(id, licensePlate) {
            if (!confirm('Êtes-vous sûr de vouloir supprimer le véhicule ' + licensePlate + ' ?')) {
                return;
            }
            
            try {
                const response = await fetch(API_BASE_URL + '/api/vehicles/' + id, {
                    method: 'DELETE'
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la suppression');
                }
                
                showSuccess('Véhicule supprimé avec succès !');
                loadVehicles();
            } catch (error) {
                                showError('Erreur lors de la suppression du véhicule');
            }
        }

        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('vehiclesContainer').style.display = show ? 'none' : 'block';
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
</body>
</html>







