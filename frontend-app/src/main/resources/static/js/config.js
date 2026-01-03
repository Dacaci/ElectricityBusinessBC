/**
 * Configuration globale pour l'application
 * IMPORTANT: Ce fichier DOIT être chargé en premier pour définir API_BASE_URL
 */

// Configuration de l'URL du backend
// Le Frontend sert maintenant de proxy, donc on pointe vers le Frontend lui-même
// IMPORTANT: Toujours utiliser window.location.origin (frontend proxy), JAMAIS localhost:8080 directement
(function() {
    'use strict';
    
    // Définir API_BASE_URL de manière robuste
    if (typeof window.API_BASE_URL === 'undefined' || !window.API_BASE_URL) {
        window.API_BASE_URL = window.location.origin;
        console.log('✅ API_BASE_URL défini:', window.API_BASE_URL);
    }
    
    // Créer aussi une constante globale pour compatibilité (certains fichiers utilisent API_BASE_URL directement)
    if (typeof API_BASE_URL === 'undefined' || !API_BASE_URL) {
        var API_BASE_URL = window.API_BASE_URL;
        // Exposer aussi sur window pour être sûr
        window.API_BASE_URL = window.API_BASE_URL || window.location.origin;
    }
    
    // Vérification de sécurité : jamais de localhost:8080 hardcodé
    if (window.API_BASE_URL && window.API_BASE_URL.includes('localhost:8080')) {
        console.warn('⚠️ ATTENTION: API_BASE_URL pointe vers localhost:8080 (backend direct) au lieu du proxy frontend!');
        console.warn('   Correction automatique vers le proxy frontend...');
        window.API_BASE_URL = window.location.origin;
        if (typeof API_BASE_URL !== 'undefined') {
            API_BASE_URL = window.location.origin;
        }
    }
    
    console.log('🔧 API Base URL (Frontend proxy):', window.API_BASE_URL);
    console.log('ℹ️ Les requêtes API passent par le proxy frontend qui redirige vers le backend');
})();











