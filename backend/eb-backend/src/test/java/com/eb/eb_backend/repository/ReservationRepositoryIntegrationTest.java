package com.eb.eb_backend.repository;

import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.StationStatus;
import com.eb.eb_backend.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@org.junit.jupiter.api.Disabled("Nécessite Docker pour Testcontainers")
class ReservationRepositoryIntegrationTest {

    @Container
    @SuppressWarnings("resource") // Testcontainers ferme automatiquement via @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @AfterAll
    static void tearDown() {
        postgres.close();
    }

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    private User owner;
    private User client;
    private Location location;
    private Station station;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setFirstName("Owner");
        owner.setLastName("Test");
        owner.setEmail("owner@test.com");
        owner.setPasswordHash("$2a$10$hash");
        owner.setStatus(User.UserStatus.ACTIVE);
        owner = userRepository.save(owner);

        client = new User();
        client.setFirstName("Client");
        client.setLastName("Test");
        client.setEmail("client@test.com");
        client.setPasswordHash("$2a$10$hash");
        client.setStatus(User.UserStatus.ACTIVE);
        client = userRepository.save(client);

        location = new Location();
        location.setOwner(owner);
        location.setLabel("Test Location");
        location.setLatitude(new BigDecimal("48.8566"));
        location.setLongitude(new BigDecimal("2.3522"));
        location.setIsActive(true);
        location = locationRepository.save(location);

        station = new Station();
        station.setLocation(location);
        station.setName("Test Station");
        station.setHourlyRate(new BigDecimal("5.00"));
        station.setStatus(StationStatus.ACTIVE);
        station.setPlugType("TYPE_2S");
        station = stationRepository.save(station);
    }

    @Test
    void testExactOverlap() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1).plusHours(2);

        Reservation r = new Reservation();
        r.setUser(client);
        r.setStation(station);
        r.setStartTime(start);
        r.setEndTime(end);
        r.setTotalAmount(new BigDecimal("10.00"));
        r.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservationRepository.save(r);

        List<Reservation> result = reservationRepository.findConflictingReservations(station, start, end);

        assertThat(result).hasSize(1);
    }

    @Test
    void testNoConflict() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1).plusHours(2);

        List<Reservation> result = reservationRepository.findConflictingReservations(station, start, end);

        assertThat(result).isEmpty();
    }

    @Test
    void testIgnoresRefusedReservations() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1).plusHours(2);

        Reservation r = new Reservation();
        r.setUser(client);
        r.setStation(station);
        r.setStartTime(start);
        r.setEndTime(end);
        r.setTotalAmount(new BigDecimal("10.00"));
        r.setStatus(Reservation.ReservationStatus.REFUSED);
        reservationRepository.save(r);

        List<Reservation> result = reservationRepository.findConflictingReservations(station, start, end);

        assertThat(result).isEmpty();
    }
}
