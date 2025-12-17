// Fonction de déconnexion
async function logout() {
    try {
        // Appeler l'API de logout pour supprimer le cookie HttpOnly côté serveur
        try {
            await fetch((window.API_BASE_URL || '') + '/api/auth/logout', {
                method: 'POST',
                credentials: 'include' // IMPORTANT: pour envoyer le cookie à supprimer
            });
        } catch (e) {
            console.warn('Erreur lors de l\'appel API logout:', e);
        }
        
        // Nettoyer le localStorage (infos utilisateur)
        if (typeof clearAuthData === 'function') {
            clearAuthData();
        } else {
            localStorage.removeItem('authToken');
            localStorage.removeItem('authUser');
            localStorage.removeItem('auth_token');
            localStorage.removeItem('auth_user');
        }
        
        localStorage.clear();
        sessionStorage.clear();
        
        // Rediriger vers /login
        window.location.replace('/login');
    } catch (error) {
        console.error('Erreur lors de la déconnexion:', error);
        window.location.replace('/login');
    }
}

// Fonction pour récupérer les infos utilisateur
function getCurrentUserInfo() {
    try {
        const authUser = localStorage.getItem('authUser');
        if (authUser) {
            const user = JSON.parse(authUser);
            return user;
        }
    } catch (error) {
            }
    return null;
}

// Fonction pour afficher le message de bienvenue
function displayWelcomeMessage() {
    const welcomeElement = document.getElementById('welcomeMessage');
    if (welcomeElement) {
        const user = getCurrentUserInfo();
        if (user && user.username) {
            welcomeElement.textContent = 'Bienvenue ' + user.username;
        }
    }
}

// Initialiser au chargement de la page
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', displayWelcomeMessage);
} else {
    displayWelcomeMessage();
}

































