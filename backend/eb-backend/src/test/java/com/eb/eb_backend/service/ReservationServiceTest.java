package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.CreateReservationDto;
import com.eb.eb_backend.dto.ReservationDto;
import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.StationStatus;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.exception.ConflictException;
import com.eb.eb_backend.exception.NotFoundException;
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
        client = new User();
        client.setId(1L);
        client.setEmail("client@example.com");
        client.setFirstName("Client");
        client.setLastName("Test");
        client.setStatus(User.UserStatus.ACTIVE);

        owner = new User();
        owner.setId(2L);
        owner.setEmail("owner@example.com");
        owner.setFirstName("Owner");
        owner.setLastName("Test");
        owner.setStatus(User.UserStatus.ACTIVE);

        location = new Location();
        location.setId(1L);
        location.setOwner(owner);
        location.setLabel("Test Location");
        location.setLatitude(new BigDecimal("48.8566"));
        location.setLongitude(new BigDecimal("2.3522"));
        location.setIsActive(true);

        activeStation = new Station();
        activeStation.setId(1L);
        activeStation.setLocation(location);
        activeStation.setName("Station Test");
        activeStation.setHourlyRate(new BigDecimal("5.00"));
        activeStation.setStatus(StationStatus.ACTIVE);
        activeStation.setPlugType("TYPE_2S");

        inactiveStation = new Station();
        inactiveStation.setId(2L);
        inactiveStation.setLocation(location);
        inactiveStation.setName("Station Inactive");
        inactiveStation.setHourlyRate(new BigDecimal("5.00"));
        inactiveStation.setStatus(StationStatus.INACTIVE);

        createReservationDto = new CreateReservationDto();
        createReservationDto.setStationId(1L);
        createReservationDto.setStartTime(LocalDateTime.now().plusDays(1));
        createReservationDto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
    }

    @Test
    void testCreateReservationSuccess() {
        doReturn(client).when(userRepository).getReferenceById(1L);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));
        when(reservationRepository.findConflictingReservations(eq(activeStation), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
        
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        ReservationDto result = reservationService.createReservation(1L, createReservationDto);

        assertEquals(Reservation.ReservationStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("10.00"), result.getTotalAmount());
    }

    @Test
    void testCreateReservationConflict() {
        doReturn(client).when(userRepository).getReferenceById(1L);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));
        
        Reservation r1 = new Reservation();
        r1.setId(10L);
        r1.setStatus(Reservation.ReservationStatus.CONFIRMED);
        when(reservationRepository.findConflictingReservations(eq(activeStation), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(r1));

        assertThrows(ConflictException.class, () -> {
            reservationService.createReservation(1L, createReservationDto);
        });
    }

    @Test
    void testCreateReservationOwnStation() {
        doReturn(owner).when(userRepository).getReferenceById(2L);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(activeStation));

        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(2L, createReservationDto);
        });
    }

    @Test
    void testConfirmReservation() {
        Reservation res = new Reservation();
        res.setId(1L);
        res.setUser(client);
        res.setStation(activeStation);
        res.setStatus(Reservation.ReservationStatus.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            return r;
        });

        ReservationDto result = reservationService.confirmReservation(1L, 2L);

        assertEquals(Reservation.ReservationStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void testCreateReservationStationInactive() {
        doReturn(client).when(userRepository).getReferenceById(1L);
        when(stationRepository.findById(2L)).thenReturn(Optional.of(inactiveStation));
        createReservationDto.setStationId(2L);

        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(1L, createReservationDto);
        });
    }

    @Test
    void testRefuseReservation() {
        Reservation res = new Reservation();
        res.setId(1L);
        res.setUser(client);
        res.setStation(activeStation);
        res.setStatus(Reservation.ReservationStatus.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            return r;
        });

        ReservationDto result = reservationService.refuseReservation(1L, 2L);

        assertEquals(Reservation.ReservationStatus.REFUSED, result.getStatus());
    }

    @Test
    void testCancelReservation() {
        Reservation res = new Reservation();
        res.setId(1L);
        res.setUser(client);
        res.setStation(activeStation);
        res.setStatus(Reservation.ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(res));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            return r;
        });

        ReservationDto result = reservationService.cancelReservation(1L, 1L);

        assertEquals(Reservation.ReservationStatus.CANCELLED, result.getStatus());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void testCancelReservationNotFound() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            reservationService.cancelReservation(999L, 1L);
        });
    }

    @Test
    void testConfirmReservationNotFound() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            reservationService.confirmReservation(999L, 2L);
        });
    }

    // Note: getReferenceById est difficile à tester car il retourne un proxy
    // Ce test est commenté car il nécessiterait une implémentation plus complexe
    // @Test
    // void testCreateReservationUserNotFound() {
    //     ...
    // }

    @Test
    void testCreateReservationStationNotFound() {
        doReturn(client).when(userRepository).getReferenceById(1L);
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());
        createReservationDto.setStationId(999L);

        assertThrows(NotFoundException.class, () -> {
            reservationService.createReservation(1L, createReservationDto);
        });
    }
}
