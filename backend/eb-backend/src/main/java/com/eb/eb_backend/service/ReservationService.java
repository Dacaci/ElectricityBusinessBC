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
import java.math.RoundingMode;
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
        // Utiliser getReferenceById pour éviter une requête SQL inutile (on a juste besoin de la référence)
        User user = userRepository.getReferenceById(userId);
        
        // Récupérer la station (besoin de vérifier le statut, donc findById nécessaire)
        Station station = stationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new NotFoundException("Station non trouvée: " + dto.getStationId()));
        
        // Vérifications métier
        if (station.getStatus() != StationStatus.ACTIVE) {
            throw new IllegalArgumentException("Station inactive");
        }
        
        Long stationOwnerId = station.getOwner().getId();
        if (stationOwnerId.equals(userId)) {
            throw new IllegalArgumentException("Impossible de réserver sa propre borne");
        }
        
        // Vérifier les conflits de réservation
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                station, dto.getStartTime(), dto.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new ConflictException("Créneau déjà réservé");
        }
        
        // Calculer le montant au prorata (utiliser les minutes pour une précision financière)
        long nbMinutes = Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes();
        BigDecimal nbHeuresDecimal = BigDecimal.valueOf(nbMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal prixTotal = station.getHourlyRate().multiply(nbHeuresDecimal)
                .setScale(2, RoundingMode.HALF_UP);
        
        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStation(station);
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setTotalAmount(prixTotal);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setNotes(dto.getNotes());
        
        Reservation saved = reservationRepository.save(reservation);
        return toDto(saved);
    }
    
    private ReservationDto toDto(Reservation reservation) {
        return new ReservationDto(reservation);
    }
    
    @Transactional(readOnly = true)
    public Optional<ReservationDto> getReservationById(Long id) {
        return reservationRepository.findById(id).map(this::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable).map(this::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + userId));
        return reservationRepository.findByUser(user, pageable).map(this::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByStation(Long stationId, Pageable pageable) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundException("Station introuvable: " + stationId));
        return reservationRepository.findByStation(station, pageable).map(this::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> getReservationsByStatus(
            Reservation.ReservationStatus status, 
            Pageable pageable) {
        return reservationRepository.findByStatus(status, pageable)
                .map(this::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<ReservationDto> searchReservations(
            Long userId,
            Long stationId,
            Reservation.ReservationStatus status,
            String search,
            Pageable pageable) {
        return reservationRepository.searchReservations(userId, stationId, status, search, pageable)
                .map(this::toDto);
    }
    
    private Reservation getReservationAndCheckOwnership(Long reservationId, Long ownerId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable: " + reservationId));
        
        if (!res.getStation().getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Action non autorisée");
        }
        
        return res;
    }
    
    public ReservationDto confirmReservation(Long reservationId, Long ownerId) {
        Reservation res = getReservationAndCheckOwnership(reservationId, ownerId);
        res.confirm();
        return toDto(reservationRepository.save(res));
    }
    
    @Transactional
    public ReservationDto refuseReservation(Long reservationId, Long ownerId) {
        Reservation res = getReservationAndCheckOwnership(reservationId, ownerId);
        res.refuse();
        return toDto(reservationRepository.save(res));
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
        
        res.cancel();
        return toDto(reservationRepository.save(res));
    }
    
    public ReservationDto completeReservation(Long reservationId, Long ownerId) {
        Reservation res = getReservationAndCheckOwnership(reservationId, ownerId);
        res.complete();
        return toDto(reservationRepository.save(res));
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getUpcomingUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + userId));
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findUpcomingUserReservations(user, now).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getUpcomingStationReservations(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundException("Station introuvable: " + stationId));
        return reservationRepository.findUpcomingStationReservations(station, LocalDateTime.now()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public void deleteReservation(Long reservationId, Long userId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable: " + reservationId));
        
        if (!res.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Action non autorisée");
        }
        
        // Mettre le statut à CANCELLED au lieu de supprimer pour garder l'historique
        // Cela empêche aussi le contournement de la règle de suppression de borne
        if (res.getStatus() != Reservation.ReservationStatus.CANCELLED) {
            res.cancel();
            reservationRepository.save(res);
        }
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getPastUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + userId));
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findPastUserReservations(user, now).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDto> getCurrentUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + userId));
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findCurrentUserReservations(user, now).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

