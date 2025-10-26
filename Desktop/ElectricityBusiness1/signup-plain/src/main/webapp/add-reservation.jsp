<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nouvelle Réservation - Electricity Business</title>
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
        <a href="add-reservation.jsp" class="nav-link active">Réserver</a>
        <a href="reservations.jsp" class="nav-link">Mes réservations</a>
        <a href="map.jsp" class="nav-link">Carte</a>
    </nav>

    <div class="container">
        <div class="content">
            <div id="messageContainer"></div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement des bornes disponibles...</p>
            </div>
            
            <div id="formContainer" class="form-container" style="display: none;">
                <form id="reservationForm">
                    <div class="form-group">
                        <label for="stationId">Borne </label>
                        <div class="station-input-container">
                            <input type="text" id="stationSearch" placeholder="Rechercher une borne..." oninput="filterStations()" style="display: none;">
                            <select id="stationId" name="stationId" required onchange="updateStationInfo()">
                                <option value="">Sélectionnez une borne...</option>
                            </select>
                            <a href="map.jsp" class="find-station-link">Trouver une borne</a>
                        </div>
                    </div>
                    
                    <div id="stationInfo" class="station-info">
                        <h5>Informations de la borne</h5>
                        <div class="station-details">
                            <div class="detail-item">
                                <span class="detail-label">Type de prise:</span>
                                <span class="detail-value" id="stationPlugType">-</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Tarif horaire:</span>
                                <span class="detail-value" id="stationHourlyRate">-</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Lieu:</span>
                                <span class="detail-value" id="stationLocation">-</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Statut:</span>
                                <span class="detail-value" id="stationStatus">-</span>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Quand ? </label>
                        <div class="datetime-row">
                            <div class="datetime-group">
                                <label for="startDate">Date début</label>
                                <input type="date" id="startDate" name="startDate" required onchange="calculatePrice()">
                            </div>
                            <div class="datetime-group">
                                <label for="startTime">Heure début</label>
                                <input type="time" id="startTime" name="startTime" required onchange="calculatePrice()">
                            </div>
                            <div class="datetime-group">
                                <label for="endDate">Date fin</label>
                                <input type="date" id="endDate" name="endDate" required onchange="calculatePrice()">
                            </div>
                            <div class="datetime-group">
                                <label for="endTime">Heure de fin</label>
                                <input type="time" id="endTime" name="endTime" required onchange="calculatePrice()">
                            </div>
                        </div>
                    </div>
                    
                    <div id="priceCalculation" class="price-calculation">
                        <h5>Montant à régler</h5>
                        <div class="price-breakdown">
                            <span>Durée:</span>
                            <span id="duration">-</span>
                        </div>
                        <div class="price-breakdown">
                            <span>Tarif horaire:</span>
                            <span id="hourlyRate">-</span>
                        </div>
                        <div class="total-price">
                            <span>Montant à régler:</span>
                            <span id="totalPrice">-</span>
                        </div>
                    </div>
                    
                    <div class="payment-section">
                        <h4>💳 Informations de paiement</h4>
                        
                        <div class="form-group">
                            <label for="cardNumber">Numéro de la carte bancaire </label>
                            <input type="text" id="cardNumber" name="cardNumber" placeholder="1234 5678 9012 3456" maxlength="19" required>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="expiryMonth">Mois d'expiration </label>
                                <select id="expiryMonth" name="expiryMonth" required>
                                    <option value="">Mois</option>
                                    <option value="01">Janvier</option>
                                    <option value="02">Février</option>
                                    <option value="03">Mars</option>
                                    <option value="04">Avril</option>
                                    <option value="05">Mai</option>
                                    <option value="06">Juin</option>
                                    <option value="07">Juillet</option>
                                    <option value="08">Août</option>
                                    <option value="09">Septembre</option>
                                    <option value="10">Octobre</option>
                                    <option value="11">Novembre</option>
                                    <option value="12">Décembre</option>
                                </select>
                            </div>
                            
                            <div class="form-group">
                                <label for="expiryYear">Année d'expiration </label>
                                <select id="expiryYear" name="expiryYear" required>
                                    <option value="">Année</option>
                                </select>
                            </div>
                            
                            <div class="form-group">
                                <label for="cvv">CVV </label>
                                <input type="text" id="cvv" name="cvv" placeholder="123" maxlength="4" required>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn btn-reserve">Réserver</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="js/jwt-utils.js"></script>
    <script>
        // Vérifier l'authentification
        if (!requireAuth()) {
            // L'utilisateur sera redirigé automatiquement
        } else {
        
        // Récupérer l'ID de l'utilisateur depuis le token JWT
        const CURRENT_USER_ID = getCurrentUserId();
        
        let stations = [];
        let locations = [];
        
        // Charger les données au chargement de la page
        document.addEventListener('DOMContentLoaded', function() {
            setMinDate();
            populateExpiryYears();
            setupCardNumberFormatting();
            const params = new URLSearchParams(window.location.search);
            const preselectId = params.get('stationId');
            loadData().then(() => {
                if (preselectId) {
                    const select = document.getElementById('stationId');
                    select.value = preselectId;
                    updateStationInfo();
                }
            });
        });
        
        function setMinDate() {
            const now = new Date();
            const today = now.toISOString().split('T')[0];
            document.getElementById('startDate').min = today;
            document.getElementById('endDate').min = today;
        }
        
        function populateExpiryYears() {
            const yearSelect = document.getElementById('expiryYear');
            const currentYear = new Date().getFullYear();
            
            for (let i = 0; i < 10; i++) {
                const year = currentYear + i;
                const option = document.createElement('option');
                option.value = year;
                option.textContent = year;
                yearSelect.appendChild(option);
            }
        }
        
        function setupCardNumberFormatting() {
            const cardInput = document.getElementById('cardNumber');
            cardInput.addEventListener('input', function(e) {
                let value = e.target.value.replace(/\s/g, '').replace(/[^0-9]/gi, '');
                let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
                e.target.value = formattedValue;
            });
        }
        
        function filterStations() {
            // Fonction pour filtrer les stations (à implémenter si nécessaire)
            console.log('Filtrage des stations...');
        }
        
        async function loadData() {
            try {
                showLoading(true);
                
                // Charger les stations et les lieux en parallèle
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
                
                const stationsData = await stationsResponse.json();
                const locationsData = await locationsResponse.json();
                
                // Gérer la pagination
                stations = stationsData.content || stationsData;
                locations = locationsData.content || locationsData;
                
                console.log('Stations chargées:', stations);
                console.log('Locations chargées:', locations);
                
                populateStationSelect();
                showLoading(false);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors du chargement: ' + error.message);
                showLoading(false);
            }
        }
        
        function populateStationSelect() {
            const select = document.getElementById('stationId');
            select.innerHTML = '<option value="">Sélectionnez une borne...</option>';
            
            console.log('Nombre de stations:', stations.length);
            console.log('Nombre de locations:', locations.length);
            
            if (!Array.isArray(stations)) {
                console.error('Stations n\'est pas un tableau:', stations);
                showError('Erreur: Les données des bornes sont invalides');
                return;
            }
            
            if (!Array.isArray(locations)) {
                console.error('Locations n\'est pas un tableau:', locations);
                showError('Erreur: Les données des lieux sont invalides');
                return;
            }
            
            // Filtrer les stations actives ET qui n'appartiennent pas à l'utilisateur
            const activeStations = stations.filter(station => {
                if (!station.isActive) return false;
                
                // Exclure les propres stations de l'utilisateur
                const location = locations.find(loc => loc.id === station.locationId);
                if (location && location.ownerId === CURRENT_USER_ID) {
                    return false; // Ne pas afficher ses propres stations
                }
                
                return true;
            });
            
            console.log('Stations actives (hors propres stations):', activeStations);
            
            if (activeStations.length === 0) {
                showError('Aucune borne disponible pour réservation. Vous ne pouvez pas réserver vos propres bornes.');
                document.getElementById('formContainer').style.display = 'block';
                return;
            }
            
            activeStations.forEach(station => {
                console.log('Traitement station:', station);
                console.log('  - ID:', station.id);
                console.log('  - Name:', station.name);
                console.log('  - HourlyRate:', station.hourlyRate);
                console.log('  - LocationId:', station.locationId);
                
                const location = locations.find(loc => loc.id === station.locationId);
                console.log('Location trouvée:', location);
                if (location) {
                    console.log('  - Location label:', location.label);
                }
                
                const locationName = location ? location.label : 'Lieu inconnu';
                
                const option = document.createElement('option');
                option.value = station.id;
                
                // Construction du texte étape par étape pour debug
                let optionText = '';
                if (station.name) {
                    optionText += station.name;
                } else {
                    optionText += 'Borne sans nom';
                }
                
                optionText += ' - ';
                optionText += locationName;
                optionText += ' (';
                
                if (station.hourlyRate !== undefined && station.hourlyRate !== null) {
                    optionText += station.hourlyRate;
                } else {
                    optionText += '0.00';
                }
                
                optionText += '€/h)';
                
                console.log('  - Texte option finale:', optionText);
                
                option.textContent = optionText;
                option.dataset.station = JSON.stringify(station);
                select.appendChild(option);
            });
            
            document.getElementById('formContainer').style.display = 'block';
        }
        
        function updateStationInfo() {
            const select = document.getElementById('stationId');
            const selectedOption = select.options[select.selectedIndex];
            
            if (selectedOption.value) {
                const station = JSON.parse(selectedOption.dataset.station);
                const location = locations.find(loc => loc.id === station.locationId);
                
                document.getElementById('stationPlugType').textContent = station.plugType;
                document.getElementById('stationHourlyRate').textContent = `${station.hourlyRate} €/h`;
                document.getElementById('stationLocation').textContent = location ? location.label : 'Lieu inconnu';
                document.getElementById('stationStatus').textContent = station.isActive ? 'Active' : 'Inactive';
                
                document.getElementById('stationInfo').style.display = 'block';
                calculatePrice();
            } else {
                document.getElementById('stationInfo').style.display = 'none';
                document.getElementById('priceCalculation').style.display = 'none';
            }
        }
        
        function calculatePrice() {
            console.log('=== calculatePrice appelée ===');
            const startDate = document.getElementById('startDate').value;
            const startTime = document.getElementById('startTime').value;
            const endDate = document.getElementById('endDate').value;
            const endTime = document.getElementById('endTime').value;
            const stationId = document.getElementById('stationId').value;
            
            console.log('StartDate:', startDate, 'StartTime:', startTime);
            console.log('EndDate:', endDate, 'EndTime:', endTime);
            console.log('StationId:', stationId);
            
            if (!startDate || !startTime || !endDate || !endTime || !stationId) {
                console.log('Données manquantes pour calculer le prix');
                document.getElementById('priceCalculation').style.display = 'none';
                return;
            }
            
            const start = new Date(startDate + 'T' + startTime);
            const end = new Date(endDate + 'T' + endTime);
            
            console.log('Start DateTime:', start);
            console.log('End DateTime:', end);
            
            if (end <= start) {
                console.log('Date de fin avant ou égale à la date de début');
                document.getElementById('priceCalculation').style.display = 'none';
                return;
            }
            
            const station = stations.find(s => s.id == stationId);
            console.log('Station trouvée:', station);
            
            if (!station) {
                console.error('Station non trouvée avec ID:', stationId);
                return;
            }
            
            const durationMs = end - start;
            const durationHours = durationMs / (1000 * 60 * 60);
            const totalPrice = durationHours * station.hourlyRate;
            
            console.log('Durée (heures):', durationHours);
            console.log('Tarif horaire:', station.hourlyRate);
            console.log('Prix total:', totalPrice);
            
            document.getElementById('duration').textContent = durationHours.toFixed(2) + ' heures';
            document.getElementById('hourlyRate').textContent = station.hourlyRate + ' €/h';
            document.getElementById('totalPrice').textContent = totalPrice.toFixed(2) + ' €';
            
            console.log('Affichage du bloc de calcul');
            document.getElementById('priceCalculation').style.display = 'block';
        }
        
        document.getElementById('reservationForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            
            // Combiner date et heure
            const startDate = formData.get('startDate');
            const startTime = formData.get('startTime');
            const endDate = formData.get('endDate');
            const endTime = formData.get('endTime');
            
            const startDateTime = startDate + 'T' + startTime + ':00.000';
            const endDateTime = endDate + 'T' + endTime + ':00.000';
            
            const reservationData = {
                stationId: parseInt(formData.get('stationId')),
                startTime: startDateTime,
                endTime: endDateTime
            };
            
            // Validation des informations de paiement
            const cardNumber = formData.get('cardNumber');
            const expiryMonth = formData.get('expiryMonth');
            const expiryYear = formData.get('expiryYear');
            const cvv = formData.get('cvv');
            
            if (!cardNumber || !expiryMonth || !expiryYear || !cvv) {
                showError('Veuillez remplir toutes les informations de paiement');
                return;
            }
            
            // Validation basique du numéro de carte (16 chiffres)
            const cleanCardNumber = cardNumber.replace(/\s/g, '');
            if (cleanCardNumber.length !== 16 || !/^\d+$/.test(cleanCardNumber)) {
                showError('Le numéro de carte doit contenir 16 chiffres');
                return;
            }
            
            // Validation du CVV (3 ou 4 chiffres)
            if (!/^\d{3,4}$/.test(cvv)) {
                showError('Le CVV doit contenir 3 ou 4 chiffres');
                return;
            }
            
            console.log('Données de réservation envoyées:', reservationData);
            
            // Validation côté client
            if (!reservationData.stationId || !reservationData.startTime || !reservationData.endTime) {
                showError('Veuillez remplir tous les champs obligatoires');
                return;
            }
            
            const start = new Date(reservationData.startTime);
            const end = new Date(reservationData.endTime);
            
            if (end <= start) {
                showError('L\'heure de fin doit être postérieure à l\'heure de début');
                return;
            }
            
            if (start < new Date()) {
                showError('La réservation ne peut pas être dans le passé');
                return;
            }
            
            try {
                showLoading(true);
                
                // Utiliser l'API authentifiée (l'userId est extrait du token JWT)
                const response = await authenticatedFetch('http://localhost:8080/api/reservations', {
                    method: 'POST',
                    body: JSON.stringify(reservationData)
                });
                
                if (!response.ok) {
                    let errorMessage = 'Erreur lors de la création de la réservation';
                    try {
                        const errorData = await response.json();
                        errorMessage = errorData || errorMessage;
                    } catch (e) {
                        const errorText = await response.text();
                        errorMessage = errorText || errorMessage;
                    }
                    throw new Error(errorMessage);
                }
                
                showSuccess('Réservation créée avec succès !');
                setTimeout(() => {
                    window.location.href = 'reservations.jsp';
                }, 2000);
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors de la création: ' + error.message);
                showLoading(false);
            }
        });
        
        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('formContainer').style.display = show ? 'none' : 'block';
            
            if (show) {
                const submitBtn = document.querySelector('button[type="submit"]');
                if (submitBtn) {
                    submitBtn.innerHTML = 'Création en cours...';
                    submitBtn.disabled = true;
                }
            } else {
                const submitBtn = document.querySelector('button[type="submit"]');
                if (submitBtn) {
                    submitBtn.innerHTML = 'Créer la réservation';
                    submitBtn.disabled = false;
                }
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
        
        } // Fin du bloc else
    </script>
    <script src="js/auth.js"></script>
</body>
</html>


