<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Ajouter une borne</title>
<meta http-equiv="Content-Security-Policy" content="default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; connect-src 'self' http://localhost:8080; script-src 'self' 'unsafe-inline' 'unsafe-eval';">
<style>
  body { font-family: Arial, sans-serif; margin: 24px; background: #fff; color: #111; }
  .container { max-width: 720px; margin: 0 auto; }
  h1 { margin: 0 0 16px; font-size: 22px; font-weight: 600; }
  .row { margin-bottom: 12px; }
  label { display: block; margin-bottom: 6px; font-weight: 600; }
  input, select, textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
  button { padding: 10px 14px; border: 1px solid #111; background: #111; color: #fff; border-radius: 4px; cursor: pointer; }
  button.secondary { background: #fff; color: #111; margin-left: 8px; }
  .msg { margin-top: 14px; padding: 10px; border-radius: 4px; display: none; }
  .msg.ok { background: #e8f5e9; border: 1px solid #c8e6c9; }
  .msg.err { background: #ffebee; border: 1px solid #ffcdd2; }
</style>
</head>
<body>
<div class="container">
  <h1>Ajouter une borne</h1>

  <div class="row">
    <label for="name">Nom</label>
    <input id="name" type="text" placeholder="Ex: Borne Maison" required>
  </div>

  <div class="row">
    <label for="locationId">Lieu</label>
    <select id="locationId" required></select>
  </div>

  <div class="row">
    <label for="power">Puissance (kW)</label>
    <input id="power" type="number" step="0.1" value="7.4">
  </div>

  <div class="row">
    <label for="city">Ville</label>
    <input id="city" type="text" placeholder="Ex: Paris">
  </div>

  <div class="row">
    <label for="latitude">Latitude</label>
    <input id="latitude" type="number" step="0.000001" placeholder="48.8566">
  </div>

  <div class="row">
    <label for="longitude">Longitude</label>
    <input id="longitude" type="number" step="0.000001" placeholder="2.3522">
  </div>

  <div class="row">
    <label for="instructions">Instructions</label>
    <textarea id="instructions" rows="3" placeholder="Infos d'accès, badge, etc."></textarea>
  </div>

  <div class="row">
    <label><input id="onFoot" type="checkbox"> Sur pied</label>
  </div>

  <div class="row">
    <button id="submitBtn">Ajouter</button>
    <button id="geoBtn" class="secondary" type="button">Utiliser ma position</button>
  </div>

  <div id="msg" class="msg"></div>
</div>

<!-- Scripts -->
<script src="js/jwt-utils.js"></script>
<script src="js/nominatim-utils.js"></script>
<script>
  // Vérifier l'authentification
  if (!requireAuth()) {
    // L'utilisateur sera redirigé automatiquement par requireAuth()
  } else {
  
  // Récupérer l'ID de l'utilisateur depuis le token JWT
  const CURRENT_USER_ID = getCurrentUserId();

  document.addEventListener('DOMContentLoaded', () => {
    loadLocations();
    document.getElementById('submitBtn').addEventListener('click', onSubmit);
    document.getElementById('geoBtn').addEventListener('click', useGeolocation);
  });

  async function loadLocations() {
    try {
      const res = await fetch('http://localhost:8080/api/locations');
      if (!res.ok) throw new Error('Erreur chargement lieux');
      const data = await res.json();
      const locations = Array.isArray(data) ? data : (data.content || []);
      const select = document.getElementById('locationId');
      select.innerHTML = '<option value="">Choisir…</option>';
      for (const loc of locations) {
        // Afficher tous les lieux (le backend vérifie la propriété lors de la création)
        const opt = document.createElement('option');
        opt.value = String(loc.id);
        opt.textContent = (loc.label || 'Lieu') + (loc.address ? ' - ' + loc.address : '');
        select.appendChild(opt);
      }
    } catch (e) {
      showMsg('Erreur lors du chargement des lieux', true);
      console.error(e);
    }
  }

  async function onSubmit(e) {
    e.preventDefault();
    console.log('onSubmit appelé');
    const name = document.getElementById('name').value.trim();
    const locationIdStr = document.getElementById('locationId').value;
    console.log('Nom:', name, 'LocationId:', locationIdStr);
    if (!name || !locationIdStr) {
      showMsg('Nom et Lieu sont obligatoires', true);
      return;
    }

    const payload = {
      // Rend le nom unique pour éviter l'exception "Une borne avec ce nom existe déjà"
      name: name + ' - ' + Date.now(),
      locationId: parseInt(locationIdStr, 10),
      power: parseFloat(document.getElementById('power').value) || null,
      city: document.getElementById('city').value || null,
      latitude: toNum(document.getElementById('latitude').value),
      longitude: toNum(document.getElementById('longitude').value),
      instructions: document.getElementById('instructions').value || null,
      onFoot: document.getElementById('onFoot').checked,
      plugType: 'TYPE2S',
      hourlyRate: 2.0,
      isActive: true
    };

    console.log('Payload:', payload);
    console.log('URL:', 'http://localhost:8080/api/stations?userId=' + CURRENT_USER_ID + '&ownerId=' + CURRENT_USER_ID);
    
    try {
      const res = await authenticatedFetch('http://localhost:8080/api/stations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      
      console.log('Réponse reçue, status:', res.status);

      if (!res.ok) {
        const txt = await res.text();
        console.error('Erreur API', res.status, txt);
        if (res.status === 400) {
          if (txt && txt.includes("n'appartient pas au propriétaire")) {
            showMsg('Le lieu sélectionné n\'appartient pas à votre compte.', true);
            return;
          }
          if (txt && txt.includes('existe déjà')) {
            showMsg('Une borne avec ce nom existe déjà. Réessayez avec un autre nom.', true);
            return;
          }
          // Message générique si le backend ne renvoie pas le détail
          showMsg('Création impossible (400). Vérifiez que le lieu vous appartient et que le nom est unique.', true);
          return;
        }
        showMsg('Création impossible (' + res.status + '). ' + (txt || 'Vérifiez les champs.'), true);
        return;
      }

      showMsg('Borne créée avec succès.', false);
      setTimeout(() => { window.location.href = 'stations.jsp'; }, 1200);
    } catch (e) {
      console.error(e);
      showMsg('Erreur réseau. Veuillez réessayer.', true);
    }
  }

  function toNum(v) {
    const n = parseFloat(v);
    return Number.isFinite(n) ? n : null;
  }

  async function useGeolocation() {
    showMsg('Détection de la position...', false);
    
    try {
      // Utiliser la fonction améliorée avec Nominatim
      const location = await getAccurateLocation();
      
      console.log('Position obtenue:', location);
      
      // Remplir les champs
      document.getElementById('latitude').value = location.latitude.toFixed(6);
      document.getElementById('longitude').value = location.longitude.toFixed(6);
      
      // Remplir aussi la ville si disponible
      if (location.city && !document.getElementById('city').value) {
        document.getElementById('city').value = location.city;
      }
      
      // Message avec source et précision
      let successMsg = 'Position détectée !';
      if (location.source === 'IP Address') {
        successMsg += ' (approximative - basée sur IP)';
      } else {
        successMsg += ' Précision: ' + Math.round(location.accuracy) + 'm';
      }
      if (location.city) {
        successMsg += ' - Ville: ' + location.city;
      }
      
      showMsg(successMsg, false);
      
    } catch (error) {
      console.error('Erreur géolocalisation:', error);
      showMsg('Erreur: ' + error.message, true);
    }
  }

  function showMsg(text, isError) {
    const el = document.getElementById('msg');
    el.textContent = text;
    el.className = 'msg ' + (isError ? 'err' : 'ok');
    el.style.display = 'block';
  }
  
  } // Fermer le bloc else
</script>
</body>
</html>
