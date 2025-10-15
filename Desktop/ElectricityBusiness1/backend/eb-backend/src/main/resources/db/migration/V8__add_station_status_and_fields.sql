-- Ajouter le champ status et autres champs manquants à la table stations
ALTER TABLE stations 
ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
ADD COLUMN power DECIMAL(10, 2),
ADD COLUMN city VARCHAR(255),
ADD COLUMN latitude DECIMAL(10, 6),
ADD COLUMN longitude DECIMAL(10, 6),
ADD COLUMN instructions TEXT,
ADD COLUMN on_foot BOOLEAN DEFAULT FALSE;

-- Créer un index sur le status pour les requêtes fréquentes
CREATE INDEX idx_stations_status ON stations(status);

-- Créer un index sur la ville pour les recherches par ville
CREATE INDEX idx_stations_city ON stations(city);

-- Créer un index composite pour les recherches géographiques
CREATE INDEX idx_stations_location ON stations(latitude, longitude);



