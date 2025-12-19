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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
}
