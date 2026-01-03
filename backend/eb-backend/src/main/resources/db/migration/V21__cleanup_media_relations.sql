-- Migration V21: Nettoyage des relations Media
-- Suppression des relations avec User et Location
-- Media ne doit illustrer que Station

-- Supprimer toutes les données existantes de medias (pour éviter les conflits)
DELETE FROM medias;

-- Supprimer les colonnes user_id et location_id
ALTER TABLE medias DROP COLUMN IF EXISTS user_id;
ALTER TABLE medias DROP COLUMN IF EXISTS location_id;

-- Rendre station_id obligatoire
ALTER TABLE medias ALTER COLUMN station_id SET NOT NULL;

-- Ajouter un commentaire
COMMENT ON TABLE medias IS 'Table des médias (photos et vidéos) liés aux bornes de recharge';
COMMENT ON COLUMN medias.station_id IS 'ID de la station (obligatoire) - Un média appartient toujours à une station';
COMMENT ON COLUMN medias.type IS 'Type de média : IMAGE ou VIDEO';




















