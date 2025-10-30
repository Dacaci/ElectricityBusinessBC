-- V13: Ajouter des coordonnées optionnelles sur les bornes (stations)

ALTER TABLE stations
ADD COLUMN IF NOT EXISTS latitude NUMERIC(10,8) NULL,
ADD COLUMN IF NOT EXISTS longitude NUMERIC(11,8) NULL;

-- Index facultatifs si besoin de recherches géo ultérieures
-- CREATE INDEX IF NOT EXISTS idx_stations_lat_lng ON stations(latitude, longitude);


