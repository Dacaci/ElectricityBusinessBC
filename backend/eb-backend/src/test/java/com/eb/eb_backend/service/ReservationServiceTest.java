package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.CreateReservationDto;
import com.eb.eb_backend.dto.ReservationDto;
import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.StationStatus;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.ReservationRepository;
import com.eb.eb_backend.repository.StationRepository;
import com.eb.eb_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User client;
    private User owner;
    private Location location;
    private Station activeStation;
    private Station inactiveStation;
    private CreateReservationDto createReservationDto;

    @BeforeEach
    void setUp() {
        // Créer un client
        client = new User();
        client.setId(1L);
        client.setEmail("client@example.com");
        client.setFirstName("Client");
        client.setLastName("Test");
        client.setStatus(User.UserStatus.ACTIVE);

        // Créer un propriétaire
        owner = new User();
        owner.setId(2L);
        owner.setEmail("owner@example.com");
        owner.setFirstName("Owner");
        owner.setLastName("Test");
        owner.setStatus(User.UserStatus.ACTIVE);

        // Créer un lieu
        location = new Location();
        location.setId(1L);
        location.setOwner(owner);
        location.setLabel("Test Location");
        location.setLatitude(new BigDecimal("48.8566"));
        location.setLongitude(new BigDecimal("2.3522"));
        location.setIsActive(true);

        // Créer une station active
        activeStation = new Station();
        activeStation.setId(1L);
        activeStation.setLocation(location);
        activeStation.setName("Station Test");
        activeStation.setHourlyRate(new BigDecimal("5.00"));
        activeStation.setStatus(StationStatus.ACTIVE);
        activeStation.setPlugType("TYPE_2S");

        // Créer une station inactive
        inactiveStation = new Station();
        inactiveStation.setId(2L);
        inactiveStation.setLocation(location);
        inactiveStation.setName("Station Inactive");
        inactiveStation.setHourlyRate(new BigDecimal("5.00"));
        inactiveStation.setStatus(StationStatus.INACTIVE);

        // Créer un DTO de réservation
        createReservationDto = new CreateReservationDto();
        createReservationDto.setStationId(1L);
        createReservationDto.setStartTime(LocalDateTime.now().plusDays(1));
        createReservationDto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
    }

    @Test
    void testCreateReservationSuccess() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));
        when(reservationRepository.findConflictingReservations(any(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        // Act
        ReservationDto result = reservationService.createReservation(1L, createReservationDto);

        // Assert
        assertNotNull(result);
        assertEquals(Reservation.ReservationStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("10.00"), result.getTotalAmount()); // 2 heures × 5€
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testCreateReservationStationInactive() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(inactiveStation));
        createReservationDto.setStationId(2L);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(1L, createReservationDto);
        });
        assertTrue(exception.getMessage().contains("n'est pas active"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testCreateReservationOwnStation() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(2L, createReservationDto);
        });
        assertTrue(exception.getMessage().contains("propre station"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testCreateReservationConflict() {
        // Arrange
        Reservation existingReservation = new Reservation();
        existingReservation.setId(1L);
        existingReservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        List<Reservation> conflicts = List.of(existingReservation);

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));
        when(reservationRepository.findConflictingReservations(any(), any(), any(), any()))
                .thenReturn(conflicts);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(1L, createReservationDto);
        });
        assertTrue(exception.getMessage().contains("déjà réservée"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testCreateReservationInvalidDates() {
        // Arrange
        createReservationDto.setStartTime(LocalDateTime.now().plusDays(2));
        createReservationDto.setEndTime(LocalDateTime.now().plusDays(1)); // Fin avant début

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));

        // Act & Assert
        // La validation Bean Validation devrait lever une exception
        // Mais comme on teste le service, on vérifie que le calcul de durée échoue
        assertThrows(Exception.class, () -> {
            reservationService.createReservation(1L, createReservationDto);
        });
    }

    @Test
    void testCreateReservationDurationLessThan1Hour() {
        // Arrange
        createReservationDto.setStartTime(LocalDateTime.now().plusDays(1));
        createReservationDto.setEndTime(LocalDateTime.now().plusDays(1).plusMinutes(30)); // 30 minutes

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));
        when(reservationRepository.findConflictingReservations(any(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        // Act
        ReservationDto result = reservationService.createReservation(1L, createReservationDto);

        // Assert
        // Le service accepte mais le montant sera 0 (0 heures × 5€)
        assertNotNull(result);
        assertEquals(new BigDecimal("0.00"), result.getTotalAmount());
    }

    @Test
    void testCreateReservationPriceCalculation() {
        // Arrange
        activeStation.setHourlyRate(new BigDecimal("7.50"));
        createReservationDto.setStartTime(LocalDateTime.now().plusDays(1));
        createReservationDto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(3)); // 3 heures

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));
        when(reservationRepository.findConflictingReservations(any(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        // Act
        ReservationDto result = reservationService.createReservation(1L, createReservationDto);

        // Assert
        assertEquals(new BigDecimal("22.50"), result.getTotalAmount()); // 3 heures × 7.50€
    }

    @Test
    void testCreateReservationStatusPending() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));
        when(reservationRepository.findConflictingReservations(any(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        // Act
        ReservationDto result = reservationService.createReservation(1L, createReservationDto);

        // Assert
        assertEquals(Reservation.ReservationStatus.PENDING, result.getStatus());
    }

    @Test
    void testConfirmReservationSuccess() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            return r;
        });

        // Act
        ReservationDto result = reservationService.confirmReservation(1L, 2L); // ownerId = 2

        // Assert
        assertEquals(Reservation.ReservationStatus.CONFIRMED, result.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testRefuseReservationSuccess() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            return r;
        });

        // Act
        ReservationDto result = reservationService.refuseReservation(1L, 2L); // ownerId = 2

        // Assert
        assertEquals(Reservation.ReservationStatus.REFUSED, result.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testCancelReservationSuccess() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);

        Reservation cancelledReservation = new Reservation();
        cancelledReservation.setId(1L);
        cancelledReservation.setUser(client);
        cancelledReservation.setStation(activeStation);
        cancelledReservation.setStatus(Reservation.ReservationStatus.CANCELLED);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation))
                .thenReturn(Optional.of(cancelledReservation));
        doNothing().when(reservationRepository).updateReservationStatus(1L, Reservation.ReservationStatus.CANCELLED);

        // Act
        ReservationDto result = reservationService.cancelReservation(1L, 1L); // clientId = 1

        // Assert
        assertNotNull(result);
        verify(reservationRepository, times(1)).updateReservationStatus(1L, Reservation.ReservationStatus.CANCELLED);
    }

    @Test
    void testCompleteReservationSuccess() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation.setEndTime(LocalDateTime.now().minusHours(1)); // Réservation terminée

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setStatus(Reservation.ReservationStatus.COMPLETED);
            return r;
        });

        // Act
        ReservationDto result = reservationService.completeReservation(1L, 2L); // ownerId = 2

        // Assert
        assertEquals(Reservation.ReservationStatus.COMPLETED, result.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testConfirmReservationNotOwner() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.confirmReservation(1L, 999L); // Mauvais ownerId
        });
        assertTrue(exception.getMessage().contains("autorisé"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testRefuseReservationNotPending() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED); // Déjà confirmée

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.refuseReservation(1L, 2L);
        });
        assertTrue(exception.getMessage().contains("attente"));
        verify(reservationRepository, never()).save(any());
    }

    // Tests pour les méthodes de récupération (logique métier importante)
    
    @Test
    void testGetReservationById() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act
        Optional<ReservationDto> result = reservationService.getReservationById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetReservationByIdNotFound() {
        // Arrange
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<ReservationDto> result = reservationService.getReservationById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetReservationsByUser() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        
        Page<Reservation> page = new PageImpl<>(List.of(reservation));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepository.findByUser(client, pageable)).thenReturn(page);

        // Act
        Page<ReservationDto> result = reservationService.getReservationsByUser(1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetReservationsByStation() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        
        Page<Reservation> page = new PageImpl<>(List.of(reservation));
        Pageable pageable = PageRequest.of(0, 10);

        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));
        when(reservationRepository.findByStation(activeStation, pageable)).thenReturn(page);

        // Act
        Page<ReservationDto> result = reservationService.getReservationsByStation(1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(stationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetReservationsByStatus() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        
        Page<Reservation> page = new PageImpl<>(List.of(reservation));
        Pageable pageable = PageRequest.of(0, 10);

        when(reservationRepository.findByStatus(Reservation.ReservationStatus.PENDING, pageable))
                .thenReturn(page);

        // Act
        Page<ReservationDto> result = reservationService.getReservationsByStatus(
                Reservation.ReservationStatus.PENDING, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(reservationRepository, times(1))
                .findByStatus(Reservation.ReservationStatus.PENDING, pageable);
    }

    @Test
    void testGetUpcomingUserReservations() {
        // Arrange
        Reservation upcomingReservation = new Reservation();
        upcomingReservation.setId(1L);
        upcomingReservation.setUser(client);
        upcomingReservation.setStation(activeStation);
        upcomingReservation.setStartTime(LocalDateTime.now().plusDays(1));
        upcomingReservation.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepository.findUpcomingUserReservations(eq(client), any(LocalDateTime.class)))
                .thenReturn(List.of(upcomingReservation));

        // Act
        List<ReservationDto> result = reservationService.getUpcomingUserReservations(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUpcomingStationReservations() {
        // Arrange
        Reservation upcomingReservation = new Reservation();
        upcomingReservation.setId(1L);
        upcomingReservation.setUser(client);
        upcomingReservation.setStation(activeStation);
        upcomingReservation.setStartTime(LocalDateTime.now().plusDays(1));

        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));
        when(reservationRepository.findUpcomingStationReservations(eq(activeStation), any(LocalDateTime.class)))
                .thenReturn(List.of(upcomingReservation));

        // Act
        List<ReservationDto> result = reservationService.getUpcomingStationReservations(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(stationRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteReservationSuccess() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        doNothing().when(reservationRepository).delete(reservation);

        // Act
        reservationService.deleteReservation(1L, 1L); // clientId = 1

        // Assert
        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    void testDeleteReservationNotAuthorized() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client); // client = 1L
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.deleteReservation(1L, 999L); // Mauvais userId
        });
        assertTrue(exception.getMessage().contains("autorisé"));
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    void testDeleteReservationNotPendingOrCancelled() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED); // Pas PENDING ni CANCELLED

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.deleteReservation(1L, 1L);
        });
        assertTrue(exception.getMessage().contains("attente ou annulées"));
        verify(reservationRepository, never()).delete(any());
    }

    @Test
    void testGetPastUserReservations() {
        // Arrange
        Reservation pastReservation = new Reservation();
        pastReservation.setId(1L);
        pastReservation.setUser(client);
        pastReservation.setStation(activeStation);
        pastReservation.setEndTime(LocalDateTime.now().minusDays(1)); // Passée

        Reservation futureReservation = new Reservation();
        futureReservation.setId(2L);
        futureReservation.setUser(client);
        futureReservation.setStation(activeStation);
        futureReservation.setEndTime(LocalDateTime.now().plusDays(1)); // Future

        Page<Reservation> page = new PageImpl<>(List.of(pastReservation, futureReservation));

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepository.findByUser(eq(client), any(Pageable.class))).thenReturn(page);

        // Act
        List<ReservationDto> result = reservationService.getPastUserReservations(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Seule la réservation passée
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetCurrentUserReservations() {
        // Arrange
        Reservation currentReservation = new Reservation();
        currentReservation.setId(1L);
        currentReservation.setUser(client);
        currentReservation.setStation(activeStation);
        currentReservation.setStartTime(LocalDateTime.now().minusHours(1)); // Commencée
        currentReservation.setEndTime(LocalDateTime.now().plusHours(1)); // Pas encore finie

        Reservation pastReservation = new Reservation();
        pastReservation.setId(2L);
        pastReservation.setUser(client);
        pastReservation.setStation(activeStation);
        pastReservation.setStartTime(LocalDateTime.now().minusDays(2));
        pastReservation.setEndTime(LocalDateTime.now().minusDays(1)); // Passée

        Page<Reservation> page = new PageImpl<>(List.of(currentReservation, pastReservation));

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepository.findByUser(eq(client), any(Pageable.class))).thenReturn(page);

        // Act
        List<ReservationDto> result = reservationService.getCurrentUserReservations(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Seule la réservation en cours
        assertEquals(1L, result.get(0).getId());
    }

    // Cas limites pour cancelReservation
    
    @Test
    void testCancelReservationAlreadyCancelled() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.cancelReservation(1L, 1L);
        });
        assertTrue(exception.getMessage().contains("déjà annulée"));
    }

    @Test
    void testCancelReservationCompleted() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.cancelReservation(1L, 1L);
        });
        assertTrue(exception.getMessage().contains("terminées"));
    }

    @Test
    void testCancelReservationNotAuthorized() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client); // client = 1L
        reservation.setStation(activeStation); // owner = 2L
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.cancelReservation(1L, 999L); // Ni client ni owner
        });
        assertTrue(exception.getMessage().contains("autorisé"));
    }

    // Cas limites pour completeReservation
    
    @Test
    void testCompleteReservationNotConfirmed() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.PENDING); // Pas CONFIRMED

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.completeReservation(1L, 2L);
        });
        assertTrue(exception.getMessage().contains("confirmées"));
    }

    @Test
    void testCompleteReservationEndTimeNotPassed() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStation(activeStation);
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation.setEndTime(LocalDateTime.now().plusHours(1)); // Pas encore terminée

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.completeReservation(1L, 2L);
        });
        assertTrue(exception.getMessage().contains("date de fin"));
    }

    @Test
    void testCompleteReservationNotOwner() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStation(activeStation); // owner = 2L
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation.setEndTime(LocalDateTime.now().minusHours(1));

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.completeReservation(1L, 999L); // Mauvais ownerId
        });
        assertTrue(exception.getMessage().contains("autorisé"));
    }
}
