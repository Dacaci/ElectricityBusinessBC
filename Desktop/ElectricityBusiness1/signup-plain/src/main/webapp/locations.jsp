<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    response.setHeader("Content-Security-Policy", "default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; connect-src 'self' http://localhost:8080; script-src 'self' 'unsafe-inline' 'unsafe-eval';");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Lieux - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251021v2">
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
        <a href="locations.jsp" class="nav-link active">Mes lieux</a>
        <a href="add-station.jsp" class="nav-link">Ajouter une borne</a>
        <a href="stations.jsp" class="nav-link">Mes bornes</a>
        <a href="add-reservation.jsp" class="nav-link">Réserver</a>
        <a href="reservations.jsp" class="nav-link">Mes réservations</a>
        <a href="map.jsp" class="nav-link">Carte</a>
    </nav>
    
    <div class="container">
        
        <div class="content">
            <div class="actions">
                <div>
                    <a href="add-location.jsp" class="btn">Ajouter un lieu</a>
                </div>
                <div class="search-box">
                    <input type="text" id="searchInput" placeholder="Rechercher un lieu..." onkeyup="filterLocations()">
                    <button class="btn btn-secondary" onclick="refreshLocations()">Actualiser</button>
                </div>
            </div>
            
            <div id="messageContainer"></div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement des lieux...</p>
            </div>
            
            <div id="locationsContainer" class="locations-grid" style="display: none;">
                <!-- Les lieux seront chargés ici via JavaScript -->
            </div>
            
            <div id="noLocationsMessage" class="no-locations" style="display: none;">
                <h3>Aucun lieu trouvé</h3>
                <p>Commencez par ajouter votre premier lieu de recharge.</p>
                <a href="add-location.jsp" class="btn" style="margin-top: 15px;">Ajouter un lieu</a>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="js/jwt-utils.js"></script>
    <script>
        // Variables globales
        let locations = [];
        let stations = [];
        let stationsByLocation = {}; // Pour grouper les stations par lieu
        let isDeletingStation = false; // Protection contre les double-clics
        let isDeletingLocation = false; // Protection contre les double-clics
        let CURRENT_USER_ID = null;
        
        // Récupérer l'ID de l'utilisateur depuis le token JWT (sera null si pas authentifié)
        if (typeof getCurrentUserId === 'function') {
            CURRENT_USER_ID = getCurrentUserId();
        }
        
        // Vérifier l'authentification
        if (!requireAuth()) {
            // L'utilisateur sera redirigé automatiquement par requireAuth()
        } else {
        
        console.log('=== LOCATIONS.JSP DEBUG ===');
        console.log('CURRENT_USER_ID:', CURRENT_USER_ID);
        console.log('Type:', typeof CURRENT_USER_ID);
        console.log('===========================');
        
        } // Fermer le bloc else
        
        // ========== FONCTIONS GLOBALES (définies en dehors du bloc else) ==========
        
        async function loadLocationsAndStations() {
            try {
                showLoading(true);
                
                // Charger les lieux et stations en parallèle
                const [locationsResponse, stationsResponse] = await Promise.all([
                    fetch('http://localhost:8080/api/locations'),
                    fetch('http://localhost:8080/api/stations/owner/' + CURRENT_USER_ID) // Mes propres bornes
                ]);
                
                if (!locationsResponse.ok || !stationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des données');
                }
                
                const locationsData = await locationsResponse.json();
                const stationsData = await stationsResponse.json();
                
                // L'API retourne une structure paginée {content: [...], ...}
                const allLocations = locationsData.content || locationsData;
                locations = allLocations.filter(loc => {
                    const ownerId = (loc.ownerId != null) ? Number(loc.ownerId) : (loc.owner && Number(loc.owner.id));
                    return Number(ownerId) === Number(CURRENT_USER_ID);
                });
                stations = stationsData.content || stationsData;
                
                // Grouper les stations par lieu
                stationsByLocation = {};
                stations.forEach(station => {
                    const locationId = station.locationId;
                    if (!stationsByLocation[locationId]) {
                        stationsByLocation[locationId] = [];
                    }
                    stationsByLocation[locationId].push(station);
                });
                
                displayLocationsWithStations(locations);
                showLoading(false);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors du chargement des données: ' + error.message);
                showLoading(false);
            }
        }
        
        function displayLocationsWithStations(locationsToShow) {
            const container = document.getElementById('locationsContainer');
            const noLocations = document.getElementById('noLocationsMessage');
            
            if (locationsToShow.length === 0) {
                container.style.display = 'none';
                noLocations.style.display = 'block';
                return;
            }
            
            container.innerHTML = locationsToShow.map(location => {
                const locationStations = stationsByLocation[location.id] || [];
                
                let stationsHtml = '';
                if (locationStations.length > 0) {
                    stationsHtml = locationStations.map(station => {
                        const statusBadge = station.isActive ? 'status-active' : 'status-inactive';
                        const statusText = station.isActive ? 'Active' : 'Inactive';
                        return '<div class="station-card" style="margin-bottom: 12px;">' +
                            '<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">' +
                                '<div style="font-weight: 600;">' + (station.name || 'Borne sans nom') + '</div>' +
                                '<span class="status-badge ' + statusBadge + '">' + statusText + '</span>' +
                            '</div>' +
                            '<div style="display: flex; gap: 8px; flex-wrap: wrap;">' +
                                '<a href="edit-station.jsp?id=' + station.id + '" class="btn">Modifier</a>' +
                                '<a href="station-rates.jsp?id=' + station.id + '" class="btn">Tarifs</a>' +
                                '<button onclick="uploadPhoto(' + station.id + ')" class="btn">Photo</button>' +
                                '<button onclick="uploadVideo(' + station.id + ')" class="btn">Vidéo</button>' +
                                '<button onclick="deleteStation(' + station.id + ')" class="btn btn-danger">Supprimer</button>' +
                            '</div>' +
                        '</div>';
                    }).join('');
                } else {
                    stationsHtml = '<div class="no-stations">Aucune borne dans ce lieu</div>';
                }
                
                return '<div class="location-card">' +
                    '<div class="location-header">' +
                        '<h3>' + (location.label || 'Lieu sans nom') + '</h3>' +
                        '<div class="location-address">' + (location.address || 'Adresse non spécifiée') + '</div>' +
                    '</div>' +
                    '<div class="stations-container" style="margin: 16px 0;">' +
                        stationsHtml +
                    '</div>' +
                    '<div class="location-actions">' +
                        '<a href="add-station.jsp?locationId=' + location.id + '" class="btn">Ajouter borne</a>' +
                        '<a href="edit-location.jsp?id=' + location.id + '" class="btn">Modifier lieu</a>' +
                        '<button onclick="deleteLocation(' + location.id + '); return false;" class="btn btn-danger">Supprimer</button>' +
                    '</div>' +
                '</div>';
            }).join('');
            
            container.style.display = 'grid';
            noLocations.style.display = 'none';
        }
        
        function filterLocations() {
            const searchTerm = document.getElementById('searchInput').value.toLowerCase();
            const filteredLocations = locations.filter(location => 
                location.label.toLowerCase().includes(searchTerm) ||
                location.address.toLowerCase().includes(searchTerm)
            );
            displayLocationsWithStations(filteredLocations);
        }
        
        // Nouvelles fonctions pour les actions des stations
        function uploadPhoto(stationId) {
            alert('Fonctionnalité de téléversement de photo pour la station ' + stationId + ' à implémenter');
        }
        
        function uploadVideo(stationId) {
            alert('Fonctionnalité de téléversement de vidéo pour la station ' + stationId + ' à implémenter');
        }
        
        async function deleteStation(stationId) {
            // Empêcher les clics multiples
            if (isDeletingStation) {
                console.log('Suppression de borne déjà en cours');
                return;
            }
            
            if (!confirm('Êtes-vous sûr de vouloir supprimer cette borne ?')) {
                return;
            }
            
            isDeletingStation = true;
            
            try {
                const response = await fetch('http://localhost:8080/api/stations/' + stationId, {
                    method: 'DELETE'
                });
                
                if (response.ok) {
                    showSuccess('Borne supprimée avec succès');
                    setTimeout(() => {
                        loadLocationsAndStations();
                        isDeletingStation = false;
                    }, 1000);
                } else if (response.status === 404) {
                    showError('Cette borne a déjà été supprimée');
                    isDeletingStation = false;
                    setTimeout(() => loadLocationsAndStations(), 500);
                } else {
                    isDeletingStation = false;
                    throw new Error('Erreur lors de la suppression');
                }
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la suppression de la borne: ' + error.message);
                isDeletingStation = false;
            }
        }
        
        async function deleteLocation(locationId) {
            console.log('=== DELETE LOCATION ===');
            console.log('locationId reçu:', locationId);
            console.log('Type:', typeof locationId);
            console.log('======================');
            
            // Empêcher les clics multiples
            if (isDeletingLocation) {
                console.log('Suppression de lieu déjà en cours');
                return;
            }
            
            // Vérifier si le lieu contient des bornes
            const locationStations = stationsByLocation[locationId] || [];
            let confirmMessage = 'Êtes-vous sûr de vouloir supprimer ce lieu ?';
            
            if (locationStations.length > 0) {
                confirmMessage = 'ATTENTION : Ce lieu contient ' + locationStations.length + ' borne(s).\n\n' +
                    'La suppression du lieu supprimera également toutes ses bornes.\n\n' +
                    'Êtes-vous vraiment sûr de vouloir continuer ?';
            }
            
            if (!confirm(confirmMessage)) {
                return;
            }
            
            isDeletingLocation = true;
            
            try {
                const url = 'http://localhost:8080/api/locations/' + locationId;
                console.log('URL de suppression:', url);
                
                const response = await fetch(url, {
                    method: 'DELETE'
                });
                
                if (!response.ok) {
                    if (response.status === 404) {
                        showError('Ce lieu a déjà été supprimé');
                        isDeletingLocation = false;
                        setTimeout(() => loadLocationsAndStations(), 500);
                        return;
                    }
                    const errorText = await response.text();
                    isDeletingLocation = false;
                    throw new Error(errorText || 'Erreur lors de la suppression');
                }
                
                showSuccess('Lieu supprimé avec succès !');
                setTimeout(() => {
                    loadLocationsAndStations();
                    isDeletingLocation = false;
                }, 1000);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la suppression: ' + error.message);
                isDeletingLocation = false;
            }
        }
        
        function refreshLocations() {
            loadLocationsAndStations();
        }
        
        function uploadPhoto(stationId) {
            showError('Fonctionnalité en cours de développement. Vous pourrez bientôt téléverser des photos de vos bornes.');
        }
        
        function uploadVideo(stationId) {
            showError('Fonctionnalité en cours de développement. Vous pourrez bientôt téléverser des vidéos de vos bornes.');
        }
        
        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('locationsContainer').style.display = show ? 'none' : 'grid';
            document.getElementById('noLocationsMessage').style.display = 'none';
        }
        
        function showError(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="error">' + message + '</div>';
            setTimeout(() => {
                container.innerHTML = '';
            }, 5000);
        }
        
        function showSuccess(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="success">' + message + '</div>';
            setTimeout(() => {
                container.innerHTML = '';
            }, 3000);
        }
        
        // Charger les lieux et stations au chargement de la page
        document.addEventListener('DOMContentLoaded', function() {
            if (CURRENT_USER_ID) {
                loadLocationsAndStations();
            }
        });
    </script>
    <script src="js/auth.js"></script>
</body>
</html>
