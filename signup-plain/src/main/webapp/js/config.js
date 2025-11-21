/**
 * Configuration globale pour l'application
 */

// Récupérer BACKEND_URL depuis la variable globale injectée par le serveur
// Si elle n'existe pas, utiliser la détection automatique
let API_BASE_URL;
if (typeof window.BACKEND_URL !== 'undefined' && window.BACKEND_URL) {
    API_BASE_URL = window.BACKEND_URL;
} else {
    // Fallback : détection automatique
    API_BASE_URL = (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
        ? 'http://localhost:8080'
        : 'https://electricity-business-backend-jvc9.onrender.com';
}

console.log('🔧 API Backend URL:', API_BASE_URL);











