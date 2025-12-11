/**
 * Configuration globale pour l'application
 */

// Configuration de l'URL du backend
if (typeof window.API_BASE_URL === 'undefined') {
    if (typeof window.BACKEND_URL !== 'undefined' && window.BACKEND_URL) {
        window.API_BASE_URL = window.BACKEND_URL;
    } else {
        window.API_BASE_URL = (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
            ? 'http://localhost:8080'
            : 'https://electricity-business-backend-jvc9.onrender.com';
    }
}

console.log('🔧 API Backend URL:', window.API_BASE_URL);











