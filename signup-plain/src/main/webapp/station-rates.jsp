<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="includes/backend-config.jsp" %>
<%
    String backendUrlForCsp = (String) request.getAttribute("BACKEND_URL");
    if (backendUrlForCsp == null || backendUrlForCsp.isEmpty()) {
        backendUrlForCsp = "http://localhost:8080";
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tarifs de la Borne - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251022v5">
    <meta http-equiv="Content-Security-Policy" content="default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; connect-src 'self' http://localhost:8080 <%= backendUrlForCsp %>; script-src 'self' 'unsafe-inline' 'unsafe-eval';">
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
            <h2>Tarifs de la borne</h2>
            
            <div id="messageContainer"></div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement...</p>
            </div>
            
            <div id="formContainer" class="card" style="display: none;">
                <div class="info" style="background: #f0f9ff; padding: 16px; border-radius: 8px; margin-bottom: 24px; border-left: 3px solid #0891b2;">
                    <p style="color: #0c4a6e; margin: 0;"><strong>Borne :</strong> <span id="stationName"></span></p>
                    <p style="color: #0c4a6e; margin: 8px 0 0 0;"><strong>Lieu :</strong> <span id="locationName"></span></p>
                </div>
                
                <form id="ratesForm">
                    <div class="form-group">
                        <label for="hourlyRate">Tarif horaire (€/h) <span style="color: #ef4444;">*</span></label>
                        <input type="number" id="hourlyRate" name="hourlyRate" class="form-control" step="0.01" min="0" required>
                        <small style="color: #64748b;">Le tarif actuel est appliqué à toutes les nouvelles réservations</small>
                    </div>
                    
                    <div class="actions" style="margin-top: 24px;">
                        <a href="locations.jsp" class="btn btn-secondary">Retour</a>
                        <button type="submit" class="btn btn-primary">Enregistrer le tarif</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="js/config.js"></script>
    <script src="js/jwt-utils.js"></script>
    <script>
        let stationId = null;
        
        // Vérifier l'authentification
        if (!requireAuth()) {
            // L'utilisateur sera redirigé automatiquement
        } else {
        
        // Récupérer l'ID de la borne depuis l'URL
        const urlParams = new URLSearchParams(window.location.search);
        stationId = urlParams.get('id');
        
        if (!stationId) {
            showError('ID de la borne manquant');
            setTimeout(function() {
                window.location.href = 'locations.jsp';
            }, 2000);
        } else {
            loadStation();
        }
        
        async function loadStation() {
            try {
                showLoading(true);
                
                const response = await authenticatedFetch(API_BASE_URL + '/api/stations/' + stationId);
                
                if (!response.ok) {
                    throw new Error('Borne non trouvée');
                }
                
                const station = await response.json();
                populateForm(station);
                showLoading(false);
                
            } catch (error) {
                                showError('Erreur lors du chargement: ' + error.message);
                showLoading(false);
            }
        }
        
        function populateForm(station) {
            document.getElementById('stationName').textContent = station.name || 'N/A';
            document.getElementById('locationName').textContent = station.locationLabel || 'N/A';
            document.getElementById('hourlyRate').value = station.hourlyRate || '';
            
            document.getElementById('formContainer').style.display = 'block';
        }
        
        document.getElementById('ratesForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const hourlyRate = parseFloat(document.getElementById('hourlyRate').value);
            
            if (isNaN(hourlyRate) || hourlyRate < 0) {
                showError('Veuillez entrer un tarif valide');
                return;
            }
            
            try {
                showLoading(true);
                
                // Utiliser PATCH pour modifier uniquement le tarif
                const response = await authenticatedFetch(API_BASE_URL + '/api/stations/' + stationId, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        hourlyRate: hourlyRate
                    })
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la modification du tarif');
                }
                
                showSuccess('Tarif modifié avec succès !');
                setTimeout(function() {
                    window.location.href = 'locations.jsp';
                }, 1500);
                
            } catch (error) {
                                showError('Erreur lors de la modification: ' + error.message);
                showLoading(false);
            }
        });
        
        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('formContainer').style.display = show ? 'none' : 'block';
        }
        
        function showError(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="error">' + message + '</div>';
        }
        
        function showSuccess(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="success">' + message + '</div>';
        }
        
        } // Fermer le bloc else
    </script>
    <script src="js/auth.js"></script>
</body>
</html>

