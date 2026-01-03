-- Migration V23: Ajouter un index composite pour optimiser la détection de conflits de réservation
-- 
-- Problème: Les requêtes de détection de conflits (dans le trigger et le service) 
-- nécessitent de filtrer par station_id ET de comparer les dates (start_time, end_time).
-- Sans index composite, PostgreSQL doit scanner toutes les réservations d'une station
-- pour vérifier les chevauchements temporels.
-- 
-- Solution: Créer un index composite sur (station_id, start_time, end_time) qui permet
-- à PostgreSQL d'optimiser directement les requêtes de détection de conflits.
-- Cet index est utilisé par :
-- - Le trigger check_reservation_conflict() (V3)
-- - La méthode findConflictingReservations() du ReservationRepository
--
-- Performance: Avec cet index, même avec des milliers de réservations par station,
-- la détection de conflit s'exécute en temps constant grâce à l'utilisation de l'index.

CREATE INDEX IF NOT EXISTS idx_reservation_conflict 
ON reservations (station_id, start_time, end_time)
WHERE status IN ('PENDING', 'CONFIRMED');

-- Note: L'index est partiel (avec WHERE) car seules les réservations PENDING/CONFIRMED
-- peuvent créer des conflits. Les réservations CANCELLED ou COMPLETED ne sont pas prises
-- en compte dans la détection de conflits.








