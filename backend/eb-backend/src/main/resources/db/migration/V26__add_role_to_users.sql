-- Migration V26: Ajout du champ role pour gérer les permissions (USER, ADMIN)
-- Système de rôles minimal pour sécuriser les endpoints administratifs

-- Ajouter la colonne role avec valeur par défaut USER
ALTER TABLE users 
ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';

-- Créer un index sur le role pour les requêtes fréquentes
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Ajouter une contrainte pour valider les valeurs de role
ALTER TABLE users ADD CONSTRAINT chk_user_role 
CHECK (role IN ('USER', 'ADMIN'));

-- Mettre à jour l'utilisateur admin existant avec le rôle ADMIN
UPDATE users 
SET role = 'ADMIN' 
WHERE email = 'admin123@gmail.com';

-- Mettre à jour tous les autres utilisateurs existants en USER (par sécurité)
UPDATE users 
SET role = 'USER' 
WHERE role IS NULL OR role = '';

-- Commentaire pour documentation
COMMENT ON COLUMN users.role IS 'Rôle de l''utilisateur : USER (utilisateur standard) ou ADMIN (administrateur avec permissions étendues)';

