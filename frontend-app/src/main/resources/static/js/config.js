/**
 * Configuration globale pour l'application
 * APPEL DIRECT AU BACKEND (sans proxy pour éviter 502)
 */

// Configuration de l'URL du backend - APPEL DIRECT
(function() {
    'use strict';
    
    // APPEL DIRECT AU BACKEND (CORS activé)
    if (typeof window.API_BASE_URL === 'undefined' || !window.API_BASE_URL) {
        window.API_BASE_URL = 'https://electricity-business-backend-jvc9.onrender.com';
        console.log('✅ API_BASE_URL défini (APPEL DIRECT):', window.API_BASE_URL);
    }
    
    // Créer aussi une constante globale pour compatibilité
    if (typeof API_BASE_URL === 'undefined' || !API_BASE_URL) {
        var API_BASE_URL = window.API_BASE_URL;
        window.API_BASE_URL = window.API_BASE_URL || 'https://electricity-business-backend-jvc9.onrender.com';
    }
    
    console.log('🔧 API Base URL (APPEL DIRECT AU BACKEND):', window.API_BASE_URL);
    console.log('ℹ️ Les requêtes API vont DIRECTEMENT au backend (plus de proxy)');
})();











