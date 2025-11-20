-- Ajouter le statut REFUSED aux réservations
ALTER TABLE reservations
DROP CONSTRAINT IF EXISTS chk_reservation_status;

ALTER TABLE reservations
ADD CONSTRAINT chk_reservation_status CHECK (
    status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'REFUSED')
);

