-- Script de migration pour ajouter les nouveaux champs à la table users

-- Ajouter les nouvelles colonnes
ALTER TABLE users ADD COLUMN IF NOT EXISTS first_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
ALTER TABLE users ADD COLUMN IF NOT EXISTS date_of_birth DATE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS address TEXT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS postal_code VARCHAR(10);
ALTER TABLE users ADD COLUMN IF NOT EXISTS city VARCHAR(100);

-- Mettre à jour les colonnes pour les rendre NOT NULL (après avoir ajouté des valeurs par défaut)
-- D'abord, ajouter des valeurs par défaut pour les utilisateurs existants
UPDATE users SET first_name = 'Utilisateur', last_name = 'Test' WHERE first_name IS NULL;

-- Maintenant rendre les colonnes NOT NULL
ALTER TABLE users ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE users ALTER COLUMN last_name SET NOT NULL;

-- Vérifier la structure de la table
\d users
