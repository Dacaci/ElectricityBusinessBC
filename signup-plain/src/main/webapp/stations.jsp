<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="includes/backend-config.jsp" %>
<%
    String backendUrl = (String) request.getAttribute("BACKEND_URL");
    String csp = "default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; connect-src 'self' http://localhost:8080 " + backendUrl + "; script-src 'self' 'unsafe-inline' 'unsafe-eval';";
    response.setHeader("Content-Security-Policy", csp);
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Bornes - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251022v5">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Content-Security-Policy" content="default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; connect-src 'self' http://localhost:8080 <%= backendUrl %>; script-src 'self' 'unsafe-inline' 'unsafe-eval';">
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
        <a href="stations.jsp" class="nav-link active">Mes bornes</a>
        <a href="add-reservation.jsp" class="nav-link">Réserver</a>
        <a href="reservations.jsp" class="nav-link">Mes réservations</a>
        <a href="map.jsp" class="nav-link">Carte</a>
    </nav>
    
    <div class="container">
        
        <div class="content">
            <div class="actions">
                <div>
                    <a href="add-station.jsp" class="btn">Ajouter une borne</a>
                </div>
                <div class="search-box">
                    <input type="text" id="searchInput" placeholder="Rechercher une borne..." onkeyup="filterStations()">
                </div>
            </div>
            
            <div id="messageContainer"></div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement des bornes...</p>
            </div>
            
            <div id="stationsContainer" class="stations-grid" style="display: none;">
                <!-- Les bornes seront chargées ici via JavaScript -->
            </div>
            
            <div id="noStationsMessage" class="no-stations" style="display: none;">
                <h3>Aucune borne trouvée</h3>
                <p>Commencez par ajouter votre première borne de recharge.</p>
                <a href="add-station.jsp" class="btn" style="margin-top: 15px;">Ajouter une borne</a>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="js/config.js"></script>
    <script src="js/jwt-utils.js"></script>
    <script>
        let stations = [];
        let locations = [];
        let isDeleting = false; // Protection contre les double-clics
        
        // Charger les données au chargement de la page
        document.addEventListener('DOMContentLoaded', function() {
            loadData();
        });
        
        // Variables globales
        let CURRENT_USER_ID = null;
        
        // Récupérer l'ID de l'utilisateur depuis le token JWT
        if (typeof getCurrentUserId === 'function') {
            CURRENT_USER_ID = getCurrentUserId();
        }
        
        // Vérifier l'authentification
        if (!requireAuth()) {
            // L'utilisateur sera redirigé automatiquement par requireAuth()
        } else {
        
                                        } // Fermer le bloc else
        
        // ========== FONCTIONS GLOBALES ==========
        
        async function loadData() {
            try {
                showLoading(true);
                
                const stationsUrl = API_BASE_URL + '/api/stations/owner/' + CURRENT_USER_ID + '?t=' + Date.now();
                                                // Charger les bornes et les lieux en parallèle
                const [stationsResponse, locationsResponse] = await Promise.all([
                    fetch(stationsUrl), // Mes propres bornes
                    fetch(API_BASE_URL + '/api/locations')
                ]);
                
                if (!stationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des bornes');
                }
                
                if (!locationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des lieux');
                }
                
                const stationsData = await stationsResponse.json();
                const locationsData = await locationsResponse.json();
                
                                                // L'API retourne une structure paginée {content: [...], ...}
                stations = stationsData.content || stationsData;
                locations = locationsData.content || locationsData;
                
                                                displayStations(stations);
                showLoading(false);
                
            } catch (error) {
                                showError('Erreur lors du chargement: ' + error.message);
                showLoading(false);
            }
        }
        
        function displayStations(stationsToShow) {
            const container = document.getElementById('stationsContainer');
            const noStations = document.getElementById('noStationsMessage');
            
            if (stationsToShow.length === 0) {
                container.style.display = 'none';
                noStations.style.display = 'block';
                return;
            }
            
            container.innerHTML = stationsToShow.map(station => {
                const location = locations.find(loc => loc.id === station.locationId);
                const locationName = station.locationLabel || (location ? location.label : 'Lieu inconnu');
                const locationAddress = station.address || (location ? location.address : '');
                const statusBadge = station.isActive ? 'status-active' : 'status-inactive';
                const statusText = station.isActive ? 'Active' : 'Inactive';
                const toggleBtnClass = station.isActive ? 'btn-secondary' : 'btn-success';
                const toggleBtnText = station.isActive ? 'Désactiver' : 'Activer';
                
                return '<div class="station-card">' +
                    '<div class="station-header">' +
                        '<div>' +
                            '<div class="station-title">' + station.name + '</div>' +
                            '<div class="station-location">' + locationName + '</div>' +
                            '<div class="station-location" style="font-size: 0.85em; color: #6c757d;">' + locationAddress + '</div>' +
                        '</div>' +
                        '<span class="status-badge ' + statusBadge + '">' + statusText + '</span>' +
                    '</div>' +
                    '<div class="station-details">' +
                        '<div class="detail-item">' +
                            '<div class="detail-label">Type de prise</div>' +
                            '<div class="detail-value">' + station.plugType + '</div>' +
                        '</div>' +
                        '<div class="detail-item">' +
                            '<div class="detail-label">Tarif horaire</div>' +
                            '<div class="detail-value">' + station.hourlyRate + ' €/h</div>' +
                        '</div>' +
                    '</div>' +
                    '<div class="station-actions">' +
                        '<a href="edit-station.jsp?id=' + station.id + '" class="btn btn-warning">Modifier</a>' +
                        '<button onclick="toggleStationStatus(' + station.id + ', ' + station.isActive + ')" class="btn ' + toggleBtnClass + '">' + toggleBtnText + '</button>' +
                        '<button onclick="deleteStation(' + station.id + ')" class="btn btn-danger">Supprimer</button>' +
                    '</div>' +
                '</div>';
            }).join('');
            
            container.style.display = 'grid';
            noStations.style.display = 'none';
        }
        
        function filterStations() {
            const searchTerm = document.getElementById('searchInput').value.toLowerCase();
            const filteredStations = stations.filter(station => 
                station.name.toLowerCase().includes(searchTerm) ||
                station.plugType.toLowerCase().includes(searchTerm)
            );
            displayStations(filteredStations);
        }
        
        async function toggleStationStatus(stationId, currentStatus) {
            try {
                // PATCH - envoyer uniquement le champ à modifier
                const response = await fetch(API_BASE_URL + '/api/stations/' + stationId, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        isActive: !currentStatus
                    })
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la modification');
                }
                
                const newStatus = !currentStatus ? 'activée' : 'désactivée';
                showSuccess('Borne ' + newStatus + ' avec succès');
                loadData(); // Recharger la liste
                
            } catch (error) {
                                showError('Erreur lors de la modification: ' + error.message);
            }
        }
        
        async function deleteStation(stationId) {
                        // Empêcher les clics multiples
            if (isDeleting) {
                                return;
            }
            
            if (!stationId) {
                alert('Erreur: ID de borne manquant');
                return;
            }
            
            if (!confirm('Êtes-vous sûr de vouloir supprimer cette borne ?')) {
                                return;
            }
            
            isDeleting = true; // Verrouiller
            
            try {
                const url = API_BASE_URL + '/api/stations/' + stationId;
                                const response = await fetch(url, {
                    method: 'DELETE'
                });
                
                                if (!response.ok) {
                    if (response.status === 404) {
                        showError('Cette borne a déjà été supprimée');
                        isDeleting = false;
                        setTimeout(() => loadData(), 500);
                        return;
                    }
                    isDeleting = false;
                    throw new Error('Erreur lors de la suppression');
                }
                
                showSuccess('Borne supprimée avec succès');
                // Délai avant rechargement pour éviter les appels multiples
                setTimeout(() => {
                    loadData();
                    isDeleting = false; // Déverrouiller après rechargement
                }, 1000);
                
            } catch (error) {
                                showError('Erreur lors de la suppression: ' + error.message);
                isDeleting = false; // Déverrouiller en cas d'erreur
            }
        }
        
        function refreshStations() {
            loadData();
        }
        
        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('stationsContainer').style.display = show ? 'none' : 'grid';
            document.getElementById('noStationsMessage').style.display = 'none';
        }
        
        function showError(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="error">' + message + '</div>';
            setTimeout(function() {
                container.innerHTML = '';
            }, 5000);
        }
        
        function showSuccess(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="success">' + message + '</div>';
            setTimeout(function() {
                container.innerHTML = '';
            }, 3000);
        }
    </script>
    <script src="js/auth.js"></script>
</body>
</html>





