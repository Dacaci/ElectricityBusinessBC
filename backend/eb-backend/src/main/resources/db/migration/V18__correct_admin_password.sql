-- Migration V18: Correct admin password with proper BCrypt hash
-- Password: test123 (correctly hashed this time)

UPDATE users 
SET password_hash = '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIvApYrZ0u'
WHERE email = 'admin123@gmail.com';














