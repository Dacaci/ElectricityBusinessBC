package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.CreateReservationDto;
import com.eb.eb_backend.dto.ReservationDto;
import com.eb.eb_backend.service.ExportService;
import com.eb.eb_backend.service.ReceiptService;
import com.eb.eb_backend.service.ReservationService;
import com.eb.eb_backend.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReservationController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ReceiptService receiptService;

    @MockBean
    private ExportService exportService;

    @MockBean
    private SecurityUtil securityUtil;

    @MockBean
    private com.eb.eb_backend.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateReservationEndpoint() throws Exception {
        CreateReservationDto createDto = new CreateReservationDto();
        createDto.setStationId(1L);
        createDto.setStartTime(LocalDateTime.now().plusDays(1));
        createDto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(1L);
        reservationDto.setTotalAmount(new BigDecimal("10.00"));

        when(securityUtil.getCurrentUserId(any())).thenReturn(1L);
        when(reservationService.createReservation(anyLong(), any(CreateReservationDto.class)))
                .thenReturn(reservationDto);

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto))
                .cookie(new jakarta.servlet.http.Cookie("JWT_TOKEN", "mock-token")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalAmount").value(10.00));
    }
}
