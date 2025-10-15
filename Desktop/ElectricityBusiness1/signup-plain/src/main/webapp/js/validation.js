// Fonctions de validation réutilisables pour Electricity Business

/**
 * Valide un email
 */
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
}

/**
 * Valide un numéro de téléphone français
 */
function validatePhone(phone) {
    const re = /^(?:(?:\+|00)33|0)\s*[1-9](?:[\s.-]*\d{2}){4}$/;
    return re.test(phone.replace(/\s/g, ''));
}

/**
 * Valide un code postal français
 */
function validatePostalCode(postalCode) {
    const re = /^[0-9]{5}$/;
    return re.test(postalCode);
}

/**
 * Valide une date de naissance (doit avoir au moins 18 ans)
 */
function validateAge(dateOfBirth) {
    const today = new Date();
    const birthDate = new Date(dateOfBirth);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
    }
    
    return age >= 18;
}

/**
 * Valide un mot de passe (min 8 caractères, 1 majuscule, 1 minuscule, 1 chiffre)
 */
function validatePassword(password) {
    if (password.length < 8) return false;
    if (!/[A-Z]/.test(password)) return false;
    if (!/[a-z]/.test(password)) return false;
    if (!/[0-9]/.test(password)) return false;
    return true;
}

/**
 * Valide que deux champs sont identiques
 */
function validateMatch(value1, value2) {
    return value1 === value2;
}

/**
 * Valide qu'un champ n'est pas vide
 */
function validateRequired(value) {
    return value !== null && value !== undefined && value.trim() !== '';
}

/**
 * Valide un nombre dans une plage
 */
function validateRange(value, min, max) {
    const num = parseFloat(value);
    return !isNaN(num) && num >= min && num <= max;
}

/**
 * Valide une date (doit être dans le futur)
 */
function validateFutureDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    return date > now;
}

/**
 * Valide qu'une date de fin est après une date de début
 */
function validateDateRange(startDate, endDate) {
    const start = new Date(startDate);
    const end = new Date(endDate);
    return end > start;
}

/**
 * Affiche une erreur sur un champ
 */
function showFieldError(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (!field) return;
    
    field.classList.add('error');
    
    // Cherche ou crée le message d'erreur
    let errorMsg = field.parentElement.querySelector('.error-message');
    if (!errorMsg) {
        errorMsg = document.createElement('div');
        errorMsg.className = 'error-message';
        field.parentElement.appendChild(errorMsg);
    }
    errorMsg.textContent = message;
    errorMsg.style.display = 'block';
}

/**
 * Supprime l'erreur d'un champ
 */
function clearFieldError(fieldId) {
    const field = document.getElementById(fieldId);
    if (!field) return;
    
    field.classList.remove('error');
    
    const errorMsg = field.parentElement.querySelector('.error-message');
    if (errorMsg) {
        errorMsg.style.display = 'none';
    }
}

/**
 * Affiche un message d'alerte
 */
function showAlert(message, type = 'info', containerId = 'messageContainer') {
    const container = document.getElementById(containerId);
    if (!container) return;
    
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.innerHTML = `
        ${message}
        <button class="close-btn" onclick="this.parentElement.remove()">×</button>
    `;
    
    container.appendChild(alert);
    
    // Auto-suppression après 5 secondes
    setTimeout(() => {
        if (alert.parentElement) {
            alert.remove();
        }
    }, 5000);
}

/**
 * Valide un formulaire complet
 */
function validateForm(formId, validators) {
    let isValid = true;
    
    for (const [fieldId, rules] of Object.entries(validators)) {
        const field = document.getElementById(fieldId);
        if (!field) continue;
        
        const value = field.value;
        
        // Efface les erreurs précédentes
        clearFieldError(fieldId);
        
        // Applique chaque règle
        for (const rule of rules) {
            if (!rule.validate(value, field)) {
                showFieldError(fieldId, rule.message);
                isValid = false;
                break; // Affiche seulement la première erreur
            }
        }
    }
    
    return isValid;
}

/**
 * Ajoute une validation en temps réel sur un champ
 */
function addRealtimeValidation(fieldId, validator, errorMessage) {
    const field = document.getElementById(fieldId);
    if (!field) return;
    
    field.addEventListener('blur', function() {
        if (!validator(this.value)) {
            showFieldError(fieldId, errorMessage);
        } else {
            clearFieldError(fieldId);
        }
    });
    
    field.addEventListener('input', function() {
        if (this.classList.contains('error') && validator(this.value)) {
            clearFieldError(fieldId);
        }
    });
}

/**
 * Désactive un bouton pendant une opération asynchrone
 */
function withLoadingButton(buttonId, asyncFunction) {
    return async function(...args) {
        const button = document.getElementById(buttonId);
        if (!button) return;
        
        const originalText = button.innerHTML;
        button.disabled = true;
        button.innerHTML = '<span class="spinner"></span> Chargement...';
        
        try {
            const result = await asyncFunction(...args);
            return result;
        } finally {
            button.disabled = false;
            button.innerHTML = originalText;
        }
    };
}

/**
 * Confirme une action avant de l'exécuter
 */
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

/**
 * Formate une date en français
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

/**
 * Formate une date et heure en français
 */
function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('fr-FR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Débounce une fonction (pour éviter les appels multiples)
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Gère les erreurs d'API de manière uniforme
 */
async function handleApiError(error, defaultMessage = 'Une erreur est survenue') {
    let message = defaultMessage;
    
    if (error.response) {
        // L'API a répondu avec un code d'erreur
        try {
            const data = await error.response.json();
            message = data.message || data.error || defaultMessage;
        } catch (e) {
            message = `Erreur ${error.response.status}: ${error.response.statusText}`;
        }
    } else if (error.message) {
        message = error.message;
    }
    
    showAlert(message, 'error');
    return message;
}

/**
 * Effectue une requête API avec gestion d'erreur
 */
async function apiRequest(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        // Vérifie si la réponse contient du JSON
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        
        return response;
    } catch (error) {
        await handleApiError(error);
        throw error;
    }
}








