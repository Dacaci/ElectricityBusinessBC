/**
 * Configuration globale pour l'application
 */

// Détection automatique du backend
const API_BASE_URL = (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
    ? 'http://localhost:8080'
    : 'https://electricity-business-backend-z373.onrender.com';

console.log('🔧 API Backend URL:', API_BASE_URL);











