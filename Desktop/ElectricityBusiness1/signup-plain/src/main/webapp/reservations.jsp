<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Réservations - Electricity Business</title>
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
            background-color: #9C27B0;
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
            background: linear-gradient(135deg, #9C27B0 0%, #7B1FA2 100%);
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
            box-shadow: 0 4px 15px rgba(156, 39, 176, 0.3);
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(156, 39, 176, 0.4);
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
        
        .btn-info {
            background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
            box-shadow: 0 4px 15px rgba(23, 162, 184, 0.3);
        }
        
        .filters {
            display: flex;
            gap: 15px;
            align-items: center;
            flex-wrap: wrap;
        }
        
        .filter-group {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }
        
        .filter-group label {
            font-size: 0.9em;
            font-weight: 600;
            color: #2c3e50;
        }
        
        .filter-group select,
        .filter-group input {
            padding: 8px 12px;
            border: 2px solid #e9ecef;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        .filter-group select:focus,
        .filter-group input:focus {
            outline: none;
            border-color: #9C27B0;
        }
        
        .reservations-table {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            margin-top: 20px;
        }
        
        .table-header {
            background: #f8f9fa;
            padding: 20px;
            border-bottom: 1px solid #e9ecef;
        }
        
        .table-header h3 {
            margin: 0;
            color: #2c3e50;
        }
        
        .table-container {
            overflow-x: auto;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }
        
        th {
            background: #f8f9fa;
            font-weight: 600;
            color: #2c3e50;
            font-size: 0.9em;
            text-transform: uppercase;
        }
        
        td {
            color: #495057;
        }
        
        .status-badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 0.8em;
            font-weight: 600;
            text-transform: uppercase;
        }
        
        .status-pending {
            background: #fff3cd;
            color: #856404;
        }
        
        .status-confirmed {
            background: #d4edda;
            color: #155724;
        }
        
        .status-cancelled {
            background: #f8d7da;
            color: #721c24;
        }
        
        .status-completed {
            background: #d1ecf1;
            color: #0c5460;
        }
        
        .reservation-actions {
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }
        
        .reservation-actions .btn {
            padding: 6px 12px;
            font-size: 12px;
        }
        
        .no-reservations {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        
        .no-reservations h3 {
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
        
        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            margin-top: 20px;
        }
        
        .pagination button {
            padding: 8px 12px;
            border: 1px solid #e9ecef;
            background: white;
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .pagination button:hover:not(:disabled) {
            background: #9C27B0;
            color: white;
            border-color: #9C27B0;
        }
        
        .pagination button:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        
        @media (max-width: 768px) {
            .actions {
                flex-direction: column;
                align-items: stretch;
            }
            
            .filters {
                flex-direction: column;
                align-items: stretch;
            }
            
            .table-container {
                font-size: 0.9em;
            }
            
            .reservation-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📅 Gestion des Réservations</h1>
            <p>Gérez vos réservations de bornes de recharge</p>
        </div>
        
        <div class="nav">
            <a href="dashboard.jsp">🏠 Tableau de bord</a>
            <a href="locations.jsp">📍 Lieux</a>
            <a href="stations.jsp">🔌 Bornes</a>
            <a href="reservations.jsp" class="active">📅 Réservations</a>
            <a href="map.jsp">🗺️ Carte</a>
            <a href="logout">🚪 Déconnexion</a>
        </div>
        
        <div class="content">
            <div class="actions">
                <div>
                    <a href="add-reservation.jsp" class="btn">➕ Nouvelle réservation</a>
                </div>
                <div class="filters">
                    <div class="filter-group">
                        <label>Statut</label>
                        <select id="statusFilter" onchange="filterReservations()">
                            <option value="">Tous les statuts</option>
                            <option value="PENDING">En attente</option>
                            <option value="CONFIRMED">Confirmée</option>
                            <option value="CANCELLED">Annulée</option>
                            <option value="COMPLETED">Terminée</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label>Période</label>
                        <select id="periodFilter" onchange="filterReservations()">
                            <option value="all">Toutes</option>
                            <option value="upcoming">À venir</option>
                            <option value="current">En cours</option>
                            <option value="past">Passées</option>
                        </select>
                    </div>
                    <button class="btn btn-secondary" onclick="refreshReservations()">🔄 Actualiser</button>
                </div>
            </div>
            
            <div id="messageContainer"></div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement des réservations...</p>
            </div>
            
            <div id="reservationsContainer" class="reservations-table" style="display: none;">
                <div class="table-header">
                    <h3>Liste des réservations</h3>
                </div>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Utilisateur</th>
                                <th>Borne</th>
                                <th>Lieu</th>
                                <th>Début</th>
                                <th>Fin</th>
                                <th>Statut</th>
                                <th>Montant</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="reservationsTableBody">
                            <!-- Les réservations seront chargées ici via JavaScript -->
                        </tbody>
                    </table>
                </div>
            </div>
            
            <div id="noReservationsMessage" class="no-reservations" style="display: none;">
                <h3>Aucune réservation trouvée</h3>
                <p>Commencez par créer votre première réservation.</p>
                <a href="add-reservation.jsp" class="btn" style="margin-top: 15px;">➕ Nouvelle réservation</a>
            </div>
        </div>
    </div>

    <script>
        let reservations = [];
        let stations = [];
        let locations = [];
        
        // Charger les données au chargement de la page
        document.addEventListener('DOMContentLoaded', function() {
            loadData();
        });
        
        async function loadData() {
            try {
                showLoading(true);
                
                // Charger les réservations, stations et lieux en parallèle
                const [reservationsResponse, stationsResponse, locationsResponse] = await Promise.all([
                    fetch('http://localhost:8080/api/reservations'),
                    fetch('http://localhost:8080/api/stations'),
                    fetch('http://localhost:8080/api/locations')
                ]);
                
                if (!reservationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des réservations');
                }
                
                if (!stationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des bornes');
                }
                
                if (!locationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des lieux');
                }
                
                const reservationsData = await reservationsResponse.json();
                const stationsData = await stationsResponse.json();
                const locationsData = await locationsResponse.json();
                
                // L'API retourne une structure paginée {content: [...], ...}
                reservations = reservationsData.content || reservationsData;
                stations = stationsData.content || stationsData;
                locations = locationsData.content || locationsData;
                
                displayReservations(reservations);
                showLoading(false);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors du chargement: ' + error.message);
                showLoading(false);
            }
        }
        
        function displayReservations(reservationsToShow) {
            const container = document.getElementById('reservationsContainer');
            const noReservations = document.getElementById('noReservationsMessage');
            const tableBody = document.getElementById('reservationsTableBody');
            
            if (reservationsToShow.length === 0) {
                container.style.display = 'none';
                noReservations.style.display = 'block';
                return;
            }
            
            tableBody.innerHTML = reservationsToShow.map(reservation => {
                const station = stations.find(s => s.id === reservation.stationId);
                const location = locations.find(l => l.id === station?.locationId);
                
                const userFirstName = reservation.user?.firstName || 'N/A';
                const userLastName = reservation.user?.lastName || 'N/A';
                const stationName = station?.name || 'Borne inconnue';
                const locationLabel = location?.label || 'Lieu inconnu';
                
                return `
                    <tr>
                        <td>#\${reservation.id}</td>
                        <td>\${userFirstName} \${userLastName}</td>
                        <td>\${stationName}</td>
                        <td>\${locationLabel}</td>
                        <td>\${formatDateTime(reservation.startTime)}</td>
                        <td>\${formatDateTime(reservation.endTime)}</td>
                        <td><span class="status-badge status-\${reservation.status.toLowerCase()}">\${getStatusLabel(reservation.status)}</span></td>
                        <td>\${reservation.totalAmount} €</td>
                        <td>
                            <div class="reservation-actions">
                                \${getActionButtons(reservation)}
                            </div>
                        </td>
                    </tr>
                `;
            }).join('');
            
            container.style.display = 'block';
            noReservations.style.display = 'none';
        }
        
        function getStatusLabel(status) {
            const labels = {
                'PENDING': 'En attente',
                'CONFIRMED': 'Confirmée',
                'CANCELLED': 'Annulée',
                'COMPLETED': 'Terminée'
            };
            return labels[status] || status;
        }
        
        function getActionButtons(reservation) {
            let buttons = '';
            
            if (reservation.status === 'PENDING') {
                buttons += `<button onclick="confirmReservation(${reservation.id})" class="btn btn-success">✅ Confirmer</button>`;
                buttons += `<button onclick="cancelReservation(${reservation.id})" class="btn btn-danger">❌ Refuser</button>`;
            } else if (reservation.status === 'CONFIRMED') {
                buttons += `<button onclick="completeReservation(${reservation.id})" class="btn btn-info">✅ Terminer</button>`;
                buttons += `<button onclick="cancelReservation(${reservation.id})" class="btn btn-warning">❌ Annuler</button>`;
            }
            
            buttons += `<a href="http://localhost:8080/api/reservations/${reservation.id}/receipt.pdf" class="btn btn-secondary" target="_blank">📄 PDF</a>`;
            buttons += `<button onclick="deleteReservation(${reservation.id})" class="btn btn-danger">🗑️ Supprimer</button>`;
            
            return buttons;
        }
        
        function formatDateTime(dateTimeString) {
            const date = new Date(dateTimeString);
            return date.toLocaleString('fr-FR', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        }
        
        function filterReservations() {
            const statusFilter = document.getElementById('statusFilter').value;
            const periodFilter = document.getElementById('periodFilter').value;
            const now = new Date();
            
            let filteredReservations = reservations.filter(reservation => {
                // Filtre par statut
                if (statusFilter && reservation.status !== statusFilter) {
                    return false;
                }
                
                // Filtre par période
                const startTime = new Date(reservation.startTime);
                const endTime = new Date(reservation.endTime);
                
                switch (periodFilter) {
                    case 'upcoming':
                        return startTime > now;
                    case 'current':
                        return startTime <= now && endTime >= now;
                    case 'past':
                        return endTime < now;
                    default:
                        return true;
                }
            });
            
            displayReservations(filteredReservations);
        }
        
        async function confirmReservation(reservationId) {
            try {
                const response = await fetch(`http://localhost:8080/api/reservations/${reservationId}/confirm`, {
                    method: 'PUT'
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la confirmation');
                }
                
                showSuccess('Réservation confirmée avec succès');
                loadData();
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la confirmation: ' + error.message);
            }
        }
        
        async function cancelReservation(reservationId) {
            if (!confirm('Êtes-vous sûr de vouloir annuler cette réservation ?')) {
                return;
            }
            
            try {
                const response = await fetch(`http://localhost:8080/api/reservations/${reservationId}/cancel`, {
                    method: 'PUT'
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de l\'annulation');
                }
                
                showSuccess('Réservation annulée avec succès');
                loadData();
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de l\'annulation: ' + error.message);
            }
        }
        
        async function completeReservation(reservationId) {
            try {
                const response = await fetch(`http://localhost:8080/api/reservations/${reservationId}/complete`, {
                    method: 'PUT'
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la finalisation');
                }
                
                showSuccess('Réservation terminée avec succès');
                loadData();
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la finalisation: ' + error.message);
            }
        }
        
        async function deleteReservation(reservationId) {
            if (!confirm('Êtes-vous sûr de vouloir supprimer cette réservation ?')) {
                return;
            }
            
            try {
                const response = await fetch(`http://localhost:8080/api/reservations/${reservationId}`, {
                    method: 'DELETE'
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la suppression');
                }
                
                showSuccess('Réservation supprimée avec succès');
                loadData();
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la suppression: ' + error.message);
            }
        }
        
        function refreshReservations() {
            loadData();
        }
        
        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('reservationsContainer').style.display = show ? 'none' : 'block';
            document.getElementById('noReservationsMessage').style.display = 'none';
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





