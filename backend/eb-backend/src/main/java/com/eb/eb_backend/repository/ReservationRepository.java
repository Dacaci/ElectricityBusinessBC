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

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    Page<Reservation> findByUser(User user, Pageable pageable);
    
    Page<Reservation> findByStation(Station station, Pageable pageable);
    
    Page<Reservation> findByStatus(Reservation.ReservationStatus status, Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.station = :station " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "AND ((:startTime >= r.startTime AND :startTime < r.endTime) OR " +
           "(:endTime > r.startTime AND :endTime <= r.endTime) OR " +
           "(:startTime <= r.startTime AND :endTime >= r.endTime))")
    List<Reservation> findConflictingReservations(
        @Param("station") Station station,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    @Query("SELECT r FROM Reservation r WHERE " +
           "(:userId IS NULL OR r.user.id = :userId) AND " +
           "(:stationId IS NULL OR r.station.id = :stationId) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(r.notes LIKE CONCAT('%', :search, '%') OR r.user.firstName LIKE CONCAT('%', :search, '%') OR r.user.lastName LIKE CONCAT('%', :search, '%'))")
    Page<Reservation> searchReservations(
        @Param("userId") Long userId,
        @Param("stationId") Long stationId,
        @Param("status") Reservation.ReservationStatus status,
        @Param("search") String search,
        Pageable pageable
    );
    
    @Query("SELECT r FROM Reservation r WHERE r.user = :user " +
           "AND r.startTime > :now " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY r.startTime ASC")
    List<Reservation> findUpcomingUserReservations(@Param("user") User user, @Param("now") LocalDateTime now);
    
    @Query("SELECT r FROM Reservation r WHERE r.station = :station " +
           "AND r.startTime > :now " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY r.startTime ASC")
    List<Reservation> findUpcomingStationReservations(@Param("station") Station station, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE Reservation r SET r.status = :status WHERE r.id = :id")
    void updateReservationStatus(@Param("id") Long id, @Param("status") Reservation.ReservationStatus status);
}

