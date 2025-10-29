-- Migration V10: Ajout de la colonne address_id à la table locations

-- Ajouter la colonne address_id à locations
ALTER TABLE locations 
ADD COLUMN address_id BIGINT REFERENCES addresses(id) ON DELETE SET NULL;

-- Créer un index pour optimiser les recherches
CREATE INDEX IF NOT EXISTS idx_locations_address_id ON locations(address_id);




