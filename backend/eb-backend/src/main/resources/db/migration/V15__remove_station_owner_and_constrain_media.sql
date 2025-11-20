-- V15: Corrections MCD - Suppression redondance Station.owner et contrainte Media
-- Conformité MERISE : élimination de la redondance propriétaire et contrainte d'exclusivité média

-- 1. Supprimer la contrainte de clé étrangère existante si elle existe
ALTER TABLE stations DROP CONSTRAINT IF EXISTS fk_station_owner;

-- 2. Mettre à jour les owner_id des stations pour qu'ils correspondent à ceux de leur location
-- Ceci est une étape de correction des données avant de supprimer la colonne
UPDATE stations s
SET owner_id = l.owner_id
FROM locations l
WHERE s.location_id = l.id AND s.owner_id IS DISTINCT FROM l.owner_id;

-- 3. Supprimer la colonne owner_id de la table stations
ALTER TABLE stations DROP COLUMN IF EXISTS owner_id;

-- 4. Ajouter une contrainte CHECK pour s'assurer qu'un média est lié à exactement une entité parent
-- (Station, Location ou User)
ALTER TABLE medias ADD CONSTRAINT media_single_parent_check CHECK (
  (CASE WHEN station_id IS NOT NULL THEN 1 ELSE 0 END +
   CASE WHEN location_id IS NOT NULL THEN 1 ELSE 0 END +
   CASE WHEN user_id IS NOT NULL THEN 1 ELSE 0 END) = 1
);

-- 5. Supprimer la contrainte d'unicité sur (owner_id, name) si elle existe
-- car owner_id n'existe plus
ALTER TABLE stations DROP CONSTRAINT IF EXISTS uk_station_owner_name;
