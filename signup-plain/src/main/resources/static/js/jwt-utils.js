/**
 * Utilitaires pour la gestion des tokens JWT avec cookies HTTPOnly
 * 
 * SÉCURITÉ : Le token JWT est maintenant stocké dans un cookie HTTPOnly côté serveur.
 * JavaScript ne peut pas accéder au token (protection contre XSS).
 * Seules les informations utilisateur sont stockées dans localStorage.
 */

// Clé pour stocker les informations utilisateur dans localStorage (pas le token !)
const JWT_USER_KEY = 'authUser';

/**
 * Sauvegarde les informations utilisateur (le token est dans le cookie HTTPOnly)
 * @param {object} user - Les informations utilisateur
 */
function saveAuthData(user) {
    // Le token JWT est automatiquement stocké dans un cookie HTTPOnly par le serveur
    localStorage.setItem(JWT_USER_KEY, JSON.stringify(user));
}

/**
 * Récupère le token JWT depuis le cookie HTTPOnly
 * NOTE: Cette fonction retourne null car JavaScript ne peut pas accéder aux cookies HTTPOnly
 * Le token est automatiquement envoyé avec chaque requête HTTP grâce au proxy
 * @returns {string|null} Toujours null (le token est dans un cookie HTTPOnly)
 */
function getAuthToken() {
    // Le token est dans un cookie HTTPOnly, JavaScript ne peut pas y accéder
    return null;
}

/**
 * Récupère les informations utilisateur depuis le localStorage
 * @returns {object|null} Les informations utilisateur ou null
 */
function getAuthUser() {
    const userStr = localStorage.getItem(JWT_USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
}

/**
 * Supprime les données d'authentification
 * Le cookie HTTPOnly sera supprimé lors de l'appel à l'API /logout
 */
function clearAuthData() {
    // Supprimer les infos utilisateur du localStorage
    localStorage.removeItem(JWT_USER_KEY);
    
    // Nettoyer aussi les anciennes clés au cas où
    localStorage.removeItem('authToken');
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
}

/**
 * Force la déconnexion complète
 * Appelle l'API /logout pour supprimer le cookie HTTPOnly
 */
async function forceLogout() {
    try {
        // Récupérer l'URL du backend depuis config.js
        const backendUrl = window.API_BASE_URL || 'http://localhost:8080';
        
        // Appeler l'API de déconnexion pour supprimer le cookie HTTPOnly
        await fetch(backendUrl + '/api/auth/logout', {
            method: 'POST',
            credentials: 'include'  // Inclure les cookies dans la requête
        });
    } catch (error) {
        console.error('Erreur lors de la déconnexion:', error);
    }
    
    // Supprimer les données locales
    clearAuthData();
    
    // Vider complètement le localStorage
    localStorage.clear();
    
    // Vider aussi sessionStorage
    sessionStorage.clear();
    
    // Redirection
    setTimeout(() => {
        window.location.href = '/login?message=logout';
    }, 100);
}

/**
 * Vérifie si l'utilisateur est connecté
 * @returns {boolean} true si connecté, false sinon
 */
function isAuthenticated() {
    const user = getAuthUser();
    // Avec le proxy et cookies HTTPOnly, on vérifie juste les infos utilisateur
    return user !== null;
}

/**
 * Récupère l'ID de l'utilisateur connecté
 * @returns {number|null} L'ID de l'utilisateur ou null
 */
function getCurrentUserId() {
    const user = getAuthUser();
    return user ? user.id : null;
}

/**
 * Récupère l'email de l'utilisateur connecté
 * @returns {string|null} L'email de l'utilisateur ou null
 */
function getCurrentUserEmail() {
    const user = getAuthUser();
    return user ? user.email : null;
}

/**
 * Récupère le nom complet de l'utilisateur connecté
 * @returns {string|null} Le nom complet ou null
 */
function getCurrentUserName() {
    const user = getAuthUser();
    return user ? (user.firstName + ' ' + user.lastName) : null;
}

/**
 * Crée les headers pour les requêtes API (sans Authorization car le token est dans le cookie)
 * @returns {object} Les headers basiques
 */
function getAuthHeaders() {
    // Le token JWT est automatiquement envoyé via le cookie HTTPOnly grâce au proxy
    return {
        'Content-Type': 'application/json'
    };
}

/**
 * Effectue une requête authentifiée
 * Le token JWT est automatiquement envoyé via le cookie HTTPOnly grâce à credentials: 'include'
 * @param {string} url - L'URL de la requête
 * @param {object} options - Les options de la requête
 * @returns {Promise<Response>} La réponse de la requête
 */
async function authenticatedFetch(url, options = {}) {
    const headers = {
        ...getAuthHeaders(),
        ...options.headers
    };
    
    const response = await fetch(url, {
        ...options,
        headers,
        credentials: 'include'  // IMPORTANT: Inclure les cookies dans la requête
    });
    
    // Si la réponse est 401 (Unauthorized), déconnecter l'utilisateur
    if (response.status === 401) {
        clearAuthData();
        window.location.href = '/login';
        return response;
    }
    
    return response;
}

/**
 * Redirige vers la page de connexion si l'utilisateur n'est pas authentifié
 */
function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = '/login';
        return false;
    }
    return true;
}

/**
 * NOTE: Les fonctions de vérification du token côté client ont été supprimées
 * car le token est maintenant dans un cookie HTTPOnly (inaccessible à JavaScript).
 * 
 * La vérification de l'expiration du token est maintenant gérée côté serveur.
 * Si le token expire, le serveur retournera une erreur 401 et l'utilisateur
 * sera automatiquement déconnecté par authenticatedFetch().
 */
