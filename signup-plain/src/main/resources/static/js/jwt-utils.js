/**
 * Utilitaires pour la gestion des tokens JWT avec cookies HTTPOnly
 * 
 * SÉCURITÉ : Le token JWT est maintenant stocké dans un cookie HTTPOnly côté serveur.
 * JavaScript ne peut pas accéder au token (protection contre XSS).
 * Seules les informations utilisateur sont stockées dans localStorage.
 */

// Protection contre le double chargement
if (typeof window.JWT_UTILS_LOADED === 'undefined') {
    window.JWT_UTILS_LOADED = true;

// Clé pour stocker les informations utilisateur dans localStorage (pas le token !)
// Utiliser window pour éviter les conflits de déclaration
if (typeof window.JWT_USER_KEY === 'undefined') {
    window.JWT_USER_KEY = 'authUser';
}

/**
 * Sauvegarde les informations d'authentification
 * NOTE: Le token JWT est maintenant dans un cookie HttpOnly côté serveur
 * JavaScript ne peut pas y accéder (sécurité XSS)
 * @param {string} token - Le token JWT (ignoré, stocké dans cookie HttpOnly)
 * @param {object} user - Les informations utilisateur (stockées dans localStorage)
 */
function saveAuthData(token, user) {
    // Le token est dans un cookie HttpOnly, on ne le stocke plus dans localStorage
    // Seules les infos utilisateur sont stockées
    if (user) {
        localStorage.setItem(window.JWT_USER_KEY, JSON.stringify(user));
    }
}

/**
 * Récupère le token JWT
 * NOTE: Le token est maintenant dans un cookie HttpOnly, inaccessible à JavaScript
 * Cette fonction retourne toujours null car le token est géré côté serveur
 * @returns {string|null} Toujours null (token dans cookie HttpOnly)
 */
function getAuthToken() {
    // Le token est dans un cookie HttpOnly, JavaScript ne peut pas y accéder
    // Le serveur récupère automatiquement le token depuis les cookies
    return null;
}

/**
 * Récupère les informations utilisateur depuis le localStorage
 * @returns {object|null} Les informations utilisateur ou null
 */
function getAuthUser() {
    const userStr = localStorage.getItem(window.JWT_USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
}

/**
 * Supprime les données d'authentification
 * Le cookie HTTPOnly sera supprimé lors de l'appel à l'API /logout
 */
function clearAuthData() {
    // Supprimer les infos utilisateur du localStorage
    localStorage.removeItem(window.JWT_USER_KEY);
    
    // Nettoyer aussi les anciennes clés au cas où
    localStorage.removeItem('authToken');
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
}

/**
 * Force la déconnexion complète
 */
async function forceLogout() {
    // Supprimer les données locales
    clearAuthData();
    
    // Vider complètement le localStorage
    localStorage.clear();
    
    // Vider aussi sessionStorage
    sessionStorage.clear();
    
    // Redirection vers la carte (accessible sans authentification)
    setTimeout(() => {
        window.location.href = '/map';
    }, 100);
}

/**
 * Vérifie si l'utilisateur est connecté
 * @returns {boolean} true si connecté, false sinon
 */
function isAuthenticated() {
    const token = getAuthToken();
    const user = getAuthUser();
    // Vérifier que le token existe, n'est pas vide, et que l'utilisateur existe avec un ID valide
    return token !== null && token !== '' && token !== undefined &&
           user !== null && user !== undefined && user.id !== null && user.id !== undefined;
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
 * Crée les headers pour les requêtes API
 * NOTE: Le token JWT est dans un cookie HttpOnly, il est envoyé automatiquement
 * Pas besoin d'ajouter le header Authorization
 * @returns {object} Les headers (sans Authorization, le cookie est envoyé automatiquement)
 */
function getAuthHeaders() {
    // Le token est dans un cookie HttpOnly, il est envoyé automatiquement avec credentials: 'include'
    // Pas besoin d'ajouter le header Authorization
    return {
        'Content-Type': 'application/json'
    };
}

/**
 * Effectue une requête authentifiée avec le token JWT dans un cookie HttpOnly
 * @param {string} url - L'URL de la requête
 * @param {object} options - Les options de la requête
 * @returns {Promise<Response>} La réponse de la requête
 */
async function authenticatedFetch(url, options = {}) {
    const headers = {
        ...getAuthHeaders(),
        ...options.headers
    };
    
    // IMPORTANT: credentials: 'include' est OBLIGATOIRE pour envoyer les cookies HttpOnly
    const response = await fetch(url, {
        ...options,
        headers,
        credentials: 'include' // OBLIGATOIRE pour les cookies HTTPOnly
    });
    
    // Si la réponse est 401 (Unauthorized), déconnecter l'utilisateur
    if (response.status === 401) {
        clearAuthData();
        // Appeler l'API de logout pour supprimer le cookie côté serveur
        try {
            await fetch(window.API_BASE_URL + '/api/auth/logout', {
                method: 'POST',
                credentials: 'include'
            });
        } catch (e) {
            // Ignorer les erreurs de logout
        }
        window.location.href = '/login';
        return response;
    }
    
    return response;
}

// Exposer authenticatedFetch globalement pour être sûr qu'il est accessible
window.authenticatedFetch = authenticatedFetch;

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

} // Fin de la protection contre double chargement
