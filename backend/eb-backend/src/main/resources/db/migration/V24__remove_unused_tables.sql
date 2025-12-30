-- Migration V24: Suppression des tables inutilisées (vehicles, plug_types et tables de jonction)
-- 
-- Ces tables sont des vestiges d'une ancienne conception qui a été simplifiée :
-- - plug_types et station_plug_type : remplacés par la colonne plug_type dans stations (V22)
-- - vehicles, user_vehicle, vehicle_plug_compatibility : jamais utilisés dans le code

-- Supprimer les tables de jonction d'abord (dépendances)
DROP TABLE IF EXISTS vehicle_plug_compatibility CASCADE;
DROP TABLE IF EXISTS station_plug_type CASCADE;
DROP TABLE IF EXISTS user_vehicle CASCADE;

-- Supprimer les tables principales
DROP TABLE IF EXISTS vehicles CASCADE;
DROP TABLE IF EXISTS plug_types CASCADE;

-- Supprimer la colonne vehicle_id de reservations si elle existe (ajoutée par V12 mais jamais utilisée)
ALTER TABLE reservations DROP COLUMN IF EXISTS vehicle_id;

-- Commentaire pour documentation
COMMENT ON TABLE stations IS 'Table des bornes de recharge. Le type de prise est stocké directement dans la colonne plug_type (V22)';




