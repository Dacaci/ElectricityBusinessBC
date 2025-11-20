/**
 * Utilitaires pour la géolocalisation avec Nominatim (OpenStreetMap)
 * Documentation: https://nominatim.org/release-docs/latest/api/Search/
 */

// Configuration Nominatim
const NOMINATIM_BASE_URL = 'https://nominatim.openstreetmap.org';
const USER_AGENT = 'ElectricityBusiness/1.0'; // Requis par Nominatim

/**
 * Recherche une adresse et retourne les coordonnées
 * @param {string} address - L'adresse à rechercher
 * @returns {Promise<Object>} - Les coordonnées et informations
 */
async function geocodeAddress(address) {
        try {
        const url = NOMINATIM_BASE_URL + '/search?' + new URLSearchParams({
            q: address,
            format: 'json',
            addressdetails: 1,
            limit: 5
        });
        
        const response = await fetch(url, {
            headers: {
                'User-Agent': USER_AGENT
            }
        });
        
        if (!response.ok) {
            throw new Error('Erreur lors de la recherche d\'adresse');
        }
        
        const results = await response.json();
        
        if (!results || results.length === 0) {
            throw new Error('Aucune adresse trouvée');
        }
        
        // Retourner tous les résultats pour permettre à l'utilisateur de choisir
        return results.map(result => ({
            displayName: result.display_name,
            latitude: parseFloat(result.lat),
            longitude: parseFloat(result.lon),
            address: result.address || {},
            city: result.address.city || result.address.town || result.address.village || '',
            postalCode: result.address.postcode || '',
            country: result.address.country || '',
            importance: result.importance // Score de pertinence (0-1)
        }));
        
    } catch (error) {
                throw error;
    }
}

/**
 * Reverse geocoding : Convertir des coordonnées en adresse
 * @param {number} latitude 
 * @param {number} longitude 
 * @returns {Promise<Object>} - L'adresse
 */
async function reverseGeocode(latitude, longitude) {
        try {
        const url = NOMINATIM_BASE_URL + '/reverse?' + new URLSearchParams({
            lat: latitude,
            lon: longitude,
            format: 'json',
            addressdetails: 1
        });
        
        const response = await fetch(url, {
            headers: {
                'User-Agent': USER_AGENT
            }
        });
        
        if (!response.ok) {
            throw new Error('Erreur lors du reverse geocoding');
        }
        
        const result = await response.json();
        
        return {
            displayName: result.display_name,
            address: result.address.road || result.address.pedestrian || '',
            city: result.address.city || result.address.town || result.address.village || '',
            postalCode: result.address.postcode || '',
            country: result.address.country || '',
            fullAddress: result.address
        };
        
    } catch (error) {
                throw error;
    }
}

/**
 * Obtenir la position précise de l'utilisateur avec fallback IP
 * @returns {Promise<Object>} - Position et informations
 */
async function getAccurateLocation() {
        // 1. Essayer d'abord le GPS/Wi-Fi du navigateur
    try {
        const position = await new Promise((resolve, reject) => {
            if (!navigator.geolocation) {
                reject(new Error('Géolocalisation non supportée'));
                return;
            }
            
            navigator.geolocation.getCurrentPosition(
                resolve,
                reject,
                {
                    enableHighAccuracy: true,
                    timeout: 10000,
                    maximumAge: 0
                }
            );
        });
        
        const location = {
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            accuracy: position.coords.accuracy,
            source: 'GPS/Wi-Fi',
            timestamp: new Date(position.timestamp)
        };
        
                // Obtenir l'adresse correspondante
        try {
            const addressInfo = await reverseGeocode(location.latitude, location.longitude);
            location.address = addressInfo.address;
            location.city = addressInfo.city;
            location.postalCode = addressInfo.postalCode;
            location.displayName = addressInfo.displayName;
        } catch (e) {
                    }
        
        return location;
        
    } catch (gpsError) {
                // 2. Fallback : Géolocalisation par IP (moins précis mais fonctionnel)
        try {
            // Utiliser un service gratuit de géolocalisation IP
            const ipResponse = await fetch('https://ipapi.co/json/');
            const ipData = await ipResponse.json();
            
            const location = {
                latitude: ipData.latitude,
                longitude: ipData.longitude,
                accuracy: 5000, // ~5km de précision
                source: 'IP Address',
                city: ipData.city,
                postalCode: ipData.postal,
                country: ipData.country_name,
                displayName: ipData.city + ', ' + ipData.country_name,
                warning: 'Position approximative basée sur votre adresse IP'
            };
            
                        return location;
            
        } catch (ipError) {
                        throw new Error('Impossible d\'obtenir votre position. Veuillez saisir les coordonnées manuellement.');
        }
    }
}

/**
 * Recherche d'adresse avec suggestions (pour autocomplétion)
 * @param {string} query - La recherche partielle
 * @param {string} countryCode - Code pays (ex: 'fr')
 * @returns {Promise<Array>} - Liste de suggestions
 */
async function searchAddressSuggestions(query, countryCode = 'fr') {
    if (!query || query.length < 3) {
        return [];
    }
    
        try {
        const url = NOMINATIM_BASE_URL + '/search?' + new URLSearchParams({
            q: query,
            format: 'json',
            addressdetails: 1,
            limit: 10,
            countrycodes: countryCode
        });
        
        const response = await fetch(url, {
            headers: {
                'User-Agent': USER_AGENT
            }
        });
        
        if (!response.ok) {
            throw new Error('Erreur lors de la recherche');
        }
        
        const results = await response.json();
        
        return results.map(result => ({
            label: result.display_name,
            latitude: parseFloat(result.lat),
            longitude: parseFloat(result.lon),
            address: result.address.road || result.address.pedestrian || '',
            city: result.address.city || result.address.town || result.address.village || '',
            postalCode: result.address.postcode || '',
            importance: result.importance
        }));
        
    } catch (error) {
                return [];
    }
}

/**
 * Calculer la distance entre deux points (en km)
 * Utilise la formule de Haversine
 * @param {number} lat1 
 * @param {number} lon1 
 * @param {number} lat2 
 * @param {number} lon2 
 * @returns {number} - Distance en kilomètres
 */
function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // Rayon de la Terre en km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = 
        Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
        Math.sin(dLon/2) * Math.sin(dLon/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    const distance = R * c;
    return distance;
}

/**
 * Formater une distance pour l'affichage
 * @param {number} km - Distance en kilomètres
 * @returns {string} - Distance formatée
 */
function formatDistance(km) {
    if (km < 1) {
        return Math.round(km * 1000) + ' m';
    } else if (km < 10) {
        return km.toFixed(1) + ' km';
    } else {
        return Math.round(km) + ' km';
    }
}








