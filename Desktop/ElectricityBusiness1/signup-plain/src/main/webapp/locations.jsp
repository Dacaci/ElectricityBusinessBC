<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Lieux - Electricity Business</title>
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
        
        .btn-danger {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
            box-shadow: 0 4px 15px rgba(220, 53, 69, 0.3);
        }
        
        .btn-warning {
            background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
            color: #212529;
            box-shadow: 0 4px 15px rgba(255, 193, 7, 0.3);
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
            border-color: #4CAF50;
        }
        
        .locations-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .location-card {
            background: white;
            border: 1px solid #e9ecef;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            transition: all 0.3s;
            position: relative;
        }
        
        .location-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        
        .location-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 15px;
        }
        
        .location-title {
            font-size: 1.3em;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 5px;
        }
        
        .location-address {
            color: #6c757d;
            font-size: 0.95em;
            line-height: 1.4;
        }
        
        .location-actions {
            display: flex;
            gap: 8px;
            margin-top: 15px;
        }
        
        .location-actions .btn {
            padding: 8px 16px;
            font-size: 12px;
        }
        
        .no-locations {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        
        .no-locations h3 {
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
            
            .locations-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📍 Gestion des Lieux</h1>
            <p>Gérez vos lieux de recharge électrique</p>
        </div>
        
        <div class="nav">
            <a href="dashboard.jsp">🏠 Tableau de bord</a>
            <a href="locations.jsp" class="active">📍 Lieux</a>
            <a href="stations.jsp">🔌 Bornes</a>
            <a href="reservations.jsp">📅 Réservations</a>
            <a href="map.jsp">🗺️ Carte</a>
            <a href="logout">🚪 Déconnexion</a>
        </div>
        
        <div class="content">
            <div class="actions">
                <div>
                    <a href="add-location.jsp" class="btn">➕ Ajouter un lieu</a>
                </div>
                <div class="search-box">
                    <input type="text" id="searchInput" placeholder="Rechercher un lieu..." onkeyup="filterLocations()">
                    <button class="btn btn-secondary" onclick="refreshLocations()">🔄 Actualiser</button>
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
                <a href="add-location.jsp" class="btn" style="margin-top: 15px;">➕ Ajouter un lieu</a>
            </div>
        </div>
    </div>

    <script>
        let locations = [];
        
        // Charger les lieux au chargement de la page
        document.addEventListener('DOMContentLoaded', function() {
            loadLocations();
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
                displayLocations(locations);
                showLoading(false);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors du chargement des lieux: ' + error.message);
                showLoading(false);
            }
        }
        
        function displayLocations(locationsToShow) {
            const container = document.getElementById('locationsContainer');
            const noLocations = document.getElementById('noLocationsMessage');
            
            if (locationsToShow.length === 0) {
                container.style.display = 'none';
                noLocations.style.display = 'block';
                return;
            }
            
            container.innerHTML = locationsToShow.map(location => {
                return '<div class="location-card">' +
                    '<div class="location-header">' +
                        '<div>' +
                            '<div class="location-title">' + location.label + '</div>' +
                            '<div class="location-address">' + location.address + '</div>' +
                        '</div>' +
                    '</div>' +
                    '<div class="location-actions">' +
                        '<a href="edit-location.jsp?id=' + location.id + '" class="btn btn-warning">✏️ Modifier</a>' +
                        '<button onclick="deleteLocation(' + location.id + ')" class="btn btn-danger">🗑️ Supprimer</button>' +
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
            displayLocations(filteredLocations);
        }
        
        async function deleteLocation(locationId) {
            if (!confirm('Êtes-vous sûr de vouloir supprimer ce lieu ?')) {
                return;
            }
            
            try {
                const response = await fetch(`http://localhost:8080/api/locations/${locationId}`, {
                    method: 'DELETE'
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la suppression');
                }
                
                showSuccess('Lieu supprimé avec succès');
                loadLocations(); // Recharger la liste
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la suppression: ' + error.message);
            }
        }
        
        function refreshLocations() {
            loadLocations();
        }
        
        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('locationsContainer').style.display = show ? 'none' : 'grid';
            document.getElementById('noLocationsMessage').style.display = 'none';
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





