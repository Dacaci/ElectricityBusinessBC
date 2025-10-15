<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Effectuer une réservation - Electricity Business</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: Arial, sans-serif;
            background: white;
            min-height: 100vh;
        }
        
        .container {
            width: 100%;
            padding: 20px;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 1px solid #ddd;
        }
        
        .logo {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 1.2em;
            font-weight: bold;
        }
        
        .user-info {
            color: #666;
        }
        
        .logout-link {
            color: #007bff;
            text-decoration: none;
            margin-left: 15px;
        }
        
        h1 {
            text-align: center;
            margin: 40px 0;
            color: #333;
            font-size: 2.5em;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }
        
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        
        .station-row {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .station-row select {
            flex: 1;
        }
        
        .find-link {
            color: #007bff;
            text-decoration: none;
            white-space: nowrap;
        }
        
        .datetime-row {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr 1fr;
            gap: 15px;
        }
        
        .datetime-group label {
            font-size: 0.9em;
            color: #666;
        }
        
        .amount-section {
            padding: 15px;
            margin: 20px 0;
            text-align: center;
            font-size: 1.2em;
            font-weight: bold;
            color: #333;
            border: 1px solid #ddd;
        }
        
        .payment-section {
            padding: 20px;
            margin: 20px 0;
            border: 1px solid #ddd;
        }
        
        .payment-section h3 {
            margin-bottom: 20px;
            color: #333;
        }
        
        .payment-row {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr;
            gap: 15px;
            margin-top: 15px;
        }
        
        .btn-reserve {
            width: 100%;
            background: #333;
            color: white;
            padding: 15px;
            border: none;
            border-radius: 5px;
            font-size: 1.1em;
            font-weight: bold;
            cursor: pointer;
            margin-top: 20px;
        }
        
        .btn-reserve:hover {
            background: #555;
        }
        
        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        
        .success {
            background: #d4edda;
            color: #155724;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="logo">
                ELECTRIC VEHICLE Electricity Business
            </div>
            <div class="user-info">
                Utilisateur connecté: Julia RIGHI
                <a href="logout" class="logout-link">Déconnexion</a>
            </div>
        </div>
        
        <h1>Effectuer une réservation</h1>
        
        <div id="messageContainer"></div>
        
        <form id="reservationForm">
            <!-- Sélection de borne -->
            <div class="form-group">
                <label>Borne</label>
                <div class="station-row">
                    <select id="stationId" name="stationId" required onchange="updateStationInfo()">
                        <option value="">Sélectionnez une borne...</option>
                    </select>
                    <a href="map.jsp" class="find-link">Trouver une borne</a>
                </div>
            </div>
            
            <!-- Dates et heures -->
            <div class="form-group">
                <label>Quand ?</label>
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
            
            <!-- Montant -->
            <div id="amountSection" class="amount-section" style="display: none;">
                Montant à régler: <span id="totalPrice">-</span> €
            </div>
            
            <!-- Section paiement -->
            <div class="payment-section">
                <h3>Informations de paiement</h3>
                
                <div class="form-group">
                    <label for="cardNumber">Numéro de la carte bancaire</label>
                    <input type="text" id="cardNumber" name="cardNumber" placeholder="1234 5678 9012 3456" maxlength="19" required>
                </div>
                
                <div class="payment-row">
                    <div class="form-group">
                        <label for="expiryMonth">Mois d'expiration</label>
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
                        <label for="expiryYear">Année d'expiration</label>
                        <select id="expiryYear" name="expiryYear" required>
                            <option value="">Année</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="cvv">CVV</label>
                        <input type="text" id="cvv" name="cvv" placeholder="123" maxlength="4" required>
                    </div>
                </div>
            </div>
            
            <button type="submit" class="btn-reserve">Réserver</button>
        </form>
    </div>

    <script>
        let stations = [];
        let locations = [];
        
        document.addEventListener('DOMContentLoaded', function() {
            setMinDate();
            populateExpiryYears();
            setupCardNumberFormatting();
            loadData();
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
        
        async function loadData() {
            try {
                const [stationsResponse, locationsResponse] = await Promise.all([
                    fetch('http://localhost:8080/api/stations'),
                    fetch('http://localhost:8080/api/locations')
                ]);
                
                const stationsData = await stationsResponse.json();
                const locationsData = await locationsResponse.json();
                
                stations = stationsData.content || stationsData;
                locations = locationsData.content || locationsData;
                
                populateStationSelect();
                
            } catch (error) {
                console.error('Erreur:', error);
                showError('Erreur lors du chargement: ' + error.message);
            }
        }
        
        function populateStationSelect() {
            const select = document.getElementById('stationId');
            select.innerHTML = '<option value="">Sélectionnez une borne...</option>';
            
            const activeStations = stations.filter(station => station.isActive);
            
            activeStations.forEach(station => {
                const location = locations.find(loc => loc.id === station.locationId);
                const locationName = location ? location.label : 'Lieu inconnu';
                
                const option = document.createElement('option');
                option.value = station.id;
                option.textContent = station.name + ' - ' + locationName + ' (' + station.hourlyRate + '€/h)';
                select.appendChild(option);
            });
        }
        
        function updateStationInfo() {
            calculatePrice();
        }
        
        function calculatePrice() {
            const startDate = document.getElementById('startDate').value;
            const startTime = document.getElementById('startTime').value;
            const endDate = document.getElementById('endDate').value;
            const endTime = document.getElementById('endTime').value;
            const stationId = document.getElementById('stationId').value;
            
            if (!startDate || !startTime || !endDate || !endTime || !stationId) {
                document.getElementById('amountSection').style.display = 'none';
                return;
            }
            
            const start = new Date(startDate + 'T' + startTime);
            const end = new Date(endDate + 'T' + endTime);
            
            if (end <= start) {
                document.getElementById('amountSection').style.display = 'none';
                return;
            }
            
            const station = stations.find(s => s.id == stationId);
            if (!station) return;
            
            const durationMs = end - start;
            const durationHours = durationMs / (1000 * 60 * 60);
            const totalPrice = durationHours * station.hourlyRate;
            
            document.getElementById('totalPrice').textContent = totalPrice.toFixed(2);
            document.getElementById('amountSection').style.display = 'block';
        }
        
        document.getElementById('reservationForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            
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
            
            const cardNumber = formData.get('cardNumber');
            const expiryMonth = formData.get('expiryMonth');
            const expiryYear = formData.get('expiryYear');
            const cvv = formData.get('cvv');
            
            if (!cardNumber || !expiryMonth || !expiryYear || !cvv) {
                showError('Veuillez remplir toutes les informations de paiement');
                return;
            }
            
            const cleanCardNumber = cardNumber.replace(/\s/g, '');
            if (cleanCardNumber.length !== 16 || !/^\d+$/.test(cleanCardNumber)) {
                showError('Le numéro de carte doit contenir 16 chiffres');
                return;
            }
            
            if (!/^\d{3,4}$/.test(cvv)) {
                showError('Le CVV doit contenir 3 ou 4 chiffres');
                return;
            }
            
            try {
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
            }
        });
        
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
