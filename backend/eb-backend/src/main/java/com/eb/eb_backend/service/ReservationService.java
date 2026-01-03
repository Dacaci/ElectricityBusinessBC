package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.CreateReservationDto;
import com.eb.eb_backend.dto.ReservationDto;
import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.StationStatus;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.exception.ConflictException;
import com.eb.eb_backend.exception.NotFoundException;
import com.eb.eb_backend.repository.ReservationRepository;
import com.eb.eb_backend.repository.StationRepository;
import com.eb.eb_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
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
    
    public ReservationDto createReservation(Long userId, CreateReservationDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé: " + userId));
        
        Station station = stationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new NotFoundException("Station non trouvée: " + dto.getStationId()));
        
        if (station.getStatus() != StationStatus.ACTIVE) {
            throw new IllegalArgumentException("Station inactive");
        }
        
        if (station.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Impossible de réserver sa propre borne");
        }
        
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                station, dto.getStartTime(), dto.getEndTime());
        
        if (!conflicts.isEmpty()) {
            throw new ConflictException("Créneau déjà réservé");
        }
        
        LocalDateTime start = dto.getStartTime();
        LocalDateTime end = dto.getEndTime();
        long hours = Duration.between(start, end).toHours();
        BigDecimal amount = station.getHourlyRate().multiply(BigDecimal.valueOf(hours));
        
        Reservation res = new Reservation();
        res.setUser(user);
        res.setStation(station);
        res.setStartTime(start);
        res.setEndTime(end);
        res.setTotalAmount(amount);
        res.setStatus(Reservation.ReservationStatus.PENDING);
        res.setNotes(dto.getNotes());
        
        return new ReservationDto(reservationRepository.save(res));
    }
    
    @Transactional(readOnly = true)
    public Optional<ReservationDto> getReservationById(Long id) {
        return reservationRepository.findById(id).map(ReservationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable).map(ReservationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + userId));
        return reservationRepository.findByUser(user, pageable).map(ReservationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByStation(Long stationId, Pageable pageable) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundException("Station introuvable: " + stationId));
        return reservationRepository.findByStation(station, pageable).map(ReservationDto::new);
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
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable: " + reservationId));
        
        if (!res.getStation().getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Action non autorisée");
        }
        
        if (res.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new IllegalStateException("Seule une réservation en attente peut être confirmée");
        }
        
        res.setStatus(Reservation.ReservationStatus.CONFIRMED);
        return new ReservationDto(reservationRepository.save(res));
    }
    
    @Transactional
    public ReservationDto refuseReservation(Long reservationId, Long ownerId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable: " + reservationId));
        
        if (!res.getStation().getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Action non autorisée");
        }
        
        if (res.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new IllegalStateException("Seule une réservation en attente peut être refusée");
        }
        
        res.setStatus(Reservation.ReservationStatus.REFUSED);
        return new ReservationDto(reservationRepository.save(res));
    }
    
    @Transactional
    public ReservationDto cancelReservation(Long reservationId, Long userId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable: " + reservationId));
        
        boolean isClient = res.getUser().getId().equals(userId);
        boolean isOwner = res.getStation().getOwner().getId().equals(userId);
        
        if (!isClient && !isOwner) {
            throw new IllegalArgumentException("Action non autorisée");
        }
        
        if (res.getStatus() == Reservation.ReservationStatus.COMPLETED) {
            throw new IllegalStateException("Impossible d'annuler une réservation terminée");
        }
        
        reservationRepository.updateReservationStatus(reservationId, Reservation.ReservationStatus.CANCELLED);
        Reservation updated = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Erreur lors de la mise à jour"));
        
        return new ReservationDto(updated);
    }
    
    public ReservationDto completeReservation(Long reservationId, Long ownerId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable: " + reservationId));
        
        if (!res.getStation().getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Action non autorisée");
        }
        
        if (res.getStatus() != Reservation.ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Seule une réservation confirmée peut être terminée");
        }
        
        if (res.getEndTime().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("La date de fin n'est pas encore atteinte");
        }
        
        res.setStatus(Reservation.ReservationStatus.COMPLETED);
        return new ReservationDto(reservationRepository.save(res));
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getUpcomingUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + userId));
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findUpcomingUserReservations(user, now).stream()
                .map(ReservationDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getUpcomingStationReservations(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundException("Station introuvable: " + stationId));
        return reservationRepository.findUpcomingStationReservations(station, LocalDateTime.now()).stream()
                .map(ReservationDto::new)
                .collect(Collectors.toList());
    }
    
    public void deleteReservation(Long reservationId, Long userId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable: " + reservationId));
        
        if (!res.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Action non autorisée");
        }
        
        if (res.getStatus() != Reservation.ReservationStatus.PENDING && 
            res.getStatus() != Reservation.ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Impossible de supprimer cette réservation");
        }
        
        reservationRepository.delete(res);
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getPastUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + userId));
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findByUser(user, Pageable.unpaged()).stream()
                .filter(r -> r.getEndTime().isBefore(now))
                .map(ReservationDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getCurrentUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + userId));
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findByUser(user, Pageable.unpaged()).stream()
                .filter(r -> !r.getStartTime().isAfter(now) && !r.getEndTime().isBefore(now))
                .map(ReservationDto::new)
                .collect(Collectors.toList());
    }
}

