-- Migration V20: Suppression de la table addresses et nettoyage des relations

-- Supprimer la colonne address_id de la table locations
ALTER TABLE locations DROP COLUMN IF EXISTS address_id;

-- Supprimer la table addresses
DROP TABLE IF EXISTS addresses CASCADE;

-- Note: Les attributs address, postal_code et city restent dans la table users
-- Note: Les attributs latitude et longitude restent dans la table locations




