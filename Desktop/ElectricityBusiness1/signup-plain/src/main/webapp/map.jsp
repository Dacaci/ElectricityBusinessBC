<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.eb.signup.user.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Carte des Stations - Electricity Business</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <style>
        body { 
            font-family: Arial, sans-serif; 
            margin: 0; 
            background-color: white; 
            height: 100vh;
            overflow: hidden;
        }
        
        .header { 
            background-color: white; 
            color: #333; 
            padding: 15px 20px; 
            border-bottom: 1px solid #ddd;
            position: relative;
            z-index: 1000;
        }
        
        .header h1 { 
            margin: 0; 
            display: inline-block; 
            font-size: 20px;
        }
        
        .header .user-info { 
            float: right; 
            margin-top: 2px; 
        }
        
        .header .user-info a { 
            color: #007bff; 
            text-decoration: none; 
            margin-left: 15px; 
            font-size: 14px;
        }
        
        .header .user-info a:hover { 
            text-decoration: underline; 
        }
        
        .map-container {
            height: calc(100vh - 70px);
            position: relative;
        }
        
        #map {
            height: 100%;
            width: 100%;
        }
        
        .controls {
            position: absolute;
            top: 10px;
            right: 10px;
            background: white;
            padding: 10px;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            z-index: 1000;
        }
        
        .controls button {
            display: block;
            width: 100%;
            margin-bottom: 5px;
            padding: 8px 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
        }
        
        .controls button:hover {
            background-color: #0056b3;
        }
        
        .station-popup {
            font-family: Arial, sans-serif;
        }
        
        .station-popup h3 {
            margin: 0 0 10px 0;
            color: #007bff;
            font-size: 16px;
        }
        
        .station-popup p {
            margin: 5px 0;
            font-size: 14px;
            color: #333;
        }
        
        .station-popup .price {
            font-weight: bold;
            color: #28a745;
            font-size: 16px;
        }
        
        .station-popup .btn {
            display: inline-block;
            padding: 8px 16px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 3px;
            margin-top: 10px;
            font-size: 12px;
        }
        
        .station-popup .btn:hover {
            background-color: #0056b3;
        }
        
        .loading {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            z-index: 1000;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Electricity Business - Carte des Stations</h1>
        <div class="user-info">
            <span>Bienvenue, <%= request.getAttribute("user") != null ? ((User)request.getAttribute("user")).getEmail() : "Utilisateur" %></span>
            <a href="dashboard">Dashboard</a>
            <a href="logout">Déconnexion</a>
        </div>
    </div>
    
    <div class="map-container">
        <div id="map"></div>
        
        <div class="controls">
            <button onclick="getUserLocation()">Ma Position</button>
            <button onclick="loadAllStations()">Actualiser</button>
            <button onclick="toggleNearbySearch()">Recherche Proximité</button>
        </div>
        
        <div id="loading" class="loading" style="display: none;">
            <p>Chargement des stations...</p>
        </div>
    </div>

    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script>
        // Configuration de la carte
        const map = L.map('map').setView([48.8566, 2.3522], 10); // Paris par défaut
        
        // Ajouter les tuiles OpenStreetMap
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(map);
        
        // Groupe de marqueurs pour les stations
        const stationsLayer = L.layerGroup().addTo(map);
        
        // Variables globales
        let userLocation = null;
        let nearbyMode = false;
        
        // Icône personnalisée pour les stations
        const stationIcon = L.divIcon({
            className: 'station-marker',
            html: '<div style="background-color: #007bff; width: 20px; height: 20px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.3);"></div>',
            iconSize: [20, 20],
            iconAnchor: [10, 10]
        });
        
        // Charger toutes les stations
        async function loadAllStations() {
            showLoading(true);
            stationsLayer.clearLayers();
            
            try {
                const response = await fetch('http://localhost:8080/api/stations/map');
                
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                
                const stations = await response.json();
                console.log('Stations chargées:', stations);
                
                if (stations.length === 0) {
                    alert('Aucune station disponible. Créez d\'abord une borne de recharge !');
                    showLoading(false);
                    return;
                }
                
                // L'API /stations/map retourne déjà latitude et longitude
                stations.forEach(station => {
                    if (station.latitude && station.longitude) {
                        const marker = L.marker([station.latitude, station.longitude], { icon: stationIcon })
                            .bindPopup(createStationPopup(station));
                        
                        stationsLayer.addLayer(marker);
                    } else {
                        console.warn('Station sans coordonnées:', station);
                    }
                });
                
                // Ajuster la vue pour montrer toutes les stations
                if (stationsLayer.getLayers().length > 0) {
                    const group = new L.featureGroup(stationsLayer.getLayers());
                    map.fitBounds(group.getBounds().pad(0.1));
                } else {
                    alert('Aucune station avec coordonnées GPS valides');
                }
                
                showLoading(false);
            } catch (error) {
                console.error('Erreur lors du chargement des stations:', error);
                showLoading(false);
                alert('Erreur lors du chargement des stations: ' + error.message);
            }
        }
        
        // Obtenir la position de l'utilisateur
        function getUserLocation() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    function(position) {
                        userLocation = [position.coords.latitude, position.coords.longitude];
                        
                        // Ajouter un marqueur pour la position de l'utilisateur
                        L.marker(userLocation, {
                            icon: L.divIcon({
                                className: 'user-marker',
                                html: '<div style="background-color: #28a745; width: 16px; height: 16px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.3);"></div>',
                                iconSize: [16, 16],
                                iconAnchor: [8, 8]
                            })
                        }).bindPopup('Votre position').addTo(map);
                        
                        // Centrer la carte sur la position de l'utilisateur
                        map.setView(userLocation, 13);
                        
                        // Charger les stations à proximité
                        loadNearbyStations(userLocation[0], userLocation[1]);
                    },
                    function(error) {
                        console.error('Erreur de géolocalisation:', error);
                        alert('Impossible d\'obtenir votre position. Veuillez autoriser la géolocalisation.');
                    }
                );
            } else {
                alert('La géolocalisation n\'est pas supportée par votre navigateur.');
            }
        }
        
        // Charger les stations à proximité
        async function loadNearbyStations(lat, lng, radius = 10) {
            showLoading(true);
            stationsLayer.clearLayers();
            
            try {
                const response = await fetch(`http://localhost:8080/api/stations/nearby?latitude=${lat}&longitude=${lng}&radiusKm=${radius}`);
                const stations = await response.json();
                
                stations.forEach(station => {
                    const marker = L.marker([station.latitude, station.longitude], { icon: stationIcon })
                        .bindPopup(createStationPopup(station));
                    
                    stationsLayer.addLayer(marker);
                });
                
                showLoading(false);
            } catch (error) {
                console.error('Erreur lors du chargement des stations proches:', error);
                showLoading(false);
                alert('Erreur lors du chargement des stations à proximité');
            }
        }
        
        // Créer le popup pour une station
        function createStationPopup(station) {
            const address = station.address || 'Adresse non disponible';
            const rate = station.hourlyRate ? `${station.hourlyRate}€/h` : 'N/A';
            const label = station.locationLabel || '';
            
            return `
                <div class="station-popup">
                    <h3>${station.name}</h3>
                    <p><strong>Lieu:</strong> ${label}</p>
                    <p><strong>Adresse:</strong> ${address}</p>
                    <p><strong>Tarif:</strong> <span class="price">${rate}</span></p>
                    <p><strong>Type de prise:</strong> ${station.plugType || 'TYPE2S'}</p>
                    <a href="add-reservation.jsp?stationId=${station.id}" class="btn">Réserver</a>
                </div>
            `;
        }
        
        // Basculer le mode recherche à proximité
        function toggleNearbySearch() {
            nearbyMode = !nearbyMode;
            
            if (nearbyMode && userLocation) {
                loadNearbyStations(userLocation[0], userLocation[1]);
            } else if (!nearbyMode) {
                loadAllStations();
            } else {
                getUserLocation();
            }
        }
        
        // Réserver une station
        function reserveStation(stationId) {
            window.location.href = `add-reservation.jsp?stationId=${stationId}`;
        }
        
        // Afficher/masquer le loading
        function showLoading(show) {
            document.getElementById('loading').style.display = show ? 'block' : 'none';
        }
        
        // Charger les stations au démarrage
        document.addEventListener('DOMContentLoaded', function() {
            loadAllStations();
        });
    </script>
</body>
</html>