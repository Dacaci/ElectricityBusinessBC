// Navigation component
function renderNavigation(activePage) {
    const pages = [
        { name: 'Tableau de bord', url: 'dashboard.jsp', id: 'dashboard' },
        { name: 'Ajouter un lieu', url: 'add-location.jsp', id: 'add-location' },
        { name: 'Mes lieux', url: 'locations.jsp', id: 'locations' },
        { name: 'Ajouter une borne', url: 'add-station.jsp', id: 'add-station' },
        { name: 'Mes bornes', url: 'stations.jsp', id: 'stations' },
        { name: 'Réserver', url: 'add-reservation.jsp', id: 'add-reservation' },
        { name: 'Mes réservations', url: 'reservations.jsp', id: 'reservations' },
        { name: 'Carte', url: 'map.jsp', id: 'map' }
    ];
    
    let html = '<nav class="navigation">';
    pages.forEach(page => {
        const activeClass = page.id === activePage ? ' active' : '';
        html += '<a href="' + page.url + '" class="nav-link' + activeClass + '">' + page.name + '</a>';
    });
    html += '</nav>';
    
    return html;
}

// Render header
function renderHeader() {
    const user = getAuthUser();
    const userName = user ? user.firstName + ' ' + user.lastName : 'Utilisateur';
    
    return '<div class="header">' +
        '<h1>Electricity Business</h1>' +
        '<div class="user-info">' +
            '<span>' + userName + '</span>' +
            '<a href="#" onclick="logout(); return false;">Déconnexion</a>' +
        '</div>' +
    '</div>';
}

// Initialize page
function initPage(pageName) {
    // Insert header
    document.body.insertAdjacentHTML('afterbegin', renderHeader());
    // Insert navigation after header
    const header = document.querySelector('.header');
    if (header) {
        header.insertAdjacentHTML('afterend', renderNavigation(pageName));
    }
}






























