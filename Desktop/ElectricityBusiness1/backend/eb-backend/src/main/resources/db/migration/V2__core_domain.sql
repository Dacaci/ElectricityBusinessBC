-- Migration V2: Domaine métier - Lieux et bornes de recharge

-- Table des lieux de recharge
CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    label VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des bornes de recharge
CREATE TABLE IF NOT EXISTS stations (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    hourly_rate DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    plug_type VARCHAR(50) NOT NULL DEFAULT 'TYPE2S',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(owner_id, name)
);

-- Index pour optimiser les recherches
CREATE INDEX IF NOT EXISTS idx_locations_owner_id ON locations(owner_id);
CREATE INDEX IF NOT EXISTS idx_locations_coordinates ON locations(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_locations_active ON locations(is_active);
CREATE INDEX IF NOT EXISTS idx_stations_owner_id ON stations(owner_id);
CREATE INDEX IF NOT EXISTS idx_stations_location_id ON stations(location_id);
CREATE INDEX IF NOT EXISTS idx_stations_active ON stations(is_active);

-- Contraintes de validation
ALTER TABLE locations ADD CONSTRAINT chk_latitude CHECK (latitude >= -90 AND latitude <= 90);
ALTER TABLE locations ADD CONSTRAINT chk_longitude CHECK (longitude >= -180 AND longitude <= 180);
ALTER TABLE stations ADD CONSTRAINT chk_hourly_rate CHECK (hourly_rate >= 0);
ALTER TABLE stations ADD CONSTRAINT chk_plug_type CHECK (plug_type = 'TYPE2S');

-- Données de test
INSERT INTO locations (owner_id, label, address, latitude, longitude, description) VALUES
(1, 'Station Centre Ville', 'Place de la République, 75001 Paris', 48.8566, 2.3522, 'Station de recharge en plein centre de Paris'),
(1, 'Station Aéroport CDG', 'Terminal 2F, 95700 Roissy-en-France', 49.0097, 2.5479, 'Station de recharge à l''aéroport Charles de Gaulle')
ON CONFLICT DO NOTHING;

INSERT INTO stations (owner_id, location_id, name, hourly_rate, plug_type) VALUES
(1, 1, 'Borne 1', 0.50, 'TYPE2S'),
(1, 1, 'Borne 2', 0.55, 'TYPE2S'),
(1, 2, 'Borne Aéroport 1', 0.60, 'TYPE2S'),
(1, 2, 'Borne Aéroport 2', 0.60, 'TYPE2S')
ON CONFLICT DO NOTHING;
