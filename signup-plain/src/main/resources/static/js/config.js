/**
 * Configuration globale pour l'application
 */

// Récupérer BACKEND_URL depuis la variable globale injectée par le serveur
// Si elle n'existe pas, utiliser la détection automatique
if (typeof window.API_BASE_URL === 'undefined') {
    if (typeof window.BACKEND_URL !== 'undefined' && window.BACKEND_URL) {
        window.API_BASE_URL = window.BACKEND_URL;
    } else {
        // Fallback : détection automatique
        window.API_BASE_URL = (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
            ? 'http://localhost:8080'
            : 'https://electricity-business-backend-jvc9.onrender.com';
    }
}

console.log('🔧 API Backend URL:', window.API_BASE_URL);











