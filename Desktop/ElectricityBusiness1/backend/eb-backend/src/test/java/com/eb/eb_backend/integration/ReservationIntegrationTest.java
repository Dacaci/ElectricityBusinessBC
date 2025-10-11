package com.eb.eb_backend.integration;

import com.eb.eb_backend.dto.CreateReservationDto;
import com.eb.eb_backend.dto.CreateUserDto;
import com.eb.eb_backend.dto.LocationDto;
import com.eb.eb_backend.dto.StationDto;
import com.eb.eb_backend.entity.Reservation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration bout-en-bout pour le flux complet de réservation
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;
    private Long locationId;
    private Long stationId;

    @BeforeEach
    public void setup() throws Exception {
        // 1. Créer un utilisateur
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setFirstName("Jean");
        createUserDto.setLastName("Dupont");
        createUserDto.setEmail("jean.dupont.test@example.com");
        createUserDto.setPhone("0612345678");
        createUserDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createUserDto.setAddress("1 rue de Paris");
        createUserDto.setPostalCode("75001");
        createUserDto.setCity("Paris");
        createUserDto.setPassword("Password123");

        MvcResult userResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String userJson = userResult.getResponse().getContentAsString();
        userId = objectMapper.readTree(userJson).get("id").asLong();

        // 2. Créer un lieu
        LocationDto locationDto = new LocationDto();
        locationDto.setLabel("Test Location");
        locationDto.setAddress("10 Avenue des Champs-Élysées, 75008 Paris");
        locationDto.setLatitude(new BigDecimal("48.8698"));
        locationDto.setLongitude(new BigDecimal("2.3078"));
        locationDto.setDescription("Lieu de test");
        locationDto.setOwnerId(userId);

        MvcResult locationResult = mockMvc.perform(post("/api/locations")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String locationJson = locationResult.getResponse().getContentAsString();
        locationId = objectMapper.readTree(locationJson).get("id").asLong();

        // 3. Créer une borne
        StationDto stationDto = new StationDto();
        stationDto.setName("Test Station");
        stationDto.setHourlyRate(new BigDecimal("0.50"));
        stationDto.setPlugType("TYPE2S");
        stationDto.setOwnerId(userId);
        stationDto.setLocationId(locationId);

        MvcResult stationResult = mockMvc.perform(post("/api/stations")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String stationJson = stationResult.getResponse().getContentAsString();
        stationId = objectMapper.readTree(stationJson).get("id").asLong();
    }

    @Test
    public void testCompleteReservationFlow() throws Exception {
        // 1. Créer une réservation
        CreateReservationDto createReservationDto = new CreateReservationDto();
        createReservationDto.setStationId(stationId);
        createReservationDto.setStartTime(LocalDateTime.now().plusDays(1));
        createReservationDto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        MvcResult reservationResult = mockMvc.perform(post("/api/reservations")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(1.0)) // 2h * 0.50€
                .andReturn();

        String reservationJson = reservationResult.getResponse().getContentAsString();
        Long reservationId = objectMapper.readTree(reservationJson).get("id").asLong();

        // 2. Confirmer la réservation
        mockMvc.perform(put("/api/reservations/" + reservationId + "/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // 3. Vérifier que la réservation est confirmée
        mockMvc.perform(get("/api/reservations/" + reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // 4. Générer le reçu PDF
        mockMvc.perform(get("/api/reservations/" + reservationId + "/receipt.pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"));

        // 5. Terminer la réservation
        mockMvc.perform(put("/api/reservations/" + reservationId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    public void testReservationCancellation() throws Exception {
        // 1. Créer une réservation
        CreateReservationDto createReservationDto = new CreateReservationDto();
        createReservationDto.setStationId(stationId);
        createReservationDto.setStartTime(LocalDateTime.now().plusDays(1));
        createReservationDto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        MvcResult reservationResult = mockMvc.perform(post("/api/reservations")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String reservationJson = reservationResult.getResponse().getContentAsString();
        Long reservationId = objectMapper.readTree(reservationJson).get("id").asLong();

        // 2. Annuler la réservation
        mockMvc.perform(put("/api/reservations/" + reservationId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // 3. Vérifier que la réservation est annulée
        mockMvc.perform(get("/api/reservations/" + reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    public void testGetReservationsByUser() throws Exception {
        // 1. Créer plusieurs réservations
        for (int i = 0; i < 3; i++) {
            CreateReservationDto createReservationDto = new CreateReservationDto();
            createReservationDto.setStationId(stationId);
            createReservationDto.setStartTime(LocalDateTime.now().plusDays(i + 1));
            createReservationDto.setEndTime(LocalDateTime.now().plusDays(i + 1).plusHours(2));

            mockMvc.perform(post("/api/reservations")
                    .param("userId", userId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
                    .andExpect(status().isCreated());
        }

        // 2. Récupérer toutes les réservations de l'utilisateur
        mockMvc.perform(get("/api/reservations/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    public void testExportReservationsToExcel() throws Exception {
        // 1. Créer une réservation
        CreateReservationDto createReservationDto = new CreateReservationDto();
        createReservationDto.setStationId(stationId);
        createReservationDto.setStartTime(LocalDateTime.now().plusDays(1));
        createReservationDto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        mockMvc.perform(post("/api/reservations")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().isCreated());

        // 2. Exporter les réservations en Excel
        mockMvc.perform(get("/api/reservations/export.xlsx"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", 
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Test
    public void testSearchNearbyStations() throws Exception {
        // Rechercher les bornes proches de Paris (48.8566, 2.3522)
        mockMvc.perform(get("/api/stations/nearby")
                .param("latitude", "48.8566")
                .param("longitude", "2.3522")
                .param("radiusKm", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].id").value(stationId));
    }

    @Test
    public void testInvalidReservation_PastDate() throws Exception {
        // Tenter de créer une réservation dans le passé
        CreateReservationDto createReservationDto = new CreateReservationDto();
        createReservationDto.setStationId(stationId);
        createReservationDto.setStartTime(LocalDateTime.now().minusDays(1));
        createReservationDto.setEndTime(LocalDateTime.now().minusDays(1).plusHours(2));

        mockMvc.perform(post("/api/reservations")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidReservation_EndBeforeStart() throws Exception {
        // Tenter de créer une réservation avec fin avant début
        CreateReservationDto createReservationDto = new CreateReservationDto();
        createReservationDto.setStationId(stationId);
        createReservationDto.setStartTime(LocalDateTime.now().plusDays(1).plusHours(2));
        createReservationDto.setEndTime(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/reservations")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().isBadRequest());
    }
}






