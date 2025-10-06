package com.eb.eb_backend.service;

import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ExportService exportService;

    private List<Reservation> testReservations;

    @BeforeEach
    void setUp() {
        // Créer des données de test
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");

        Location location = new Location();
        location.setId(1L);
        location.setAddress("123 Rue de la Paix, Paris");

        Station station1 = new Station();
        station1.setId(1L);
        station1.setName("Station Test 1");
        station1.setLocation(location);

        Station station2 = new Station();
        station2.setId(2L);
        station2.setName("Station Test 2");
        station2.setLocation(location);

        Reservation reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setUser(user1);
        reservation1.setStation(station1);
        reservation1.setStartTime(LocalDateTime.now().minusDays(1));
        reservation1.setEndTime(LocalDateTime.now().minusDays(1).plusHours(2));
        reservation1.setTotalAmount(new BigDecimal("5.00"));
        reservation1.setStatus(Reservation.ReservationStatus.COMPLETED);

        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setUser(user2);
        reservation2.setStation(station2);
        reservation2.setStartTime(LocalDateTime.now().minusDays(2));
        reservation2.setEndTime(LocalDateTime.now().minusDays(2).plusHours(1));
        reservation2.setTotalAmount(new BigDecimal("2.50"));
        reservation2.setStatus(Reservation.ReservationStatus.CONFIRMED);

        testReservations = Arrays.asList(reservation1, reservation2);
    }

    @Test
    void exportReservations_ShouldReturnExcelBytes() {
        // Given
        Page<Reservation> page = new PageImpl<>(testReservations);
        when(reservationRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // When
        byte[] result = exportService.exportReservations(null, null, null);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(reservationRepository).findAll(any(PageRequest.class));
    }

    @Test
    void exportReservations_WithFilters_ShouldReturnFilteredExcelBytes() {
        // Given
        LocalDateTime from = LocalDateTime.now().minusDays(3);
        LocalDateTime to = LocalDateTime.now();
        Reservation.ReservationStatus status = Reservation.ReservationStatus.COMPLETED;
        
        Page<Reservation> page = new PageImpl<>(testReservations);
        when(reservationRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // When
        byte[] result = exportService.exportReservations(from, to, status);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(reservationRepository).findAll(any(PageRequest.class));
    }

    @Test
    void exportReservations_WithEmptyData_ShouldReturnExcelWithHeaders() {
        // Given
        Page<Reservation> emptyPage = new PageImpl<>(Arrays.asList());
        when(reservationRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        // When
        byte[] result = exportService.exportReservations(null, null, null);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(reservationRepository).findAll(any(PageRequest.class));
    }
}
