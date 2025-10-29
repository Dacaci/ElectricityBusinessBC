-- Migration V9: Création des tables manquantes (addresses, vehicles, plug_types, medias)

-- Table des adresses
CREATE TABLE IF NOT EXISTS addresses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    street VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    region VARCHAR(255),
    complement VARCHAR(255),
    floor VARCHAR(50),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des types de prise
CREATE TABLE IF NOT EXISTS plug_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    max_power DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des véhicules
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    license_plate VARCHAR(50) UNIQUE NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INTEGER,
    battery_capacity DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des médias
CREATE TABLE IF NOT EXISTS medias (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    file_size BIGINT,
    mime_type VARCHAR(100),
    station_id BIGINT REFERENCES stations(id) ON DELETE CASCADE,
    location_id BIGINT REFERENCES locations(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table de jonction user_vehicle
CREATE TABLE IF NOT EXISTS user_vehicle (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, vehicle_id)
);

-- Table de jonction vehicle_plug_compatibility
CREATE TABLE IF NOT EXISTS vehicle_plug_compatibility (
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    plug_type_id BIGINT NOT NULL REFERENCES plug_types(id) ON DELETE CASCADE,
    PRIMARY KEY (vehicle_id, plug_type_id)
);

-- Table de jonction station_plug_type
CREATE TABLE IF NOT EXISTS station_plug_type (
    station_id BIGINT NOT NULL REFERENCES stations(id) ON DELETE CASCADE,
    plug_type_id BIGINT NOT NULL REFERENCES plug_types(id) ON DELETE CASCADE,
    PRIMARY KEY (station_id, plug_type_id)
);

-- Index pour optimiser les recherches
CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON addresses(user_id);
CREATE INDEX IF NOT EXISTS idx_addresses_city ON addresses(city);
CREATE INDEX IF NOT EXISTS idx_addresses_coordinates ON addresses(latitude, longitude);

CREATE INDEX IF NOT EXISTS idx_vehicles_license_plate ON vehicles(license_plate);

CREATE INDEX IF NOT EXISTS idx_medias_station_id ON medias(station_id);
CREATE INDEX IF NOT EXISTS idx_medias_location_id ON medias(location_id);
CREATE INDEX IF NOT EXISTS idx_medias_user_id ON medias(user_id);
CREATE INDEX IF NOT EXISTS idx_medias_type ON medias(type);

-- Données de test pour les types de prises
INSERT INTO plug_types (name, description, max_power) VALUES
('TYPE2S', 'Type 2 Socket', 22.0),
('TYPE2C', 'Type 2 Cable', 43.0),
('CCS', 'Combined Charging System', 350.0),
('CHADEMO', 'CHAdeMO', 100.0),
('TYPE1', 'Type 1 (J1772)', 7.4)
ON CONFLICT (name) DO NOTHING;

