// Fonction de déconnexion
function logout() {
    console.log('=== DÉCONNEXION DÉMARRÉE ===');
    alert('Déconnexion en cours...');
    
    console.log('=== NETTOYAGE COMPLET ===');
    try {
        localStorage.removeItem('authToken');
        localStorage.removeItem('authUser');
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_user');
        localStorage.clear();
        sessionStorage.clear();
        console.log('Storages vidés avec succès');
        
        console.log('Redirection vers login...');
        window.location.replace('/login.jsp?message=logout');
    } catch (error) {
        console.error('Erreur lors de la déconnexion:', error);
        window.location.replace('/login.jsp?message=logout');
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
        console.error('Erreur lors de la récupération des infos utilisateur:', error);
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


















