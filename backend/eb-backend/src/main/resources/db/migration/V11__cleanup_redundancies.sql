-- Migration V11: Nettoyage des redondances du schéma

-- 1. Supprimer les coordonnées GPS dupliquées des STATIONS
--    (garder seulement celles de LOCATIONS)
ALTER TABLE stations 
DROP COLUMN IF EXISTS latitude,
DROP COLUMN IF EXISTS longitude,
DROP COLUMN IF EXISTS city;

-- 2. Supprimer l'ancien champ plug_type VARCHAR des STATIONS
--    (garder seulement la relation Many-to-Many avec plug_types)
ALTER TABLE stations
DROP COLUMN IF EXISTS plug_type;

-- 3. Supprimer le champ adresse texte de LOCATIONS
--    (garder seulement la relation avec la table addresses)
ALTER TABLE locations
DROP COLUMN IF EXISTS address;

-- Note: Les données des coordonnées GPS et adresses sont préservées
-- car elles existent déjà dans les tables LOCATIONS et ADDRESSES

