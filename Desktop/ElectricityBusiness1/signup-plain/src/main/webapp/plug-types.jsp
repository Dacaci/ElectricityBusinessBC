<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Types de Prises - Electricity Business</title>
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
        <a href="reservations.jsp" class="nav-link">Mes réservations</a>
        <a href="vehicles.jsp" class="nav-link">Mes véhicules</a>
        <a href="map.jsp" class="nav-link">Carte</a>
    </nav>

    <div class="container">
        <div class="content">
            <div class="section-header">
                <h2>Types de Prises Disponibles</h2>
                <button class="btn btn-primary" onclick="showAddForm()">Ajouter un type de prise</button>
            </div>
            
            <div id="messageContainer"></div>
            
            <!-- Formulaire d'ajout (masqué par défaut) -->
            <div id="addFormContainer" style="display: none;" class="form-container">
                <h3>Nouveau Type de Prise</h3>
                <form id="addPlugTypeForm">
                    <div class="form-group">
                        <label for="name">Nom</label>
                        <input type="text" id="name" class="form-control" required placeholder="Ex: Type 2, CCS, CHAdeMO">
                    </div>
                    
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" class="form-control" rows="3" placeholder="Description optionnelle"></textarea>
                    </div>
                    
                    <div class="form-group">
                        <label for="maxPower">Puissance maximale (kW)</label>
                        <input type="number" id="maxPower" class="form-control" step="0.1" min="0" placeholder="Ex: 22">
                    </div>
                    
                    <div class="actions">
                        <button type="button" class="btn btn-secondary" onclick="hideAddForm()">Annuler</button>
                        <button type="submit" class="btn btn-primary">Ajouter</button>
                    </div>
                </form>
            </div>
            
            <div id="loadingIndicator" class="loading">
                <p>Chargement des types de prises...</p>
            </div>

            <div id="plugTypesContainer" style="display: none;">
                <table id="plugTypesTable">
                    <thead>
                        <tr>
                            <th>Nom</th>
                            <th>Description</th>
                            <th>Puissance max</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="plugTypesBody"></tbody>
                </table>
                <div id="plugTypesEmpty" class="no-data" style="display: none;">
                    Aucun type de prise enregistré.
                </div>
            </div>
        </div>
    </div>

    <script src="js/jwt-utils.js"></script>
    <script>
        if (!requireAuth()) {
            // Redirection automatique
        } else {
            const user = getAuthUser();
            if (user) {
                document.getElementById('welcomeMessage').textContent = 'Bienvenue, ' + user.firstName + ' ' + user.lastName;
            }
            
            loadPlugTypes();
            document.getElementById('addPlugTypeForm').addEventListener('submit', handleAddSubmit);
        }

        async function loadPlugTypes() {
            showLoading(true);
            try {
                const response = await fetch('http://localhost:8080/api/plug-types');
                if (!response.ok) {
                    throw new Error('Erreur lors du chargement des types de prises');
                }
                
                const plugTypes = await response.json();
                displayPlugTypes(plugTypes);
                showLoading(false);
            } catch (error) {
                                showError('Erreur lors du chargement: ' + error.message);
                showLoading(false);
            }
        }

        function displayPlugTypes(plugTypes) {
            const tbody = document.getElementById('plugTypesBody');
            const emptyMessage = document.getElementById('plugTypesEmpty');
            
            if (!plugTypes || plugTypes.length === 0) {
                emptyMessage.style.display = 'block';
                tbody.innerHTML = '';
                return;
            }
            
            emptyMessage.style.display = 'none';
            
            let html = '';
            plugTypes.forEach(plugType => {
                html += '<tr>';
                html += '<td><strong>' + plugType.name + '</strong></td>';
                html += '<td>' + (plugType.description || '-') + '</td>';
                html += '<td>' + (plugType.maxPower ? plugType.maxPower + ' kW' : '-') + '</td>';
                html += '<td>';
                html += '<button class="btn btn-danger" onclick="deletePlugType(' + plugType.id + ', \'' + plugType.name + '\')">Supprimer</button>';
                html += '</td>';
                html += '</tr>';
            });
            
            tbody.innerHTML = html;
        }

        function showAddForm() {
            document.getElementById('addFormContainer').style.display = 'block';
            document.getElementById('name').focus();
        }

        function hideAddForm() {
            document.getElementById('addFormContainer').style.display = 'none';
            document.getElementById('addPlugTypeForm').reset();
        }

        async function handleAddSubmit(event) {
            event.preventDefault();
            
            const plugTypeData = {
                name: document.getElementById('name').value.trim(),
                description: document.getElementById('description').value.trim() || null,
                maxPower: document.getElementById('maxPower').value ? parseFloat(document.getElementById('maxPower').value) : null
            };
            
            try {
                const response = await fetch('http://localhost:8080/api/plug-types', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(plugTypeData)
                });
                
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || 'Erreur lors de l\'ajout');
                }
                
                showSuccess('Type de prise ajouté avec succès !');
                hideAddForm();
                loadPlugTypes();
                
            } catch (error) {
                                showError('Erreur: ' + error.message);
            }
        }

        async function deletePlugType(id, name) {
            if (!confirm('Êtes-vous sûr de vouloir supprimer le type de prise "' + name + '" ?')) {
                return;
            }
            
            try {
                const response = await fetch('http://localhost:8080/api/plug-types/' + id, {
                    method: 'DELETE'
                });
                
                if (!response.ok) {
                    throw new Error('Erreur lors de la suppression');
                }
                
                showSuccess('Type de prise supprimé avec succès !');
                loadPlugTypes();
            } catch (error) {
                                showError('Erreur lors de la suppression');
            }
        }

        function showLoading(show) {
            document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
            document.getElementById('plugTypesContainer').style.display = show ? 'none' : 'block';
        }

        function showError(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="error">' + message + '</div>';
            setTimeout(() => { container.innerHTML = ''; }, 5000);
        }

        function showSuccess(message) {
            const container = document.getElementById('messageContainer');
            container.innerHTML = '<div class="success">' + message + '</div>';
            setTimeout(() => { container.innerHTML = ''; }, 3000);
        }

        function logout() {
            localStorage.clear();
            sessionStorage.clear();
            window.location.replace('/login.jsp?message=logout');
        }
    </script>
</body>
</html>







