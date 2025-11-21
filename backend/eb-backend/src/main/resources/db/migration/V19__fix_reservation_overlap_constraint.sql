-- Migration V19: Corriger la contrainte de chevauchement des réservations
-- 
-- Problème: La contrainte unique idx_reservations_no_overlap empêchait d'avoir
-- plus d'une réservation PENDING/CONFIRMED par station, même si les dates ne se chevauchaient pas.
-- 
-- Solution: Supprimer cette contrainte unique incorrecte. Le trigger check_reservation_conflict()
-- et le service vérifient déjà correctement les chevauchements de dates.

-- Supprimer l'index unique incorrect
DROP INDEX IF EXISTS idx_reservations_no_overlap;

-- Note: Le trigger trigger_check_reservation_conflict() continue de vérifier
-- les chevauchements de dates correctement, et le service ReservationService
-- vérifie aussi les conflits avant l'insertion.
