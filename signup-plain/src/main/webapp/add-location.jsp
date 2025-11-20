<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ajouter un Lieu - Electricity Business</title>
    <link rel="stylesheet" href="css/common-styles.css?v=20251022v5">
    <meta http-equiv="Content-Security-Policy" content="default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; connect-src 'self' https://electricity-business-backend-z373.onrender.com http://localhost:8080 https://nominatim.openstreetmap.org; script-src 'self' 'unsafe-inline' 'unsafe-eval';">
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
        <a href="add-location.jsp" class="nav-link active">Ajouter un lieu</a>
        <a href="locations.jsp" class="nav-link">Mes lieux</a>
        <a href="add-station.jsp" class="nav-link">Ajouter une borne</a>
        <a href="stations.jsp" class="nav-link">Mes bornes</a>
        <a href="add-reservation.jsp" class="nav-link">Réserver</a>
        <a href="reservations.jsp" class="nav-link">Mes réservations</a>
        <a href="map.jsp" class="nav-link">Carte</a>
    </nav>
    
    <div class="container">
        
        <div class="content">
            <div id="messageContainer"></div>
            
            <div class="form-container">
                <form id="locationForm">
                    <div class="form-group">
                        <label for="label">Nom du lieu <span class="required">*</span></label>
                        <input type="text" id="label" name="label" required maxlength="255" 
                               placeholder="Ex: Parking du centre commercial">
                    </div>
                    
                    <div class="form-group">
                        <label for="address">Adresse complète <span class="required">*</span></label>
                        <textarea id="address" name="address" required maxlength="500" 
                                  placeholder="Ex: 123 Rue de la Paix, 75001 Paris, France"></textarea>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="latitude">Latitude <span class="required">*</span></label>
                            <input type="number" id="latitude" name="latitude" step="any" required 
                                   placeholder="Ex: 48.8566">
                        </div>
                        
                        <div class="form-group">
                            <label for="longitude">Longitude <span class="required">*</span></label>
                            <input type="number" id="longitude" name="longitude" step="any" required 
                                   placeholder="Ex: 2.3522">
                        </div>
                    </div>
                    
                    <div class="geo-button-container">
                        <button type="button" class="btn btn-secondary" id="geoButton">
                            Utiliser ma position actuelle
                        </button>
                        <button type="button" class="btn btn-secondary" onclick="searchAddress()" style="margin-left: 10px;">
                            Rechercher l'adresse
                        </button>
                    </div>
                    <p style="font-size: 0.9em; color: #666; margin-top: 10px;">
                        Astuce : Saisissez l'adresse et cliquez sur "Rechercher l'adresse" pour obtenir les coordonnées automatiquement
                    </p>
                    
                    <div class="form-group">
                        <label for="description">Description (optionnel)</label>
                        <textarea id="description" name="description" maxlength="1000" 
                                  placeholder="Informations supplémentaires sur le lieu..."></textarea>
                    </div>
                    
                    <div class="form-actions">
                        <a href="locations.jsp" class="btn btn-secondary">Annuler</a>
                        <button type="submit" class="btn" id="submitBtn">Créer le lieu</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="js/config.js"></script>
    <script src="js/jwt-utils.js"></script>
    <script src="js/nominatim-utils.js"></script>
    <script>
        // Vérifier l'authentification
        if (!requireAuth()) {
            // L'utilisateur sera redirigé automatiquement par requireAuth()
        } else {
        
        // Récupérer l'ID de l'utilisateur depuis le token JWT
        const CURRENT_USER_ID = getCurrentUserId();
        
                document.getElementById('locationForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const locationData = {
                label: formData.get('label').trim(),
                address: formData.get('address').trim(),
                latitude: formData.get('latitude'),
                longitude: formData.get('longitude'),
                description: formData.get('description')?.trim() || null,
                ownerId: CURRENT_USER_ID // Utilisation du vrai ID utilisateur
            };
            
            // Validation côté client
            if (!locationData.label || !locationData.address) {
                showError('Veuillez remplir tous les champs obligatoires');
                return;
            }
            
            if (isNaN(locationData.latitude) || isNaN(locationData.longitude)) {
                showError('Les coordonnées GPS doivent être des nombres valides');
                return;
            }
            
            try {
                showLoading(true);
                
                                const response = await authenticatedFetch(API_BASE_URL + '/api/locations', {
                    method: 'POST',
                    body: JSON.stringify(locationData)
                });
                
                                if (!response.ok) {
                    const errorText = await response.text();
                                        throw new Error(errorText || 'Erreur lors de la création du lieu');
                }
                
                const createdLocation = await response.json();
                                showSuccess('Lieu créé avec succès !');
                setTimeout(() => {
                    window.location.href = 'locations.jsp';
                }, 1500);
                
            } catch (error) {
                                showError('Erreur lors de la création: ' + error.message);
                showLoading(false);
            }
        });
        
        // Bouton de géolocalisation
        document.getElementById('geoButton').addEventListener('click', function() {
            getCurrentLocation();
        });
        
        // Fonction pour obtenir les coordonnées GPS automatiquement avec Nominatim
        async function getCurrentLocation() {
            showLoading(true);
            const btn = document.getElementById('geoButton');
            btn.disabled = true;
            btn.innerHTML = 'Détection en cours...';
            
            try {
                // Utiliser la fonction améliorée avec Nominatim
                const location = await getAccurateLocation();
                
                                // Remplir les champs
                document.getElementById('latitude').value = location.latitude.toFixed(6);
                document.getElementById('longitude').value = location.longitude.toFixed(6);
                
                // Remplir aussi l'adresse si disponible
                if (location.city) {
                    document.getElementById('city').value = location.city;
                }
                if (location.postalCode) {
                    document.getElementById('postalCode').value = location.postalCode;
                }
                if (location.address && !document.getElementById('address').value) {
                    document.getElementById('address').value = location.address;
                }
                
                // Message avec source et précision
                let successMsg = 'Position détectée avec succès !';
                if (location.source === 'IP Address') {
                    successMsg += '\nPosition approximative (basée sur votre IP)';
                } else {
                    successMsg += '\nPrécision : ' + Math.round(location.accuracy) + 'm';
                }
                if (location.city) {
                    successMsg += '\nVille détectée : ' + location.city;
                }
                
                showSuccess(successMsg);
                btn.disabled = false;
                btn.innerHTML = 'Utiliser ma position actuelle';
                showLoading(false);
                
            } catch (error) {
                                showError('Erreur : ' + error.message + '\nVeuillez saisir les coordonnées manuellement.');
                btn.disabled = false;
                btn.innerHTML = 'Utiliser ma position actuelle';
                showLoading(false);
            }
        }
        
        // Fonction de recherche d'adresse avec Nominatim
        async function searchAddress() {
            const address = document.getElementById('address').value;
            const city = document.getElementById('city').value;
            const postalCode = document.getElementById('postalCode').value;
            
            if (!address && !city) {
                showError('Veuillez saisir au moins une adresse ou une ville');
                return;
            }
            
            const query = [address, postalCode, city].filter(x => x).join(', ');
            
            showLoading(true);
            
            try {
                const results = await geocodeAddress(query);
                
                if (results.length === 0) {
                    showError('Aucune adresse trouvée. Vérifiez votre saisie.');
                    showLoading(false);
                    return;
                }
                
                // Prendre le premier résultat (le plus pertinent)
                const location = results[0];
                
                                // Remplir les coordonnées
                document.getElementById('latitude').value = location.latitude.toFixed(6);
                document.getElementById('longitude').value = location.longitude.toFixed(6);
                
                // Mettre à jour les champs d'adresse avec les données précises
                if (location.city) {
                    document.getElementById('city').value = location.city;
                }
                if (location.postalCode) {
                    document.getElementById('postalCode').value = location.postalCode;
                }
                
                showSuccess('Adresse trouvée : ' + location.displayName);
                showLoading(false);
                
            } catch (error) {
                                showError('Erreur lors de la recherche d\'adresse : ' + error.message);
                showLoading(false);
            }
        }
        
        // Ancienne fonction de géolocalisation simple (fallback)
        function getCurrentLocationSimple() {
            if (!navigator.geolocation) {
                showError('La géolocalisation n\'est pas supportée par ce navigateur');
                return;
            }
            
            showLoading(true);
            const btn = document.getElementById('geoButton');
            btn.disabled = true;
            btn.innerHTML = 'Détection en cours...';
            
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    document.getElementById('latitude').value = position.coords.latitude.toFixed(6);
                    document.getElementById('longitude').value = position.coords.longitude.toFixed(6);
                    showSuccess('Position détectée avec succès !');
                    btn.disabled = false;
                    btn.innerHTML = 'Utiliser ma position actuelle';
                    showLoading(false);
                },
                function(error) {
                                        let errorMessage = 'Impossible d\'obtenir la position.';
                    
                    switch(error.code) {
                        case error.PERMISSION_DENIED:
                            errorMessage = 'Permission de géolocalisation refusée.';
                            break;
                        case error.POSITION_UNAVAILABLE:
                            errorMessage = 'Position indisponible.';
                            break;
                        case error.TIMEOUT:
                            errorMessage = 'Délai de géolocalisation dépassé.';
                            break;
                    }
                    
                    showError(errorMessage);
                    btn.disabled = false;
                    btn.innerHTML = 'Utiliser ma position actuelle';
                    showLoading(false);
                },
                {
                    enableHighAccuracy: true,
                    timeout: 10000,
                    maximumAge: 60000
                }
            );
        }
        
        function showLoading(show) {
            const submitBtn = document.getElementById('submitBtn');
            if (show) {
                submitBtn.innerHTML = 'Création en cours...';
                submitBtn.disabled = true;
            } else {
                submitBtn.innerHTML = 'Créer le lieu';
                submitBtn.disabled = false;
            }
        }
        
        function showError(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="error">' + message + '</div>';
            // Auto-hide après 5 secondes
            setTimeout(() => {
                container.innerHTML = '';
            }, 5000);
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
