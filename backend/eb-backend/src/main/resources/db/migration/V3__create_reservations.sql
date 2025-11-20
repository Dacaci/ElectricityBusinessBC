-- Migration V3: Système de réservation

-- Table des réservations
CREATE TABLE IF NOT EXISTS reservations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    station_id BIGINT NOT NULL REFERENCES stations(id) ON DELETE RESTRICT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index pour optimiser les recherches
CREATE INDEX IF NOT EXISTS idx_reservations_user_id ON reservations(user_id);
CREATE INDEX IF NOT EXISTS idx_reservations_station_id ON reservations(station_id);
CREATE INDEX IF NOT EXISTS idx_reservations_status ON reservations(status);
CREATE INDEX IF NOT EXISTS idx_reservations_start_time ON reservations(start_time);
CREATE INDEX IF NOT EXISTS idx_reservations_end_time ON reservations(end_time);
CREATE INDEX IF NOT EXISTS idx_reservations_date_range ON reservations(start_time, end_time);

-- Contraintes de validation
ALTER TABLE reservations ADD CONSTRAINT chk_reservation_dates CHECK (end_time > start_time);
ALTER TABLE reservations ADD CONSTRAINT chk_reservation_duration CHECK (EXTRACT(EPOCH FROM (end_time - start_time)) >= 3600); -- Au moins 1 heure
ALTER TABLE reservations ADD CONSTRAINT chk_reservation_amount CHECK (total_amount >= 0);
ALTER TABLE reservations ADD CONSTRAINT chk_reservation_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED'));

-- Contrainte pour éviter les conflits de réservation
-- Une station ne peut pas avoir deux réservations qui se chevauchent
CREATE UNIQUE INDEX idx_reservations_no_overlap 
ON reservations (station_id) 
WHERE status IN ('PENDING', 'CONFIRMED');

-- Fonction pour vérifier les conflits de réservation
CREATE OR REPLACE FUNCTION check_reservation_conflict()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM reservations 
        WHERE station_id = NEW.station_id 
        AND status IN ('PENDING', 'CONFIRMED')
        AND id != COALESCE(NEW.id, 0)
        AND (
            (NEW.start_time BETWEEN start_time AND end_time) OR
            (NEW.end_time BETWEEN start_time AND end_time) OR
            (NEW.start_time <= start_time AND NEW.end_time >= end_time)
        )
    ) THEN
        RAISE EXCEPTION 'Conflit de réservation: la station est déjà réservée pour cette période';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger pour vérifier les conflits
CREATE TRIGGER trigger_check_reservation_conflict
    BEFORE INSERT OR UPDATE ON reservations
    FOR EACH ROW
    EXECUTE FUNCTION check_reservation_conflict();

-- Données de test pour les réservations
INSERT INTO reservations (user_id, station_id, start_time, end_time, total_amount, status, notes) VALUES
(1, 1, CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day 2 hours', 1.00, 'PENDING', 'Réservation de test'),
(1, 2, CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '2 days 3 hours', 1.65, 'CONFIRMED', 'Réservation confirmée')
ON CONFLICT DO NOTHING;

