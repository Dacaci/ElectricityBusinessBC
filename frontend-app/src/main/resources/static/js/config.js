/**
 * Configuration globale pour l'application
 * COMPATIBILITÉ : Ne redéfinit pas API_BASE_URL si déjà défini par backend-config.html
 */

// Configuration de l'URL du backend - FALLBACK uniquement
(function() {
    'use strict';
    
    // Si API_BASE_URL est déjà défini par backend-config.html, ne pas le modifier
    if (typeof window.API_BASE_URL !== 'undefined' && window.API_BASE_URL) {
        // API_BASE_URL déjà défini par backend-config.html, on le respecte
        if (typeof API_BASE_URL === 'undefined') {
            var API_BASE_URL = window.API_BASE_URL;
        }
        // Ne pas logger pour éviter les doublons (déjà loggé par backend-config.html)
        return;
    }
    
    // FALLBACK : Si backend-config.html n'est pas inclus, utiliser le proxy frontend
    window.API_BASE_URL = window.location.origin;
    if (typeof API_BASE_URL === 'undefined') {
        var API_BASE_URL = window.API_BASE_URL;
    }
    
    console.log('🔧 API Base URL (FALLBACK - PROXY FRONTEND):', window.API_BASE_URL);
    console.log('ℹ️ Les requêtes API passent par le proxy frontend qui redirige vers le backend');
    console.log('ℹ️ Les cookies JWT sont forwardés automatiquement par le proxy');
})();











