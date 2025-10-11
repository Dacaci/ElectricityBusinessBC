<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nouvelle Réservation - Electricity Business</title>
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
            padding: 20px;
            border-bottom: 1px solid #e9ecef;
        }
        
        .header-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
            max-width: 1200px;
            margin: 0 auto;
        }
        
        .logo {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .lightning {
            font-size: 1.5em;
        }
        
        .car {
            font-size: 1.2em;
        }
        
        .logo-text {
            font-weight: bold;
            font-size: 1.2em;
            color: #2c3e50;
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 15px;
            color: #2c3e50;
        }
        
        .logout-link {
            color: #007bff;
            text-decoration: none;
        }
        
        .logout-link:hover {
            text-decoration: underline;
        }
        
        .main-title {
            text-align: center;
            font-size: 2.5em;
            margin: 40px 0;
            color: #2c3e50;
            font-weight: bold;
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
            padding: 40px;
        }
        
        .form-container {
            background: #f8f9fa;
            padding: 30px;
            border-radius: 12px;
            border: 1px solid #e9ecef;
        }
        
        .form-group {
            margin-bottom: 25px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #2c3e50;
        }
        
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #9C27B0;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
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
        
        .btn-reserve {
            background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
            color: white;
            font-size: 1.1em;
            padding: 15px 30px;
            width: 100%;
            box-shadow: 0 4px 15px rgba(44, 62, 80, 0.3);
        }
        
        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: flex-end;
            margin-top: 30px;
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
        
        .loading {
            text-align: center;
            padding: 20px;
            color: #6c757d;
        }
        
        .required {
            color: #dc3545;
        }
        
        .info-box {
            background: #e3f2fd;
            border: 1px solid #bbdefb;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
            color: #1565c0;
        }
        
        .info-box h4 {
            margin-bottom: 8px;
            font-size: 1.1em;
        }
        
        .station-info {
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 15px;
            margin-top: 10px;
            display: none;
        }
        
        .station-info h5 {
            color: #2c3e50;
            margin-bottom: 10px;
        }
        
        .station-details {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            font-size: 0.9em;
        }
        
        .detail-item {
            display: flex;
            justify-content: space-between;
        }
        
        .detail-label {
            color: #6c757d;
            font-weight: 500;
        }
        
        .detail-value {
            color: #2c3e50;
            font-weight: 600;
        }
        
        .price-calculation {
            background: #d4edda;
            border: 1px solid #c3e6cb;
            border-radius: 8px;
            padding: 15px;
            margin-top: 15px;
            display: none;
        }
        
        .price-calculation h5 {
            color: #155724;
            margin-bottom: 10px;
        }
        
        .price-breakdown {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
        }
        
        .total-price {
            border-top: 1px solid #c3e6cb;
            padding-top: 10px;
            margin-top: 10px;
            font-weight: 600;
            font-size: 1.2em;
            color: #155724;
        }
        
        .station-input-container {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .station-input-container select {
            flex: 1;
        }
        
        .find-station-link {
            color: #007bff;
            text-decoration: none;
            font-size: 0.9em;
            white-space: nowrap;
        }
        
        .find-station-link:hover {
            text-decoration: underline;
        }
        
        .datetime-row {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr 1fr;
            gap: 15px;
        }
        
        .datetime-group {
            display: flex;
            flex-direction: column;
        }
        
        .datetime-group label {
            font-size: 0.9em;
            margin-bottom: 5px;
            color: #6c757d;
        }
        
        .payment-section {
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 20px;
            margin-top: 20px;
        }
        
        .payment-section h4 {
            color: #2c3e50;
            margin-bottom: 20px;
            border-bottom: 2px solid #9C27B0;
            padding-bottom: 10px;
        }
        
        #cardNumber {
            letter-spacing: 2px;
        }
        
        #cvv {
            text-align: center;
        }
        
        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
            }
            
            .form-actions {
                flex-direction: column;
            }
            
            .station-details {
                grid-template-columns: 1fr;
            }
            
            .datetime-row {
                grid-template-columns: 1fr 1fr;
            }
            
            .station-input-container {
                flex-direction: column;
                align-items: stretch;
            }
            
            .find-station-link {
                text-align: center;
                margin-top: 10px;
            }
        }
        
        @media (max-width: 480px) {
            .datetime-row {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="header-content">
                <div class="logo">
                    <span class="lightning">⚡</span>
                    <span class="car">🚗</span>
                    <div class="logo-text">Electricity Business</div>
                </div>
                <div class="user-info">
                    <span>Utilisateur connecté: Julia RIGHI</span>
                    <a href="logout" class="logout-link">Déconnexion</a>
                </div>
            </div>
        </div>
        
        <div class="content">
            <div id="messageContainer"></div>
            
            <div class="info-box">
                <h4>ℹ️ Information importante</h4>
                <p>Veuillez sélectionner une borne disponible et choisir vos créneaux de réservation. Le montant sera calculé automatiquement.</p>
            </div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement des bornes disponibles...</p>
            </div>
            
            <div id="formContainer" class="form-container" style="display: none;">
                <form id="reservationForm">
                    <div class="form-group">
                        <label for="stationId">Borne <span class="required">*</span></label>
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
                        <label>Quand ? <span class="required">*</span></label>
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
                            <label for="cardNumber">Numéro de la carte bancaire <span class="required">*</span></label>
                            <input type="text" id="cardNumber" name="cardNumber" placeholder="1234 5678 9012 3456" maxlength="19" required>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="expiryMonth">Mois d'expiration <span class="required">*</span></label>
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
                                <label for="expiryYear">Année d'expiration <span class="required">*</span></label>
                                <select id="expiryYear" name="expiryYear" required>
                                    <option value="">Année</option>
                                </select>
                            </div>
                            
                            <div class="form-group">
                                <label for="cvv">CVV <span class="required">*</span></label>
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

    <script>
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
            
            const activeStations = stations.filter(station => station.isActive);
            console.log('Stations actives:', activeStations);
            
            if (activeStations.length === 0) {
                showError('Aucune borne active disponible. Veuillez créer des bornes d\'abord.');
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
            
            const startDateTime = startDate + 'T' + startTime + ':00';
            const endDateTime = endDate + 'T' + endTime + ':00';
            
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
                
                // L'API attend userId comme paramètre de requête
                // TODO: Récupérer dynamiquement l'ID de l'utilisateur connecté depuis la session
                const response = await fetch('http://localhost:8080/api/reservations?userId=8', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(reservationData)
                });
                
                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Erreur lors de la création de la réservation');
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
                    submitBtn.innerHTML = '⏳ Création en cours...';
                    submitBtn.disabled = true;
                }
            } else {
                const submitBtn = document.querySelector('button[type="submit"]');
                if (submitBtn) {
                    submitBtn.innerHTML = '✅ Créer la réservation';
                    submitBtn.disabled = false;
                }
            }
        }
        
        function showError(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = `<div class="error">${message}</div>`;
        }
        
        function showSuccess(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = `<div class="success">${message}</div>`;
        }
    </script>
</body>
</html>


