/**
 * Configuration globale pour l'application
 * UTILISE LE PROXY FRONTEND pour forwarder les cookies JWT
 */

// Configuration de l'URL du backend - PROXY FRONTEND
(function() {
    'use strict';
    
    // Utiliser le proxy frontend (même domaine = cookies fonctionnent)
    if (typeof window.API_BASE_URL === 'undefined' || !window.API_BASE_URL) {
        window.API_BASE_URL = window.location.origin;
        console.log('✅ API_BASE_URL défini (PROXY FRONTEND):', window.API_BASE_URL);
    }
    
    // Créer aussi une constante globale pour compatibilité
    if (typeof API_BASE_URL === 'undefined' || !API_BASE_URL) {
        var API_BASE_URL = window.API_BASE_URL;
        window.API_BASE_URL = window.API_BASE_URL || window.location.origin;
    }
    
    console.log('🔧 API Base URL (PROXY FRONTEND):', window.API_BASE_URL);
    console.log('ℹ️ Les requêtes API passent par le proxy frontend qui redirige vers le backend');
    console.log('ℹ️ Les cookies JWT sont forwardés automatiquement par le proxy');
})();











