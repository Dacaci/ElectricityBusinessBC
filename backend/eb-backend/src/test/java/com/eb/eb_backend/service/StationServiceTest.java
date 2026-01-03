package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.StationDto;
import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.StationStatus;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.LocationRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private StationService stationService;

    private User owner;
    private Location location;
    private Station station;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setStatus(User.UserStatus.ACTIVE);

        location = new Location();
        location.setId(1L);
        location.setOwner(owner);
        location.setLabel("Test Location");

        station = new Station();
        station.setId(1L);
        station.setLocation(location);
        station.setName("Test Station");
        station.setHourlyRate(new BigDecimal("5.00"));
        station.setStatus(StationStatus.ACTIVE);
    }

    @Test
    void testActivateStation() {
        station.setStatus(StationStatus.INACTIVE);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(stationRepository.save(any(Station.class))).thenAnswer(invocation -> {
            Station s = invocation.getArgument(0);
            s.setStatus(StationStatus.ACTIVE);
            return s;
        });

        StationDto result = stationService.activateStation(1L);

        assertNotNull(result);
        assertEquals(StationStatus.ACTIVE, result.getStatus());
    }

    @Test
    void testDeactivateStation() {
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(stationRepository.save(any(Station.class))).thenAnswer(invocation -> {
            Station s = invocation.getArgument(0);
            s.setStatus(StationStatus.INACTIVE);
            return s;
        });

        StationDto result = stationService.deactivateStation(1L);

        assertNotNull(result);
        assertEquals(StationStatus.INACTIVE, result.getStatus());
    }

    @Test
    void testDeleteStationWithReservations() {
        when(stationRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Cannot delete station with existing reservations"))
                .when(stationRepository).deleteById(1L);

        assertThrows(RuntimeException.class, () -> {
            stationService.deleteStation(1L);
        });
    }
}
