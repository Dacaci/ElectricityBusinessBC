/**
 * Utilitaires pour la gestion des tokens JWT
 */

// Clé pour stocker le token dans localStorage
const JWT_TOKEN_KEY = 'authToken';
const JWT_USER_KEY = 'authUser';

/**
 * Sauvegarde le token JWT et les informations utilisateur
 * @param {string} token - Le token JWT
 * @param {object} user - Les informations utilisateur
 */
function saveAuthData(token, user) {
    localStorage.setItem(JWT_TOKEN_KEY, token);
    localStorage.setItem(JWT_USER_KEY, JSON.stringify(user));
}

/**
 * Récupère le token JWT depuis le localStorage
 * @returns {string|null} Le token JWT ou null
 */
function getAuthToken() {
    return localStorage.getItem(JWT_TOKEN_KEY);
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
 */
function clearAuthData() {
    localStorage.removeItem(JWT_TOKEN_KEY);
    localStorage.removeItem(JWT_USER_KEY);
    
    // Nettoyer aussi les anciennes clés au cas où
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
}

/**
 * Force la déconnexion complète
 */
function forceLogout() {
            clearAuthData();
    
    // Vider complètement le localStorage
    localStorage.clear();
    
    // Vider aussi sessionStorage
    sessionStorage.clear();
    
            // Attendre un peu avant la redirection
    setTimeout(() => {
                window.location.href = '/login.jsp?message=logout';
    }, 100);
}

/**
 * Vérifie si l'utilisateur est connecté
 * @returns {boolean} true si connecté, false sinon
 */
function isAuthenticated() {
    const token = getAuthToken();
    const user = getAuthUser();
    
    // Vérifier que le token et l'utilisateur existent
    if (!token || !user) {
                return false;
    }
    
    // Vérifier la validité du token directement
    if (isTokenExpired(token)) {
                clearAuthData();
        return false;
    }
    
        return true;
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
 * Crée les headers d'autorisation pour les requêtes API
 * @returns {object} Les headers avec le token JWT
 */
function getAuthHeaders() {
    const token = getAuthToken();
    return {
        'Authorization': token ? `Bearer ${token}` : '',
        'Content-Type': 'application/json'
    };
}

/**
 * Effectue une requête authentifiée
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
        headers
    });
    
    // Si la réponse est 401 (Unauthorized), déconnecter l'utilisateur
    if (response.status === 401) {
        clearAuthData();
        window.location.href = '/login.jsp';
        return response;
    }
    
    return response;
}

/**
 * Redirige vers la page de connexion si l'utilisateur n'est pas authentifié
 */
function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = '/login.jsp';
        return false;
    }
    return true;
}

/**
 * Décode le token JWT (pour obtenir les informations sans faire de requête)
 * @param {string} token - Le token JWT
 * @returns {object|null} Le payload décodé ou null
 */
function decodeJwtPayload(token) {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) {
            return null;
        }
        
        const payload = parts[1];
        const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
        return JSON.parse(decoded);
    } catch (error) {
                return null;
    }
}

/**
 * Vérifie si le token JWT est expiré
 * @param {string} token - Le token JWT
 * @returns {boolean} true si expiré, false sinon
 */
function isTokenExpired(token) {
    const payload = decodeJwtPayload(token);
    if (!payload || !payload.exp) {
        return true;
    }
    
    const currentTime = Math.floor(Date.now() / 1000);
    return payload.exp < currentTime;
}

/**
 * Vérifie et nettoie les données d'authentification si nécessaire
 */
function validateAuthData() {
    const token = getAuthToken();
    const user = getAuthUser();
    
    // Vérifier que le token et l'utilisateur existent
    if (!token || !user) {
                return false;
    }
    
    // Vérifier si le token est expiré
    if (isTokenExpired(token)) {
                clearAuthData();
        return false;
    }
    
        return true;
}

/**
 * Démarre la surveillance automatique de l'expiration du token
 * Vérifie toutes les minutes si le token est expiré
 */
function startTokenExpirationMonitoring() {
    setInterval(function() {
        const token = getAuthToken();
        if (token && isTokenExpired(token)) {
            alert('Votre session a expiré. Vous allez être déconnecté.');
            forceLogout();
        }
    }, 60000); // Vérifier toutes les minutes
}

/**
 * Démarre automatiquement la surveillance de l'expiration du token quand le script est chargé
 * Si on est sur une page protégée (pas login ou register), on démarre la surveillance
 */
(function() {
    const currentPath = window.location.pathname;
    const publicPages = ['/login.jsp', '/register.jsp', '/verify-success.jsp'];
    const isPublicPage = publicPages.some(page => currentPath.includes(page));
    
    if (!isPublicPage) {
        // Vérifier si un token existe avant de démarrer la surveillance
        const token = getAuthToken();
        if (token) {
            startTokenExpirationMonitoring();
        }
    }
})();
