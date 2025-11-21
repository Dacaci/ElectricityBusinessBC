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
    <title>Modifier une Borne - Electricity Business</title>
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
        <a href="locations.jsp" class="nav-link">Mes lieux</a>
        <a href="add-station.jsp" class="nav-link">Ajouter une borne</a>
        <a href="stations.jsp" class="nav-link active">Mes bornes</a>
        <a href="add-reservation.jsp" class="nav-link">Réserver</a>
        <a href="reservations.jsp" class="nav-link">Mes réservations</a>
        <a href="map.jsp" class="nav-link">Carte</a>
    </nav>
    
    <div class="container">
        <div class="content">
            <h2>Modifier une borne</h2>
            
            <div id="messageContainer"></div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement des informations...</p>
            </div>
            
            <div id="formContainer" class="card" style="display: none;">
                <form id="stationForm">
                    <div class="form-group">
                        <label for="name">Nom de la borne <span style="color: #ef4444;">*</span></label>
                        <input type="text" id="name" name="name" class="form-control" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="locationId">Lieu <span style="color: #ef4444;">*</span></label>
                        <select id="locationId" name="locationId" class="form-control" required>
                            <option value="">Sélectionnez un lieu...</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="hourlyRate">Tarif horaire (€/h) <span style="color: #ef4444;">*</span></label>
                        <input type="number" id="hourlyRate" name="hourlyRate" class="form-control" step="0.01" min="0" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="plugType">Type de prise</label>
                        <input type="text" id="plugType" name="plugType" class="form-control" value="TYPE2S" readonly style="background-color: #f8f9fa; color: #6c757d;">
                    </div>
                    
                    <div class="form-group">
                        <label>
                            <input type="checkbox" id="isActive" name="isActive"> 
                            Borne active (disponible pour les réservations)
                        </label>
                    </div>
                    
                    <div class="actions">
                        <a href="stations.jsp" class="btn btn-secondary">Annuler</a>
                        <button type="submit" class="btn btn-primary">Sauvegarder</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="js/config.js"></script>
    <script src="js/jwt-utils.js"></script>
    <script>
        let stationId = null;
        let locations = [];
        
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
                window.location.href = 'stations.jsp';
            }, 2000);
        } else {
            loadData();
        }
        
        async function loadData() {
            try {
                showLoading(true);
                
                // Charger la borne et les lieux en parallèle
                const stationResponse = await authenticatedFetch(API_BASE_URL + '/api/stations/' + stationId);
                const locationsResponse = await authenticatedFetch(API_BASE_URL + '/api/locations');
                
                if (!stationResponse.ok) {
                    throw new Error('Borne non trouvée');
                }
                
                if (!locationsResponse.ok) {
                    throw new Error('Erreur lors du chargement des lieux');
                }
                
                const station = await stationResponse.json();
                const locationsData = await locationsResponse.json();
                
                locations = locationsData.content || locationsData;
                
                populateLocationSelect();
                populateForm(station);
                showLoading(false);
                
            } catch (error) {
                                showError('Erreur lors du chargement: ' + error.message);
                showLoading(false);
            }
        }
        
        function populateLocationSelect() {
            const select = document.getElementById('locationId');
            select.innerHTML = '<option value="">Sélectionnez un lieu...</option>';
            
            locations.forEach(function(location) {
                const option = document.createElement('option');
                option.value = location.id;
                option.textContent = location.label + ' - ' + location.address;
                select.appendChild(option);
            });
        }
        
        function populateForm(station) {
                        document.getElementById('name').value = station.name || '';
            document.getElementById('locationId').value = station.locationId || '';
            document.getElementById('hourlyRate').value = station.hourlyRate || '';
            document.getElementById('isActive').checked = station.isActive || false;
            
            document.getElementById('formContainer').style.display = 'block';
        }
        
        document.getElementById('stationForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const stationData = {
                name: formData.get('name'),
                locationId: parseInt(formData.get('locationId')),
                hourlyRate: parseFloat(formData.get('hourlyRate')),
                plugType: 'TYPE2S',
                isActive: formData.get('isActive') === 'on'
            };
            
            // Validation côté client
            if (!stationData.name || !stationData.locationId || 
                isNaN(stationData.hourlyRate) || stationData.hourlyRate < 0) {
                showError('Veuillez remplir tous les champs obligatoires correctement');
                return;
            }
            
            try {
                showLoading(true);
                
                const response = await authenticatedFetch(API_BASE_URL + '/api/stations/' + stationId, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(stationData)
                });
                
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || 'Erreur lors de la modification');
                }
                
                showSuccess('Borne modifiée avec succès !');
                setTimeout(function() {
                    window.location.href = 'stations.jsp';
                }, 1500);
                
            } catch (error) {
                                showError('Erreur lors de la modification: ' + error.message);
                showLoading(false);
            }
        });
        
        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('formContainer').style.display = show ? 'none' : 'block';
            
            const submitBtn = document.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = show;
                submitBtn.textContent = show ? 'Sauvegarde en cours...' : 'Sauvegarder';
            }
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
