package com.eb.eb_backend.repository;

import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // Recherche par utilisateur
    Page<Reservation> findByUser(User user, Pageable pageable);
    
    // Recherche par station
    Page<Reservation> findByStation(Station station, Pageable pageable);
    
    // Recherche par statut
    Page<Reservation> findByStatus(Reservation.ReservationStatus status, Pageable pageable);
    
    // Recherche par utilisateur et statut
    Page<Reservation> findByUserAndStatus(User user, Reservation.ReservationStatus status, Pageable pageable);
    
    // Recherche par station et statut
    Page<Reservation> findByStationAndStatus(Station station, Reservation.ReservationStatus status, Pageable pageable);
    
    // Vérifier les conflits de réservation
    @Query("SELECT r FROM Reservation r WHERE r.station = :station " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "AND ((:startTime BETWEEN r.startTime AND r.endTime) OR " +
           "(:endTime BETWEEN r.startTime AND r.endTime) OR " +
           "(:startTime <= r.startTime AND :endTime >= r.endTime)) " +
           "AND (:excludeId IS NULL OR r.id != :excludeId)")
    List<Reservation> findConflictingReservations(
        @Param("station") Station station,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("excludeId") Long excludeId
    );
    
    // Réservations d'un utilisateur dans une période
    @Query("SELECT r FROM Reservation r WHERE r.user = :user " +
           "AND r.startTime >= :startDate AND r.endTime <= :endDate " +
           "ORDER BY r.startTime DESC")
    List<Reservation> findUserReservationsInPeriod(
        @Param("user") User user,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Réservations d'une station dans une période
    @Query("SELECT r FROM Reservation r WHERE r.station = :station " +
           "AND r.startTime >= :startDate AND r.endTime <= :endDate " +
           "ORDER BY r.startTime DESC")
    List<Reservation> findStationReservationsInPeriod(
        @Param("station") Station station,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Recherche textuelle dans les notes
    @Query("SELECT r FROM Reservation r WHERE " +
           "(:userId IS NULL OR r.user.id = :userId) AND " +
           "(:stationId IS NULL OR r.station.id = :stationId) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(LOWER(r.notes) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.station.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Reservation> searchReservations(
        @Param("userId") Long userId,
        @Param("stationId") Long stationId,
        @Param("status") Reservation.ReservationStatus status,
        @Param("search") String search,
        Pageable pageable
    );
    
    // Statistiques des réservations
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user = :user AND r.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") Reservation.ReservationStatus status);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.station = :station AND r.status = :status")
    long countByStationAndStatus(@Param("station") Station station, @Param("status") Reservation.ReservationStatus status);
    
    // Réservations à venir pour un utilisateur
    @Query("SELECT r FROM Reservation r WHERE r.user = :user " +
           "AND r.startTime > :now " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY r.startTime ASC")
    List<Reservation> findUpcomingUserReservations(@Param("user") User user, @Param("now") LocalDateTime now);
    
    // Réservations à venir pour une station
    @Query("SELECT r FROM Reservation r WHERE r.station = :station " +
           "AND r.startTime > :now " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY r.startTime ASC")
    List<Reservation> findUpcomingStationReservations(@Param("station") Station station, @Param("now") LocalDateTime now);
    
    // Mise à jour du statut sans validation
    @Modifying
    @Query("UPDATE Reservation r SET r.status = :status WHERE r.id = :id")
    void updateReservationStatus(@Param("id") Long id, @Param("status") Reservation.ReservationStatus status);
}

