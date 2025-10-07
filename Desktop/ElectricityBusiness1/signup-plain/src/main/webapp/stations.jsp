<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Bornes - Electricity Business</title>
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
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .header {
            background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
            color: white;
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
            background-color: #2196F3;
            color: white;
        }
        
        .content {
            padding: 30px;
        }
        
        .actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            flex-wrap: wrap;
            gap: 15px;
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
        
        .btn-danger {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
            box-shadow: 0 4px 15px rgba(220, 53, 69, 0.3);
        }
        
        .btn-warning {
            background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
            color: #212529;
            box-shadow: 0 4px 15px rgba(255, 193, 7, 0.3);
        }
        
        .btn-success {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            box-shadow: 0 4px 15px rgba(40, 167, 69, 0.3);
        }
        
        .search-box {
            display: flex;
            gap: 10px;
            align-items: center;
        }
        
        .search-box input {
            padding: 10px 15px;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            font-size: 14px;
            width: 250px;
            transition: border-color 0.3s;
        }
        
        .search-box input:focus {
            outline: none;
            border-color: #2196F3;
        }
        
        .stations-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .station-card {
            background: white;
            border: 1px solid #e9ecef;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            transition: all 0.3s;
            position: relative;
        }
        
        .station-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        
        .station-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 15px;
        }
        
        .station-title {
            font-size: 1.3em;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 5px;
        }
        
        .station-location {
            color: #6c757d;
            font-size: 0.95em;
            line-height: 1.4;
            margin-bottom: 10px;
        }
        
        .station-details {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            margin-bottom: 15px;
        }
        
        .detail-item {
            display: flex;
            flex-direction: column;
        }
        
        .detail-label {
            font-size: 0.8em;
            color: #6c757d;
            text-transform: uppercase;
            font-weight: 600;
            margin-bottom: 2px;
        }
        
        .detail-value {
            font-size: 0.95em;
            color: #2c3e50;
            font-weight: 500;
        }
        
        .status-badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 0.8em;
            font-weight: 600;
            text-transform: uppercase;
        }
        
        .status-active {
            background: #d4edda;
            color: #155724;
        }
        
        .status-inactive {
            background: #f8d7da;
            color: #721c24;
        }
        
        .station-actions {
            display: flex;
            gap: 8px;
            margin-top: 15px;
            flex-wrap: wrap;
        }
        
        .station-actions .btn {
            padding: 8px 16px;
            font-size: 12px;
        }
        
        .no-stations {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        
        .no-stations h3 {
            font-size: 1.5em;
            margin-bottom: 15px;
        }
        
        .loading {
            text-align: center;
            padding: 40px;
            color: #6c757d;
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
        
        @media (max-width: 768px) {
            .actions {
                flex-direction: column;
                align-items: stretch;
            }
            
            .search-box {
                width: 100%;
            }
            
            .search-box input {
                width: 100%;
            }
            
            .stations-grid {
                grid-template-columns: 1fr;
            }
            
            .station-details {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🔌 Gestion des Bornes</h1>
            <p>Gérez vos bornes de recharge électrique</p>
        </div>
        
        <div class="nav">
            <a href="dashboard.jsp">🏠 Tableau de bord</a>
            <a href="locations.jsp">📍 Lieux</a>
            <a href="stations.jsp" class="active">🔌 Bornes</a>
            <a href="reservations.jsp">📅 Réservations</a>
            <a href="map.jsp">🗺️ Carte</a>
            <a href="logout">🚪 Déconnexion</a>
        </div>
        
        <div class="content">
            <div class="actions">
                <div>
                    <a href="add-station.jsp" class="btn">➕ Ajouter une borne</a>
                </div>
                <div class="search-box">
                    <input type="text" id="searchInput" placeholder="Rechercher une borne..." onkeyup="filterStations()">
                    <button class="btn btn-secondary" onclick="refreshStations()">🔄 Actualiser</button>
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
                <a href="add-station.jsp" class="btn" style="margin-top: 15px;">➕ Ajouter une borne</a>
            </div>
        </div>
    </div>

    <script>
        let stations = [];
        let locations = [];
        
        // Charger les données au chargement de la page
        document.addEventListener('DOMContentLoaded', function() {
            loadData();
        });
        
        async function loadData() {
            try {
                showLoading(true);
                
                // Charger les bornes et les lieux en parallèle
                const [stationsResponse, locationsResponse] = await Promise.all([
                    fetch('http://localhost:8080/api/stations'),
                    fetch('http://localhost:8080/api/locations')
                ]);
                
                if (!stationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des bornes');
                }
                
                if (!locationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des lieux');
                }
                
                stations = await stationsResponse.json();
                locations = await locationsResponse.json();
                
                displayStations(stations);
                showLoading(false);
                
            } catch (error) {
                console.error('Erreur:', error);
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
                const locationName = location ? location.label : 'Lieu inconnu';
                const locationAddress = location ? location.address : '';
                
                return `
                    <div class="station-card">
                        <div class="station-header">
                            <div>
                                <div class="station-title">${station.name}</div>
                                <div class="station-location">📍 ${locationName}</div>
                                <div class="station-location" style="font-size: 0.85em; color: #6c757d;">${locationAddress}</div>
                            </div>
                            <span class="status-badge ${station.isActive ? 'status-active' : 'status-inactive'}">
                                ${station.isActive ? 'Active' : 'Inactive'}
                            </span>
                        </div>
                        
                        <div class="station-details">
                            <div class="detail-item">
                                <div class="detail-label">Type de prise</div>
                                <div class="detail-value">${station.plugType}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">Tarif horaire</div>
                                <div class="detail-value">${station.hourlyRate} €/h</div>
                            </div>
                        </div>
                        
                        <div class="station-actions">
                            <a href="edit-station.jsp?id=${station.id}" class="btn btn-warning">✏️ Modifier</a>
                            <button onclick="toggleStationStatus(${station.id}, ${station.isActive})" 
                                    class="btn ${station.isActive ? 'btn-secondary' : 'btn-success'}">
                                ${station.isActive ? '⏸️ Désactiver' : '▶️ Activer'}
                            </button>
                            <button onclick="deleteStation(${station.id})" class="btn btn-danger">🗑️ Supprimer</button>
                        </div>
                    </div>
                `;
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
                const response = await fetch(`http://localhost:8080/api/stations/${stationId}`, {
                    method: 'PUT',
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
                
                showSuccess(`Borne ${!currentStatus ? 'activée' : 'désactivée'} avec succès`);
                loadData(); // Recharger la liste
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la modification: ' + error.message);
            }
        }
        
        async function deleteStation(stationId) {
            if (!confirm('Êtes-vous sûr de vouloir supprimer cette borne ?')) {
                return;
            }
            
            try {
                const response = await fetch(`http://localhost:8080/api/stations/${stationId}`, {
                    method: 'DELETE'
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la suppression');
                }
                
                showSuccess('Borne supprimée avec succès');
                loadData(); // Recharger la liste
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la suppression: ' + error.message);
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
            container.innerHTML = `<div class="error">${message}</div>`;
            setTimeout(() => {
                container.innerHTML = '';
            }, 5000);
        }
        
        function showSuccess(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = `<div class="success">${message}</div>`;
            setTimeout(() => {
                container.innerHTML = '';
            }, 3000);
        }
    </script>
</body>
</html>



