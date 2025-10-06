package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.CreateReservationDto;
import com.eb.eb_backend.dto.ReservationDto;
import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    private final ReservationService reservationService;
    
    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(
            @RequestParam Long userId,
            @Valid @RequestBody CreateReservationDto createReservationDto) {
        try {
            ReservationDto reservationDto = reservationService.createReservation(userId, createReservationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservationDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<ReservationDto>> getAllReservations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Pageable pageable) {
        
        Page<ReservationDto> reservations;
        
        if (search != null && !search.trim().isEmpty()) {
            Reservation.ReservationStatus statusEnum = null;
            if (status != null && !status.trim().isEmpty()) {
                try {
                    statusEnum = Reservation.ReservationStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().build();
                }
            }
            reservations = reservationService.searchReservations(userId, stationId, statusEnum, search.trim(), pageable);
        } else if (userId != null) {
            reservations = reservationService.getReservationsByUser(userId, pageable);
        } else if (stationId != null) {
            reservations = reservationService.getReservationsByStation(stationId, pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            try {
                Reservation.ReservationStatus statusEnum = Reservation.ReservationStatus.valueOf(status.toUpperCase());
                reservations = reservationService.getReservationsByStatus(statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            reservations = reservationService.getAllReservations(pageable);
        }
        
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReservationDto>> getReservationsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        try {
            Page<ReservationDto> reservations = reservationService.getReservationsByUser(userId, pageable);
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/station/{stationId}")
    public ResponseEntity<Page<ReservationDto>> getReservationsByStation(
            @PathVariable Long stationId,
            Pageable pageable) {
        try {
            Page<ReservationDto> reservations = reservationService.getReservationsByStation(stationId, pageable);
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<ReservationDto>> getUpcomingUserReservations(@PathVariable Long userId) {
        try {
            List<ReservationDto> reservations = reservationService.getUpcomingUserReservations(userId);
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/station/{stationId}/upcoming")
    public ResponseEntity<List<ReservationDto>> getUpcomingStationReservations(@PathVariable Long stationId) {
        try {
            List<ReservationDto> reservations = reservationService.getUpcomingStationReservations(stationId);
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ReservationDto> confirmReservation(
            @PathVariable Long id,
            @RequestParam Long ownerId) {
        try {
            ReservationDto reservationDto = reservationService.confirmReservation(id, ownerId);
            return ResponseEntity.ok(reservationDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationDto> cancelReservation(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            ReservationDto reservationDto = reservationService.cancelReservation(id, userId);
            return ResponseEntity.ok(reservationDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<ReservationDto> completeReservation(
            @PathVariable Long id,
            @RequestParam Long ownerId) {
        try {
            ReservationDto reservationDto = reservationService.completeReservation(id, ownerId);
            return ResponseEntity.ok(reservationDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            reservationService.deleteReservation(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}


