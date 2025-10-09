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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReceiptService receiptService;

    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        // Créer des données de test
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        Location location = new Location();
        location.setId(1L);
        location.setAddress("123 Rue de la Paix, Paris");

        Station station = new Station();
        station.setId(1L);
        station.setName("Station Test");
        station.setHourlyRate(new BigDecimal("2.50"));
        station.setLocation(location);

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setUser(user);
        testReservation.setStation(station);
        testReservation.setStartTime(LocalDateTime.now().plusHours(1));
        testReservation.setEndTime(LocalDateTime.now().plusHours(3));
        testReservation.setTotalAmount(new BigDecimal("5.00"));
        testReservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
    }

    @Test
    void generateReservationReceiptPdf_ShouldReturnPdfBytes() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // When
        byte[] result = receiptService.generateReservationReceiptPdf(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(reservationRepository).findById(1L);
    }

    @Test
    void generateReservationReceiptPdf_ShouldThrowException_WhenReservationNotFound() {
        // Given
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> receiptService.generateReservationReceiptPdf(999L)
        );
        
        assertEquals("Réservation non trouvée avec l'ID: 999", exception.getMessage());
        verify(reservationRepository).findById(999L);
    }
}





