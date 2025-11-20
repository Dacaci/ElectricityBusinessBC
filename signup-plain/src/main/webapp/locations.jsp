<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    response.setHeader("Content-Security-Policy", "default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; connect-src 'self' http://localhost:8080 https://electricity-business-backend-z373.onrender.com https://nominatim.openstreetmap.org; script-src 'self' 'unsafe-inline' 'unsafe-eval';");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Lieux - Electricity Business</title>
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
    <script src="js/config.js"></script>
    <script src="js/jwt-utils.js"></script>
    <script>
        // Variables globales
        let locations = [];
        let stations = [];
        let medias = [];
        let mediasByStation = {}; // Pour grouper les médias par borne
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
        
                                        } // Fermer le bloc else
        
        // ========== FONCTIONS GLOBALES (définies en dehors du bloc else) ==========
        
        async function loadLocationsAndStations() {
            try {
                showLoading(true);
                
                // Charger les lieux, stations et médias en parallèle
                const [locationsResponse, stationsResponse, mediasResponse] = await Promise.all([
                    fetch(API_BASE_URL + '/api/locations'),
                    fetch(API_BASE_URL + '/api/stations/owner/' + CURRENT_USER_ID), // Mes propres bornes
                    fetch(API_BASE_URL + '/api/medias')
                ]);
                
                if (!locationsResponse.ok || !stationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des données');
                }
                
                const locationsData = await locationsResponse.json();
                const stationsData = await stationsResponse.json();
                
                // Charger les médias (peut échouer sans bloquer)
                if (mediasResponse.ok) {
                    medias = await mediasResponse.json();
                } else {
                    medias = [];
                }
                
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
                
                // Grouper les médias par station
                mediasByStation = {};
                medias.forEach(media => {
                    if (media.stationId) {
                        if (!mediasByStation[media.stationId]) {
                            mediasByStation[media.stationId] = [];
                        }
                        mediasByStation[media.stationId].push(media);
                    }
                });
                
                displayLocationsWithStations(locations);
                showLoading(false);
                
            } catch (error) {
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
                        const stationMedias = mediasByStation[station.id] || [];
                        
                        let mediasHtml = '';
                        if (stationMedias.length > 0) {
                            mediasHtml = '<div class="media-gallery" style="margin: 12px 0;">' +
                                stationMedias.map(media => {
                                    if (media.type === 'IMAGE') {
                                        return '<div class="media-item">' +
                                            '<img src="' + media.url + '" alt="' + (media.name || 'Photo') + '" style="width: 100%; height: 120px; object-fit: cover; border-radius: 4px;">' +
                                            '<div style="display: flex; justify-content: space-between; align-items: center; margin-top: 4px;">' +
                                                '<span style="font-size: 12px; color: #64748b;">' + (media.name || 'Photo') + '</span>' +
                                                '<button onclick="deleteMedia(' + media.id + ')" class="btn-icon" title="Supprimer">×</button>' +
                                            '</div>' +
                                        '</div>';
                                    } else {
                                        return '<div class="media-item">' +
                                            '<div style="width: 100%; height: 120px; background: #f1f5f9; border-radius: 4px; display: flex; align-items: center; justify-content: center; font-size: 40px;">🎥</div>' +
                                            '<div style="display: flex; justify-content: space-between; align-items: center; margin-top: 4px;">' +
                                                '<a href="' + media.url + '" target="_blank" style="font-size: 12px; color: #3b82f6;">' + (media.name || 'Vidéo') + '</a>' +
                                                '<button onclick="deleteMedia(' + media.id + ')" class="btn-icon" title="Supprimer">×</button>' +
                                            '</div>' +
                                        '</div>';
                                    }
                                }).join('') +
                            '</div>';
                        }
                        
                        return '<div class="station-card" style="margin-bottom: 12px;">' +
                            '<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">' +
                                '<div style="font-weight: 600;">' + (station.name || 'Borne sans nom') + '</div>' +
                                '<span class="status-badge ' + statusBadge + '">' + statusText + '</span>' +
                            '</div>' +
                            mediasHtml +
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
            showMediaModal(stationId, 'IMAGE');
        }
        
        function uploadVideo(stationId) {
            showMediaModal(stationId, 'VIDEO');
        }
        
        async function deleteStation(stationId) {
            // Empêcher les clics multiples
            if (isDeletingStation) {
                                return;
            }
            
            if (!confirm('Êtes-vous sûr de vouloir supprimer cette borne ?')) {
                return;
            }
            
            isDeletingStation = true;
            
            try {
                const response = await fetch(API_BASE_URL + '/api/stations/' + stationId, {
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
                                showError('Erreur lors de la suppression de la borne: ' + error.message);
                isDeletingStation = false;
            }
        }
        
        async function deleteLocation(locationId) {
                                                            // Empêcher les clics multiples
            if (isDeletingLocation) {
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
                const url = API_BASE_URL + '/api/locations/' + locationId;
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
                                showError('Erreur lors de la suppression: ' + error.message);
                isDeletingLocation = false;
            }
        }
        
        function refreshLocations() {
            loadLocationsAndStations();
        }
        
        async function deleteMedia(mediaId) {
            if (!confirm('Êtes-vous sûr de vouloir supprimer ce média ?')) {
                return;
            }
            
            try {
                const response = await fetch(API_BASE_URL + '/api/medias/' + mediaId, {
                    method: 'DELETE'
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la suppression');
                }
                
                showSuccess('Média supprimé avec succès !');
                loadLocationsAndStations();
            } catch (error) {
                                showError('Erreur lors de la suppression du média');
            }
        }
        
        let currentMediaStation = null;
        let currentMediaType = null;
        
        function showMediaModal(stationId, mediaType) {
            currentMediaStation = stationId;
            currentMediaType = mediaType;
            
            const modal = document.getElementById('mediaModal');
            const modalTitle = document.getElementById('mediaModalTitle');
            modalTitle.textContent = mediaType === 'IMAGE' ? 'Ajouter une Photo' : 'Ajouter une Vidéo';
            
            document.getElementById('mediaUrl').value = '';
            document.getElementById('mediaName').value = '';
            document.getElementById('mediaDescription').value = '';
            
            modal.style.display = 'block';
        }
        
        function closeMediaModal() {
            document.getElementById('mediaModal').style.display = 'none';
            currentMediaStation = null;
            currentMediaType = null;
        }
        
        async function submitMedia() {
            const url = document.getElementById('mediaUrl').value.trim();
            const name = document.getElementById('mediaName').value.trim();
            const description = document.getElementById('mediaDescription').value.trim();
            
            if (!url || !name) {
                showError('Veuillez remplir les champs obligatoires');
                return;
            }
            
            const mediaData = {
                name: name,
                url: url,
                type: currentMediaType,
                description: description || null,
                stationId: currentMediaStation
            };
            
            try {
                const response = await fetch(API_BASE_URL + '/api/medias', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(mediaData)
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de l\'ajout du média');
                }
                
                showSuccess('Média ajouté avec succès !');
                closeMediaModal();
                loadLocationsAndStations();
            } catch (error) {
                                showError('Erreur lors de l\'ajout du média');
            }
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
    
    <!-- Modal pour ajouter un média -->
    <div id="mediaModal" class="modal" style="display: none;">
        <div class="modal-content">
            <span class="close" onclick="closeMediaModal()">&times;</span>
            <h2 id="mediaModalTitle">Ajouter un Média</h2>
            <form onsubmit="event.preventDefault(); submitMedia();">
                <div class="form-group">
                    <label for="mediaName">Nom *</label>
                    <input type="text" id="mediaName" class="form-control" required placeholder="Ex: Photo de la borne">
                </div>
                <div class="form-group">
                    <label for="mediaUrl">URL *</label>
                    <input type="url" id="mediaUrl" class="form-control" required placeholder="https://exemple.com/image.jpg">
                    <small style="color: #64748b;">Entrez l'URL de l'image ou vidéo hébergée en ligne</small>
                </div>
                <div class="form-group">
                    <label for="mediaDescription">Description</label>
                    <textarea id="mediaDescription" class="form-control" rows="3" placeholder="Description optionnelle"></textarea>
                </div>
                <div class="actions">
                    <button type="button" class="btn btn-secondary" onclick="closeMediaModal()">Annuler</button>
                    <button type="submit" class="btn btn-primary">Ajouter</button>
                </div>
            </form>
        </div>
    </div>
    
    <style>
        /* Styles pour la modal */
        .modal {
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }
        
        .modal-content {
            background-color: #fff;
            margin: 10% auto;
            padding: 30px;
            border-radius: 8px;
            width: 90%;
            max-width: 500px;
            position: relative;
        }
        
        .close {
            position: absolute;
            right: 20px;
            top: 15px;
            font-size: 28px;
            font-weight: bold;
            color: #999;
            cursor: pointer;
        }
        
        .close:hover {
            color: #333;
        }
        
        /* Styles pour la galerie de médias */
        .media-gallery {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
            gap: 12px;
        }
        
        .media-item {
            position: relative;
        }
        
        .btn-icon {
            background: #ef4444;
            color: white;
            border: none;
            border-radius: 50%;
            width: 24px;
            height: 24px;
            cursor: pointer;
            font-size: 18px;
            line-height: 1;
            padding: 0;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .btn-icon:hover {
            background: #dc2626;
        }
    </style>
</body>
</html>
