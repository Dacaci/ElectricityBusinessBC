-- Migration V12: Ajouter la relation véhicule aux réservations

-- Ajouter la colonne vehicle_id à la table reservations
ALTER TABLE reservations 
ADD COLUMN IF NOT EXISTS vehicle_id BIGINT REFERENCES vehicles(id) ON DELETE SET NULL;

-- Créer un index pour optimiser les recherches par véhicule
CREATE INDEX IF NOT EXISTS idx_reservations_vehicle_id ON reservations(vehicle_id);

