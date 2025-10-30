<%@ page contentType="text/html; charset=UTF-8" %>
<%
    response.setHeader("Content-Security-Policy", "default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; connect-src 'self' http://localhost:8080; script-src 'self' 'unsafe-inline' 'unsafe-eval';");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Tableau de bord - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251022v5">
    <style>
        /* Styles spécifiques au dashboard uniquement */
        .section { 
            background: #fff; 
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 25px; 
            margin-bottom: 25px;
        }
        .section-title { 
            color: #333; 
            font-size: 18px; 
            margin-bottom: 20px; 
            padding-bottom: 10px;
            border-bottom: 2px solid #333;
        }
        .filter-bar { 
            display: flex; 
            gap: 15px; 
            margin-bottom: 15px; 
            align-items: center;
            padding: 15px;
            background-color: #f5f5f5;
            border-radius: 4px;
            border: 1px solid #ddd;
        }
        .filter-bar label { font-size: 13px; color: #333; font-weight: 500; }
        .filter-bar input { 
            padding: 6px 10px; 
            border: 1px solid #ccc; 
            border-radius: 4px;
            font-size: 13px;
        }
        .locations-grid { 
            display: grid; 
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); 
            gap: 20px; 
        }
        .location-card { 
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 20px;
            background-color: #fff;
        }
        .location-card h4 { color: #333; margin-bottom: 15px; font-size: 16px; }
        .location-card .station { 
            background-color: #f5f5f5; 
            padding: 10px; 
            margin: 8px 0; 
            border-radius: 4px;
            border-left: 3px solid #333;
        }
        .location-card .station-name { font-weight: 600; color: #333; font-size: 13px; }
        .location-card .station-rate { color: #666; font-size: 12px; margin-top: 3px; }
        .location-actions { 
            display: flex; 
            flex-wrap: wrap; 
            gap: 5px; 
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #ddd;
        }
        .no-data { 
            text-align: center; 
            padding: 40px; 
            color: #999; 
            font-size: 14px;
        }
        .pagination { 
            display: flex; 
            justify-content: center; 
            align-items: center; 
            gap: 10px; 
            margin-top: 20px;
        }
        .pagination button { 
            padding: 6px 12px; 
            border: 1px solid #ccc; 
            background-color: #fff;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
        }
        .pagination button:hover { background-color: #f5f5f5; }
        .pagination button:disabled { opacity: 0.5; cursor: not-allowed; }
        .pagination .page-info { color: #666; font-size: 13px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Tableau de Bord - Electricity Business</h1>
        <div class="user-info">
            <span id="welcomeMessage">Bienvenue</span>
            <span class="status-badge status-active">Actif</span>
            <a href="#" onclick="logout(); return false;">Déconnexion</a>
        </div>
    </div>
    
    <nav class="navigation">
        <a href="dashboard.jsp" class="nav-link active">Tableau de bord</a>
        <a href="add-location.jsp" class="nav-link">Ajouter un lieu</a>
        <a href="locations.jsp" class="nav-link">Mes lieux</a>
        <a href="add-station.jsp" class="nav-link">Ajouter une borne</a>
        <a href="stations.jsp" class="nav-link">Mes bornes</a>
        <a href="add-reservation.jsp" class="nav-link">Réserver une borne</a>
        <a href="reservations.jsp" class="nav-link">Mes réservations</a>
        <a href="vehicles.jsp" class="nav-link">Mes véhicules</a>
        <a href="map.jsp" class="nav-link">Carte des bornes</a>
    </nav>
    
    <div class="container">
        <!-- Message container -->
        <div id="messageContainer"></div>
        
        <!-- Mes réservations en cours -->
        <div class="section">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <h2 class="section-title" style="margin-bottom: 0;">Mes réservations en cours</h2>
                <a href="add-reservation.jsp" class="btn btn-primary">Effectuer une réservation</a>
            </div>
            <div id="currentReservationsLoading" class="loading">Chargement...</div>
            <div id="currentReservationsContainer" style="display: none;">
                <table id="currentReservationsTable">
                    <thead>
                        <tr>
                            <th>Date et heure début</th>
                            <th>Date et heure fin</th>
                            <th>Borne, Lieu, Ville</th>
                            <th>Montant réglé</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="currentReservationsBody"></tbody>
                </table>
                <div id="currentReservationsEmpty" class="no-data" style="display: none;">
                    Aucune réservation en cours
                </div>
            </div>
            </div>
            
        <!-- Mes réservations passées -->
        <div class="section">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <h2 class="section-title" style="margin-bottom: 0;">Mes réservations passées</h2>
                <a href="#" onclick="exportToExcel(); return false;" class="btn btn-info">Exporter toutes les réservations au format Excel</a>
            </div>
            <div class="filter-bar">
                <label>Date début :</label>
                <input type="date" id="filterDateStart">
                <label>Date fin :</label>
                <input type="date" id="filterDateEnd">
                <button class="btn btn-primary" onclick="filterPastReservations()">Filtrer</button>
                <button class="btn btn-warning" onclick="resetPastFilter()">Réinitialiser</button>
            </div>
            <div id="pastReservationsLoading" class="loading">Chargement...</div>
            <div id="pastReservationsContainer" style="display: none;">
                <table id="pastReservationsTable">
                    <thead>
                        <tr>
                            <th>Date et heure début</th>
                            <th>Date et heure fin</th>
                            <th>Borne, Lieu, Ville</th>
                            <th>Montant réglé</th>
                            <th>Statut</th>
                        </tr>
                    </thead>
                    <tbody id="pastReservationsBody"></tbody>
                </table>
                <div id="pastReservationsEmpty" class="no-data" style="display: none;">
                    Aucune réservation passée
                </div>
                <div class="pagination" id="pastPagination"></div>
            </div>
            </div>
            
        <!-- Lieux de recharge -->
        <div class="section">
            <h2 class="section-title">Lieux de recharge</h2>
            <button class="btn btn-success" onclick="window.location.href='add-location.jsp'" style="margin-bottom: 15px;">
                Ajouter un lieu
            </button>
            <div id="locationsLoading" class="loading">Chargement...</div>
            <div id="locationsContainer" style="display: none;">
                <div class="locations-grid" id="locationsGrid"></div>
                <div id="locationsEmpty" class="no-data" style="display: none;">
                    Aucun lieu de recharge
                </div>
            </div>
            </div>
            
        <!-- Demandes de réservations à traiter -->
        <div class="section">
            <h2 class="section-title">Demandes de réservations à traiter</h2>
            <div id="pendingRequestsLoading" class="loading">Chargement...</div>
            <div id="pendingRequestsContainer" style="display: none;">
                <table id="pendingRequestsTable">
                    <thead>
                        <tr>
                            <th>Date et heure début</th>
                            <th>Date et heure fin</th>
                            <th>Utilisateur</th>
                            <th>Borne, Lieu</th>
                            <th>Montant réglé</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="pendingRequestsBody"></tbody>
                </table>
                <div id="pendingRequestsEmpty" class="no-data" style="display: none;">
                    Aucune demande en attente
                </div>
            </div>
        </div>
        
        <!-- Demandes de réservations traitées -->
        <div class="section">
            <h2 class="section-title">Demandes de réservations traitées</h2>
            <div id="treatedRequestsLoading" class="loading">Chargement...</div>
            <div id="treatedRequestsContainer" style="display: none;">
                <table id="treatedRequestsTable">
                    <thead>
                        <tr>
                            <th>Date et heure début</th>
                            <th>Date et heure fin</th>
                            <th>Utilisateur</th>
                            <th>Borne, Lieu</th>
                            <th>Montant réglé</th>
                            <th>Statut</th>
                        </tr>
                    </thead>
                    <tbody id="treatedRequestsBody"></tbody>
                </table>
                <div id="treatedRequestsEmpty" class="no-data" style="display: none;">
                    Aucune demande traitée
                </div>
                <div class="pagination" id="treatedPagination"></div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="js/jwt-utils.js?v=20251019"></script>
    <script>
                let CURRENT_USER_ID = null;
        let allReservations = [];
        let allStations = [];
        let allLocations = [];
        let allUsers = [];
        
        // Pagination
        let currentPastPage = 1;
        let currentTreatedPage = 1;
        const itemsPerPage = 12;
        
        // Vérifier l'authentification
        if (!requireAuth()) {
            // Redirection automatique
        } else {
            CURRENT_USER_ID = getCurrentUserId();
            const user = getAuthUser();
            if (user) {
                document.getElementById('welcomeMessage').textContent = 'Bienvenue, ' + user.firstName + ' ' + user.lastName;
            }
            
            // Charger toutes les données au démarrage
            loadAllData();
        }
        
        async function loadAllData() {
            try {
                const [reservations, stations, locations] = await Promise.all([
                    authenticatedFetch('http://localhost:8080/api/reservations').then(r => r.json()),
                    authenticatedFetch('http://localhost:8080/api/stations').then(r => r.json()),
                    authenticatedFetch('http://localhost:8080/api/locations').then(r => r.json())
                ]);
                
                allReservations = reservations.content || reservations;
                allStations = stations.content || stations;
                allLocations = locations.content || locations;
                
                loadCurrentReservations();
                loadPastReservations();
                loadLocations();
                loadPendingRequests();
                loadTreatedRequests();
                
            } catch (error) {
                                showError('Erreur lors du chargement des données');
            }
        }
        
        function loadCurrentReservations() {
            const container = document.getElementById('currentReservationsContainer');
            const loading = document.getElementById('currentReservationsLoading');
            const body = document.getElementById('currentReservationsBody');
            const empty = document.getElementById('currentReservationsEmpty');
            
            // Réservations en cours = PENDING, CONFIRMED ou ACTIVE
            const current = allReservations.filter(r => 
                r.userId === CURRENT_USER_ID && 
                ['PENDING', 'CONFIRMED', 'ACTIVE'].includes(r.status)
            );
            
            loading.style.display = 'none';
            container.style.display = 'block';
            
            if (current.length === 0) {
                empty.style.display = 'block';
                body.innerHTML = '';
                return;
            }
            
            empty.style.display = 'none';
            body.innerHTML = current.map(reservation => {
                const station = allStations.find(s => s.id === reservation.stationId);
                const location = allLocations.find(l => l.id === station?.locationId);
                
                return '<tr>' +
                    '<td>' + formatDateTime(reservation.startDateTime) + '</td>' +
                    '<td>' + formatDateTime(reservation.endDateTime) + '</td>' +
                    '<td>' + (station?.name || 'N/A') + '<br>' + (location?.name || 'N/A') + ', ' + (location?.city || 'N/A') + '</td>' +
                    '<td>' + (reservation.totalPrice ? reservation.totalPrice.toFixed(2) : '0.00') + ' €</td>' +
                    '<td><span class="status-badge status-' + reservation.status.toLowerCase() + '">' + getStatusText(reservation.status) + '</span></td>' +
                    '<td>' +
                        (reservation.status === 'PENDING' ? '<button class="btn btn-danger" onclick="cancelReservation(' + reservation.id + ')">Annuler</button>' : '') +
                    '</td>' +
                '</tr>';
            }).join('');
        }
        
        function loadPastReservations() {
            const container = document.getElementById('pastReservationsContainer');
            const loading = document.getElementById('pastReservationsLoading');
            const body = document.getElementById('pastReservationsBody');
            const empty = document.getElementById('pastReservationsEmpty');
            const pagination = document.getElementById('pastPagination');
            
            // Réservations passées = COMPLETED, CANCELLED
            const past = allReservations.filter(r => 
                r.userId === CURRENT_USER_ID && 
                ['COMPLETED', 'CANCELLED'].includes(r.status)
            );
            
            loading.style.display = 'none';
            container.style.display = 'block';
            
            if (past.length === 0) {
                empty.style.display = 'block';
                body.innerHTML = '';
                pagination.innerHTML = '';
                return;
            }
            
            empty.style.display = 'none';
            
            // Calculer la pagination
            const totalPages = Math.ceil(past.length / itemsPerPage);
            const startIndex = (currentPastPage - 1) * itemsPerPage;
            const endIndex = startIndex + itemsPerPage;
            const paginatedPast = past.slice(startIndex, endIndex);
            
            body.innerHTML = paginatedPast.map(reservation => {
                const station = allStations.find(s => s.id === reservation.stationId);
                const location = allLocations.find(l => l.id === station?.locationId);
                
                return '<tr>' +
                    '<td>' + formatDateTime(reservation.startDateTime) + '</td>' +
                    '<td>' + formatDateTime(reservation.endDateTime) + '</td>' +
                    '<td>' + (station?.name || 'N/A') + '<br>' + (location?.name || 'N/A') + ', ' + (location?.city || 'N/A') + '</td>' +
                    '<td>' + (reservation.totalPrice ? reservation.totalPrice.toFixed(2) : '0.00') + ' €</td>' +
                    '<td><span class="status-badge status-' + reservation.status.toLowerCase() + '">' + getStatusText(reservation.status) + '</span></td>' +
                '</tr>';
            }).join('');
            
            // Générer les contrôles de pagination
            pagination.innerHTML = generatePaginationControls(currentPastPage, totalPages, 'changePastPage');
        }
        
        function loadLocations() {
            const container = document.getElementById('locationsContainer');
            const loading = document.getElementById('locationsLoading');
            const grid = document.getElementById('locationsGrid');
            const empty = document.getElementById('locationsEmpty');
            
            const myLocations = allLocations.filter(l => l.ownerId === CURRENT_USER_ID);
            
            loading.style.display = 'none';
            container.style.display = 'block';
            
            if (myLocations.length === 0) {
                empty.style.display = 'block';
                grid.innerHTML = '';
                return;
            }
            
            empty.style.display = 'none';
            grid.innerHTML = myLocations.map(location => {
                const locationStations = allStations.filter(s => s.locationId === location.id);
                
                return '<div class="location-card">' +
                    '<h4>' + location.name + '</h4>' +
                    '<p style="color: #7f8c8d; font-size: 12px; margin-bottom: 10px;">' + location.address + ', ' + location.city + '</p>' +
                    '<div>' +
                        locationStations.map(station => 
                            '<div class="station">' +
                                '<div class="station-name">' + station.name + '</div>' +
                                '<div class="station-rate">' + (station.hourlyRate ? station.hourlyRate.toFixed(2) : '0.00') + ' €/h</div>' +
                            '</div>'
                        ).join('') +
                    '</div>' +
                    '<div class="location-actions">' +
                        '<button class="btn btn-primary" onclick="window.location.href=\'edit-location.jsp?id=' + location.id + '\'">Modifier</button>' +
                        '<button class="btn btn-info" onclick="window.location.href=\'add-station.jsp?locationId=' + location.id + '\'">Ajouter une borne</button>' +
                        '<button class="btn btn-danger" onclick="deleteLocation(' + location.id + ')">Supprimer</button>' +
                    '</div>' +
                '</div>';
            }).join('');
        }
        
        function loadPendingRequests() {
            const container = document.getElementById('pendingRequestsContainer');
            const loading = document.getElementById('pendingRequestsLoading');
            const body = document.getElementById('pendingRequestsBody');
            const empty = document.getElementById('pendingRequestsEmpty');
            
            // Demandes en attente = réservations PENDING sur MES bornes
            const myStationIds = allStations.filter(s => {
                const location = allLocations.find(l => l.id === s.locationId);
                return location && location.ownerId === CURRENT_USER_ID;
            }).map(s => s.id);
            
            const pending = allReservations.filter(r => 
                r.status === 'PENDING' && 
                myStationIds.includes(r.stationId) &&
                r.userId !== CURRENT_USER_ID
            );
            
            loading.style.display = 'none';
            container.style.display = 'block';
            
            if (pending.length === 0) {
                empty.style.display = 'block';
                body.innerHTML = '';
                return;
            }
            
            empty.style.display = 'none';
            body.innerHTML = pending.map(reservation => {
                const station = allStations.find(s => s.id === reservation.stationId);
                const location = allLocations.find(l => l.id === station?.locationId);
                
                return '<tr>' +
                    '<td>' + formatDateTime(reservation.startDateTime) + '</td>' +
                    '<td>' + formatDateTime(reservation.endDateTime) + '</td>' +
                    '<td>Utilisateur #' + reservation.userId + '</td>' +
                    '<td>' + (station?.name || 'N/A') + '<br>' + (location?.name || 'N/A') + '</td>' +
                    '<td>' + (reservation.totalPrice ? reservation.totalPrice.toFixed(2) : '0.00') + ' €</td>' +
                    '<td>' +
                        '<button class="btn btn-success" onclick="acceptReservation(' + reservation.id + ')">Accepter</button>' +
                        '<button class="btn btn-danger" onclick="refuseReservation(' + reservation.id + ')">Refuser</button>' +
                    '</td>' +
                '</tr>';
            }).join('');
        }
        
        function loadTreatedRequests() {
            const container = document.getElementById('treatedRequestsContainer');
            const loading = document.getElementById('treatedRequestsLoading');
            const body = document.getElementById('treatedRequestsBody');
            const empty = document.getElementById('treatedRequestsEmpty');
            const pagination = document.getElementById('treatedPagination');
            
            // Demandes traitées = réservations CONFIRMED, CANCELLED, COMPLETED sur MES bornes
            const myStationIds = allStations.filter(s => {
                const location = allLocations.find(l => l.id === s.locationId);
                return location && location.ownerId === CURRENT_USER_ID;
            }).map(s => s.id);
            
            const treated = allReservations.filter(r => 
                ['CONFIRMED', 'CANCELLED', 'COMPLETED'].includes(r.status) && 
                myStationIds.includes(r.stationId) &&
                r.userId !== CURRENT_USER_ID
            );
            
            loading.style.display = 'none';
            container.style.display = 'block';
            
            if (treated.length === 0) {
                empty.style.display = 'block';
                body.innerHTML = '';
                pagination.innerHTML = '';
                return;
            }
            
            empty.style.display = 'none';
            
            // Calculer la pagination
            const totalPages = Math.ceil(treated.length / itemsPerPage);
            const startIndex = (currentTreatedPage - 1) * itemsPerPage;
            const endIndex = startIndex + itemsPerPage;
            const paginatedTreated = treated.slice(startIndex, endIndex);
            
            body.innerHTML = paginatedTreated.map(reservation => {
                const station = allStations.find(s => s.id === reservation.stationId);
                const location = allLocations.find(l => l.id === station?.locationId);
                
                return '<tr>' +
                    '<td>' + formatDateTime(reservation.startDateTime) + '</td>' +
                    '<td>' + formatDateTime(reservation.endDateTime) + '</td>' +
                    '<td>Utilisateur #' + reservation.userId + '</td>' +
                    '<td>' + (station?.name || 'N/A') + '<br>' + (location?.name || 'N/A') + '</td>' +
                    '<td>' + (reservation.totalPrice ? reservation.totalPrice.toFixed(2) : '0.00') + ' €</td>' +
                    '<td><span class="status-badge status-' + reservation.status.toLowerCase() + '">' + getStatusText(reservation.status) + '</span></td>' +
                '</tr>';
            }).join('');
            
            // Générer les contrôles de pagination
            pagination.innerHTML = generatePaginationControls(currentTreatedPage, totalPages, 'changeTreatedPage');
        }
        
        async function acceptReservation(id) {
            try {
                const response = await authenticatedFetch('http://localhost:8080/api/reservations/' + id + '/confirm', {
                    method: 'PUT'
                });
                if (!response.ok) throw new Error('Erreur');
                showSuccess('Réservation acceptée');
                loadAllData();
            } catch (error) {
                showError('Erreur lors de l\'acceptation');
            }
        }
        
        async function refuseReservation(id) {
            if (!confirm('Voulez-vous refuser cette demande ?')) return;
            try {
                const response = await authenticatedFetch('http://localhost:8080/api/reservations/' + id + '/cancel', {
                    method: 'PUT'
                });
                if (!response.ok) throw new Error('Erreur');
                showSuccess('Réservation refusée');
                loadAllData();
            } catch (error) {
                showError('Erreur lors du refus');
            }
        }
        
        async function cancelReservation(id) {
            if (!confirm('Voulez-vous annuler cette réservation ?')) return;
            try {
                const response = await authenticatedFetch('http://localhost:8080/api/reservations/' + id + '/cancel', {
                    method: 'PUT'
                });
                if (!response.ok) throw new Error('Erreur');
                showSuccess('Réservation annulée');
                loadAllData();
            } catch (error) {
                showError('Erreur lors de l\'annulation');
            }
        }
        
        async function deleteLocation(id) {
            if (!confirm('Voulez-vous supprimer ce lieu ?')) return;
            try {
                const response = await authenticatedFetch('http://localhost:8080/api/locations/' + id, {
                    method: 'DELETE'
                });
                if (!response.ok) throw new Error('Erreur');
                showSuccess('Lieu supprimé');
                loadAllData();
            } catch (error) {
                showError('Erreur lors de la suppression');
            }
        }
        
        function filterPastReservations() {
            // TODO: Implémenter le filtre
            showSuccess('Filtre appliqué');
        }
        
        function resetPastFilter() {
            document.getElementById('filterDateStart').value = '';
            document.getElementById('filterDateEnd').value = '';
            currentPastPage = 1;
            loadPastReservations();
        }
        
        function changePastPage(page) {
            currentPastPage = page;
            loadPastReservations();
        }
        
        function changeTreatedPage(page) {
            currentTreatedPage = page;
            loadTreatedRequests();
        }
        
        function generatePaginationControls(currentPage, totalPages, changeFunction) {
            if (totalPages <= 1) return '';
            
            let html = '';
            
            // Bouton première page
            html += '<button ' + (currentPage === 1 ? 'disabled' : '') + ' onclick="' + changeFunction + '(1)">«</button>';
            
            // Bouton précédent
            html += '<button ' + (currentPage === 1 ? 'disabled' : '') + ' onclick="' + changeFunction + '(' + (currentPage - 1) + ')">‹</button>';
            
            // Affichage info page
            html += '<span class="page-info">Page ' + currentPage + ' sur ' + totalPages + '</span>';
            
            // Bouton suivant
            html += '<button ' + (currentPage === totalPages ? 'disabled' : '') + ' onclick="' + changeFunction + '(' + (currentPage + 1) + ')">›</button>';
            
            // Bouton dernière page
            html += '<button ' + (currentPage === totalPages ? 'disabled' : '') + ' onclick="' + changeFunction + '(' + totalPages + ')">»</button>';
            
            return html;
        }
        
        function exportToExcel() {
            // Récupérer toutes les réservations de l'utilisateur
            const myReservations = allReservations.filter(r => r.userId === CURRENT_USER_ID);
            
            if (myReservations.length === 0) {
                showError('Aucune réservation à exporter');
                return;
            }
            
            // Créer le contenu CSV
            let csv = 'Date et heure debut,Date et heure fin,Borne,Lieu,Ville,Montant regle,Statut\n';
            
            myReservations.forEach(reservation => {
                const station = allStations.find(s => s.id === reservation.stationId);
                const location = allLocations.find(l => l.id === station?.locationId);
                
                csv += formatDateTime(reservation.startDateTime) + ',' +
                       formatDateTime(reservation.endDateTime) + ',' +
                       (station?.name || 'N/A') + ',' +
                       (location?.name || 'N/A') + ',' +
                       (location?.city || 'N/A') + ',' +
                       (reservation.totalPrice ? reservation.totalPrice.toFixed(2) : '0.00') + ' EUR,' +
                       getStatusText(reservation.status) + '\n';
            });
            
            // Créer un blob et télécharger
            const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            
            link.setAttribute('href', url);
            link.setAttribute('download', 'mes_reservations_' + new Date().getTime() + '.csv');
            link.style.visibility = 'hidden';
            
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            
            showSuccess('Export réussi !');
        }
        
        function formatDateTime(dateTimeStr) {
            if (!dateTimeStr) return 'N/A';
            
            const date = new Date(dateTimeStr);
            
            // Vérifier si la date est valide
            if (isNaN(date.getTime())) return 'Date invalide';
            
            const day = String(date.getDate()).padStart(2, '0');
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const year = date.getFullYear();
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            return day + '/' + month + '/' + year + ' ' + hours + ':' + minutes;
        }
        
        function getStatusText(status) {
            const statusMap = {
                'PENDING': 'En attente',
                'CONFIRMED': 'Accepté',
                'ACTIVE': 'En cours',
                'COMPLETED': 'Terminé',
                'CANCELLED': 'Annulé'
            };
            return statusMap[status] || status;
        }
        
        function showError(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="message error">' + message + '</div>';
            setTimeout(() => container.innerHTML = '', 5000);
        }
        
        function showSuccess(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="message success">' + message + '</div>';
            setTimeout(() => container.innerHTML = '', 3000);
        }
        
        function logout() {
                        alert('Déconnexion en cours...');
            localStorage.clear();
            sessionStorage.clear();
            window.location.replace('/login.jsp?message=logout');
        }
    </script>
</body>
</html>
