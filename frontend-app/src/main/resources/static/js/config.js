/**
 * Configuration globale pour l'application
 */

// Configuration de l'URL du backend
// Le Frontend sert maintenant de proxy, donc on pointe vers le Frontend lui-même
if (typeof window.API_BASE_URL === 'undefined') {
    // Utiliser l'URL du Frontend (même origine) pour les appels API
    // Le Frontend fera le proxy vers l'API Backend
    window.API_BASE_URL = window.location.origin;
}

console.log('🔧 API Backend URL:', window.API_BASE_URL);











