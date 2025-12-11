/**
 * Configuration globale pour l'application
 */

// En production, utiliser le proxy /api du frontend (même domaine = cookies HTTPOnly OK !)
// En local, pointer directement vers le backend sur localhost:8080
if (typeof window.API_BASE_URL === 'undefined') {
    window.API_BASE_URL = (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
        ? 'http://localhost:8080'
        : '';  // URL relative = même domaine que le frontend
}

console.log('🔧 API Backend URL:', window.API_BASE_URL || 'SAME DOMAIN (proxy)');











