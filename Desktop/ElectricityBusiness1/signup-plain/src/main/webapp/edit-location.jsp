<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modifier un Lieu - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251022v5">
    <meta http-equiv="Content-Security-Policy" content="default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; connect-src 'self' http://localhost:8080; script-src 'self' 'unsafe-inline' 'unsafe-eval';">
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
            <h2>Modifier un lieu</h2>
            
            <div id="messageContainer"></div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement des informations...</p>
            </div>
            
            <div id="formContainer" class="card" style="display: none;">
                <form id="locationForm">
                    <div class="form-group">
                        <label for="label">Nom du lieu <span style="color: #ef4444;">*</span></label>
                        <input type="text" id="label" name="label" class="form-control" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="address">Adresse <span style="color: #ef4444;">*</span></label>
                        <input type="text" id="address" name="address" class="form-control" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control" rows="3"></textarea>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="latitude">Latitude <span style="color: #ef4444;">*</span></label>
                            <input type="number" id="latitude" name="latitude" class="form-control" step="0.000001" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="longitude">Longitude <span style="color: #ef4444;">*</span></label>
                            <input type="number" id="longitude" name="longitude" class="form-control" step="0.000001" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>
                            <input type="checkbox" id="isActive" name="isActive"> 
                            Lieu actif (visible et disponible)
                        </label>
                    </div>
                    
                    <div class="actions">
                        <a href="locations.jsp" class="btn btn-secondary">Annuler</a>
                        <button type="submit" class="btn btn-primary">Sauvegarder</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="js/jwt-utils.js"></script>
    <script>
        let locationId = null;
        
        // Vérifier l'authentification
        if (!requireAuth()) {
            // L'utilisateur sera redirigé automatiquement
        } else {
        
        // Récupérer l'ID du lieu depuis l'URL
        const urlParams = new URLSearchParams(window.location.search);
        locationId = urlParams.get('id');
        
        if (!locationId) {
            showError('ID du lieu manquant');
            setTimeout(function() {
                window.location.href = 'locations.jsp';
            }, 2000);
        } else {
            loadData();
        }
        
        async function loadData() {
            try {
                showLoading(true);
                
                const response = await authenticatedFetch('http://localhost:8080/api/locations/' + locationId);
                
                if (!response.ok) {
                    throw new Error('Lieu non trouvé');
                }
                
                const location = await response.json();
                populateForm(location);
                showLoading(false);
                
            } catch (error) {
                                showError('Erreur lors du chargement: ' + error.message);
                showLoading(false);
            }
        }
        
        function populateForm(location) {
                        document.getElementById('label').value = location.label || '';
            document.getElementById('address').value = location.address || '';
            document.getElementById('description').value = location.description || '';
            document.getElementById('latitude').value = location.latitude || '';
            document.getElementById('longitude').value = location.longitude || '';
            document.getElementById('isActive').checked = location.isActive || false;
            
            document.getElementById('formContainer').style.display = 'block';
        }
        
        document.getElementById('locationForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const locationData = {
                label: formData.get('label'),
                address: formData.get('address'),
                description: formData.get('description') || null,
                latitude: parseFloat(formData.get('latitude')),
                longitude: parseFloat(formData.get('longitude')),
                isActive: formData.get('isActive') === 'on'
            };
            
            // Validation côté client
            if (!locationData.label || !locationData.address || 
                isNaN(locationData.latitude) || isNaN(locationData.longitude)) {
                showError('Veuillez remplir tous les champs obligatoires correctement');
                return;
            }
            
            try {
                showLoading(true);
                
                const response = await authenticatedFetch('http://localhost:8080/api/locations/' + locationId, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(locationData)
                });
                
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || 'Erreur lors de la modification');
                }
                
                showSuccess('Lieu modifié avec succès !');
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
