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
    <title>Mes Réservations - Electricity Business</title>
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
        <a href="reservations.jsp" class="nav-link active">Mes réservations</a>
        <a href="map.jsp" class="nav-link">Carte</a>
    </nav>
    
    <div class="container">
        
        <div class="content">
            <div class="actions">
                <div>
                    <a href="add-reservation.jsp" class="btn">Nouvelle réservation</a>
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
                <a href="add-reservation.jsp" class="btn" style="margin-top: 15px;">Nouvelle réservation</a>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="js/jwt-utils.js?v=20251019171700"></script>
    <script>
        console.log('=== RESERVATIONS.JSP VERSION 2025-10-19 17:17:00 CHARGÉE ===');
        
        // Variables globales
        let reservations = [];
        let stations = [];
        let locations = [];
        let CURRENT_USER_ID = null;
        
        // Vérifier l'authentification
        if (!requireAuth()) {
            // L'utilisateur sera redirigé automatiquement
        } else {
        
        // Récupérer l'ID de l'utilisateur depuis le token JWT
        CURRENT_USER_ID = getCurrentUserId();
        
        // Charger les données au chargement de la page
        document.addEventListener('DOMContentLoaded', function() {
            loadData();
        });
        
        async function loadData() {
            try {
                showLoading(true);
                
                // Charger les réservations, stations et lieux en parallèle
                const [reservationsResponse, stationsResponse, locationsResponse] = await Promise.all([
                    authenticatedFetch('http://localhost:8080/api/reservations'),
                    authenticatedFetch('http://localhost:8080/api/stations'),
                    authenticatedFetch('http://localhost:8080/api/locations')
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
            
            // Vérifier si l'utilisateur connecté est le client (celui qui a fait la réservation)
            const isClient = reservation.userId === CURRENT_USER_ID;
            
            if (isClient) {
                // EN TANT QUE CLIENT : Seulement "Annuler" pour ses propres réservations
                if (reservation.status === 'PENDING' || reservation.status === 'CONFIRMED') {
                    buttons += '<button onclick="cancelReservation(' + reservation.id + ')" class="btn btn-warning">Annuler ma réservation</button>';
                }
            } else {
                // EN TANT QUE PROPRIÉTAIRE : Peut confirmer/refuser les réservations sur ses stations
                if (reservation.status === 'PENDING') {
                    buttons += '<button onclick="confirmReservation(' + reservation.id + ')" class="btn btn-success">Accepter</button>';
                    buttons += '<button onclick="cancelReservation(' + reservation.id + ')" class="btn btn-danger">Refuser</button>';
                } else if (reservation.status === 'CONFIRMED') {
                    buttons += '<button onclick="completeReservation(' + reservation.id + ')" class="btn btn-info">Marquer comme terminée</button>';
                    buttons += '<button onclick="cancelReservation(' + reservation.id + ')" class="btn btn-warning">Annuler</button>';
                }
            }
            
            // PDF et suppression pour tous
            buttons += '<a href="http://localhost:8080/api/reservations/' + reservation.id + '/receipt.pdf" class="btn btn-secondary" target="_blank">PDF</a>';
            buttons += '<button onclick="deleteReservation(' + reservation.id + ')" class="btn btn-danger">Supprimer</button>';
            
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
            if (!reservationId) {
                alert('Erreur: ID de réservation manquant');
                return;
            }
            
            try {
                const url = 'http://localhost:8080/api/reservations/' + reservationId + '/confirm';
                
                const response = await authenticatedFetch(url, {
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
            if (!reservationId) {
                alert('Erreur: ID de réservation manquant');
                return;
            }
            
            if (!confirm('Êtes-vous sûr de vouloir annuler cette réservation ?')) {
                return;
            }
            
            try {
                const url = 'http://localhost:8080/api/reservations/' + reservationId + '/cancel';
                
                const response = await authenticatedFetch(url, {
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
            if (!reservationId) {
                alert('Erreur: ID de réservation manquant');
                return;
            }
            
            try {
                const url = 'http://localhost:8080/api/reservations/' + reservationId + '/complete';
                
                const response = await authenticatedFetch(url, {
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
            if (!reservationId) {
                alert('Erreur: ID de réservation manquant');
                return;
            }
            
            if (!confirm('Êtes-vous sûr de vouloir supprimer cette réservation ?')) {
                return;
            }
            
            try {
                const url = 'http://localhost:8080/api/reservations/' + reservationId;
                
                const response = await authenticatedFetch(url, {
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
        
        // Rendre les fonctions accessibles globalement pour les onclick
        window.confirmReservation = confirmReservation;
        window.cancelReservation = cancelReservation;
        window.deleteReservation = deleteReservation;
        window.completeReservation = completeReservation;
        window.refreshReservations = refreshReservations;
        window.filterReservations = filterReservations;
        
        } // Fin du bloc else
    </script>
    <script src="js/auth.js"></script>
</body>
</html>





