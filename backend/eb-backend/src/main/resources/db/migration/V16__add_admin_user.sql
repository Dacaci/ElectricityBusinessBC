-- Migration V16: Ajout d'un utilisateur admin pour les tests
-- Mot de passe: is pk ya du python  (même que test@example.com pour simplicité)

INSERT INTO users (first_name, last_name, email, phone, date_of_birth, address, postal_code, city, password_hash, status) 
VALUES (
    'Admin', 
    'System', 
    'admin123@gmail.com', 
    '0600000000', 
    '1985-01-01', 
    '1 Rue Admin', 
    '75000', 
    'Paris', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- test123
    'ACTIVE'
)
ON CONFLICT (email) DO UPDATE SET 
    password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    status = 'ACTIVE';

