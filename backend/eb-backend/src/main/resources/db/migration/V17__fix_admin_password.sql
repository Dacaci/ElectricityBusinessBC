-- Migration V17: Fix admin user password hash
-- Delete and recreate admin user with correct BCrypt hash

DELETE FROM users WHERE email = 'admin123@gmail.com';

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
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ACTIVE'
);














