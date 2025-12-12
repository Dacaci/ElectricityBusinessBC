package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.CreateReservationDto;
import com.eb.eb_backend.dto.ReservationDto;
import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.ReservationRepository;
import com.eb.eb_backend.repository.StationRepository;
import com.eb.eb_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    
    public ReservationDto createReservation(Long userId, CreateReservationDto createReservationDto) {
        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));
        
        // Vérifier que la station existe et est active
        Station station = stationRepository.findById(createReservationDto.getStationId())
                .orElseThrow(() -> new IllegalArgumentException("Station non trouvée avec l'ID: " + createReservationDto.getStationId()));
        
        if (station.getStatus() != com.eb.eb_backend.entity.StationStatus.ACTIVE) {
            throw new IllegalArgumentException("La station n'est pas active");
        }
        
        // Vérifier que l'utilisateur ne réserve pas sa propre station
        if (station.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas réserver votre propre station");
        }
        
        // Vérifier les conflits de réservation
        List<Reservation> conflictingReservations = reservationRepository.findConflictingReservations(
                station, 
                createReservationDto.getStartTime(), 
                createReservationDto.getEndTime(), 
                null
        );
        
        if (!conflictingReservations.isEmpty()) {
            throw new IllegalArgumentException("La station est déjà réservée pour cette période");
        }
        
        // Calculer le montant total
        long durationInHours = java.time.Duration.between(
                createReservationDto.getStartTime(), 
                createReservationDto.getEndTime()
        ).toHours();
        
        BigDecimal totalAmount = station.getHourlyRate().multiply(BigDecimal.valueOf(durationInHours));
        
        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStation(station);
        reservation.setStartTime(createReservationDto.getStartTime());
        reservation.setEndTime(createReservationDto.getEndTime());
        reservation.setTotalAmount(totalAmount);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setNotes(createReservationDto.getNotes());
        
        Reservation savedReservation = reservationRepository.save(reservation);
        return new ReservationDto(savedReservation);
    }
    
    @Transactional(readOnly = true)
    public Optional<ReservationDto> getReservationById(Long id) {
        return reservationRepository.findById(id)
                .map(ReservationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(ReservationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));
        
        return reservationRepository.findByUser(user, pageable)
                .map(ReservationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByStation(Long stationId, Pageable pageable) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station non trouvée avec l'ID: " + stationId));
        
        return reservationRepository.findByStation(station, pageable)
                .map(ReservationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByStatus(
            Reservation.ReservationStatus status, 
            Pageable pageable) {
        return reservationRepository.findByStatus(status, pageable)
                .map(ReservationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> searchReservations(
            Long userId,
            Long stationId,
            Reservation.ReservationStatus status,
            String search,
            Pageable pageable) {
        return reservationRepository.searchReservations(userId, stationId, status, search, pageable)
                .map(ReservationDto::new);
    }
    
    public ReservationDto confirmReservation(Long reservationId, Long ownerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée avec l'ID: " + reservationId));
        
        // Vérifier que l'utilisateur est le propriétaire de la station
        if (!reservation.getStation().getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à confirmer cette réservation");
        }
        
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Seules les réservations en attente peuvent être confirmées");
        }
        
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        Reservation savedReservation = reservationRepository.save(reservation);
        return new ReservationDto(savedReservation);
    }
    
    @Transactional
    public ReservationDto refuseReservation(Long reservationId, Long ownerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée avec l'ID: " + reservationId));
        
        // Vérifier que l'utilisateur est le propriétaire de la station
        if (!reservation.getStation().getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à refuser cette réservation");
        }
        
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Seules les réservations en attente peuvent être refusées");
        }
        
        reservation.setStatus(Reservation.ReservationStatus.REFUSED);
        Reservation savedReservation = reservationRepository.save(reservation);
        return new ReservationDto(savedReservation);
    }
    
    @Transactional
    public ReservationDto cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée avec l'ID: " + reservationId));
        
        // Vérifier que l'utilisateur est soit le client soit le propriétaire
        boolean isClient = reservation.getUser().getId().equals(userId);
        boolean isOwner = reservation.getStation().getOwner().getId().equals(userId);
        
        if (!isClient && !isOwner) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à annuler cette réservation");
        }
        
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("Cette réservation est déjà annulée");
        }
        
        if (reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {
            throw new IllegalArgumentException("Les réservations terminées ne peuvent pas être annulées");
        }
        
        // Utiliser une requête native pour éviter la validation
        reservationRepository.updateReservationStatus(reservationId, Reservation.ReservationStatus.CANCELLED);
        
        // Récupérer la réservation mise à jour
        Reservation updatedReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée après mise à jour"));
        
        return new ReservationDto(updatedReservation);
    }
    
    public ReservationDto completeReservation(Long reservationId, Long ownerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée avec l'ID: " + reservationId));
        
        // Vérifier que l'utilisateur est le propriétaire de la station
        if (!reservation.getStation().getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à marquer cette réservation comme terminée");
        }
        
        if (reservation.getStatus() != Reservation.ReservationStatus.CONFIRMED) {
            throw new IllegalArgumentException("Seules les réservations confirmées peuvent être marquées comme terminées");
        }
        
        // Vérifier que la date de fin est passée
        if (reservation.getEndTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La réservation ne peut être marquée comme terminée qu'après sa date de fin");
        }
        
        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        Reservation savedReservation = reservationRepository.save(reservation);
        return new ReservationDto(savedReservation);
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getUpcomingUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));
        
        return reservationRepository.findUpcomingUserReservations(user, LocalDateTime.now())
                .stream()
                .map(ReservationDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getUpcomingStationReservations(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station non trouvée avec l'ID: " + stationId));
        
        return reservationRepository.findUpcomingStationReservations(station, LocalDateTime.now())
                .stream()
                .map(ReservationDto::new)
                .collect(Collectors.toList());
    }
    
    public void deleteReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée avec l'ID: " + reservationId));
        
        // Vérifier que l'utilisateur est le client
        if (!reservation.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer cette réservation");
        }
        
        // Ne permettre la suppression que pour les réservations en attente ou annulées
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING && 
            reservation.getStatus() != Reservation.ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("Seules les réservations en attente ou annulées peuvent être supprimées");
        }
        
        reservationRepository.delete(reservation);
    }
    
    /**
     * Récupérer les réservations passées d'un utilisateur
     * Équivalent SQL: 
     * SELECT * FROM utilisateurs u
     * JOIN reservations r ON r.id_utilisateur = u.id
     * WHERE U.id = userId AND NOW() > r.date_fin
     */
    @Transactional(readOnly = true)
    public List<ReservationDto> getPastUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        return reservationRepository.findByUser(user, org.springframework.data.domain.Pageable.unpaged()).stream()
                .filter(reservation -> reservation.getEndTime().isBefore(now))
                .map(ReservationDto::new)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Récupérer les réservations actuelles (en cours) d'un utilisateur
     * Équivalent SQL:
     * SELECT * FROM reservations WHERE id_utilisateur = userId 
     * AND date_debut <= NOW() AND date_fin >= NOW()
     */
    @Transactional(readOnly = true)
    public List<ReservationDto> getCurrentUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        return reservationRepository.findByUser(user, org.springframework.data.domain.Pageable.unpaged()).stream()
                .filter(reservation -> 
                    !reservation.getStartTime().isAfter(now) && 
                    !reservation.getEndTime().isBefore(now))
                .map(ReservationDto::new)
                .collect(java.util.stream.Collectors.toList());
    }
}

