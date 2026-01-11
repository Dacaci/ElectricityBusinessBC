package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.CreateReservationDto;
import com.eb.eb_backend.dto.ReservationDto;
import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.service.ExportService;
import com.eb.eb_backend.service.ReceiptService;
import com.eb.eb_backend.service.ReservationService;
import com.eb.eb_backend.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    private final ReservationService reservationService;
    private final ReceiptService receiptService;
    private final ExportService exportService;
    private final SecurityUtil securityUtil;
    
    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(
            @Valid @RequestBody CreateReservationDto createReservationDto,
            HttpServletRequest request) {
        Long userId = securityUtil.getCurrentUserId(request);
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        ReservationDto reservationDto = reservationService.createReservation(userId, createReservationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDto);
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
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationDto>> getAllReservationsAdmin(Pageable pageable) {
        Page<ReservationDto> reservations = reservationService.getAllReservations(pageable);
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReservationDto>> getReservationsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<ReservationDto> reservations = reservationService.getReservationsByUser(userId, pageable);
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/station/{stationId}")
    public ResponseEntity<Page<ReservationDto>> getReservationsByStation(
            @PathVariable Long stationId,
            Pageable pageable) {
        Page<ReservationDto> reservations = reservationService.getReservationsByStation(stationId, pageable);
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<ReservationDto>> getUpcomingUserReservations(@PathVariable Long userId) {
        List<ReservationDto> reservations = reservationService.getUpcomingUserReservations(userId);
        return ResponseEntity.ok(reservations);
    }
    
    /**
     * Récupérer les réservations passées d'un utilisateur
     * Équivalent SQL: WHERE U.id = userId AND NOW() > r.date_fin
     */
    @GetMapping("/user/{userId}/past")
    public ResponseEntity<List<ReservationDto>> getPastUserReservations(@PathVariable Long userId) {
        List<ReservationDto> reservations = reservationService.getPastUserReservations(userId);
        return ResponseEntity.ok(reservations);
    }
    
    /**
     * Récupérer les réservations actuelles (en cours) d'un utilisateur
     * Équivalent SQL: WHERE date_debut <= NOW() AND date_fin >= NOW()
     */
    @GetMapping("/user/{userId}/current")
    public ResponseEntity<List<ReservationDto>> getCurrentUserReservations(@PathVariable Long userId) {
        List<ReservationDto> reservations = reservationService.getCurrentUserReservations(userId);
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/station/{stationId}/upcoming")
    public ResponseEntity<List<ReservationDto>> getUpcomingStationReservations(@PathVariable Long stationId) {
        List<ReservationDto> reservations = reservationService.getUpcomingStationReservations(stationId);
        return ResponseEntity.ok(reservations);
    }
    
    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationDto> confirmReservation(@PathVariable Long id, HttpServletRequest request) {
        Long userId = securityUtil.getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReservationDto reservationDto = reservationService.confirmReservation(id, userId);
        return ResponseEntity.ok(reservationDto);
    }
    
    @PutMapping("/{id}/refuse")
    public ResponseEntity<ReservationDto> refuseReservation(@PathVariable Long id, HttpServletRequest request) {
        Long userId = securityUtil.getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReservationDto reservationDto = reservationService.refuseReservation(id, userId);
        return ResponseEntity.ok(reservationDto);
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationDto> cancelReservation(@PathVariable Long id, HttpServletRequest request) {
        Long userId = securityUtil.getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReservationDto reservationDto = reservationService.cancelReservation(id, userId);
        return ResponseEntity.ok(reservationDto);
    }
    
    @PutMapping("/{id}/complete")
    public ResponseEntity<ReservationDto> completeReservation(@PathVariable Long id, HttpServletRequest request) {
        Long userId = securityUtil.getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReservationDto reservationDto = reservationService.completeReservation(id, userId);
        return ResponseEntity.ok(reservationDto);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id, HttpServletRequest request) {
        Long userId = securityUtil.getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        reservationService.deleteReservation(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/receipt.pdf")
    public ResponseEntity<byte[]> getReservationReceipt(@PathVariable Long id) {
        byte[] pdf = receiptService.generateReservationReceiptPdf(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "inline; filename=receipt-" + id + ".pdf")
                .body(pdf);
    }

    @GetMapping("/export.xlsx")
    public ResponseEntity<byte[]> exportReservations(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String status
    ) {
        Reservation.ReservationStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try { statusEnum = Reservation.ReservationStatus.valueOf(status.toUpperCase()); } catch (Exception ignored) {}
        }

        java.time.LocalDateTime fromDt = null;
        java.time.LocalDateTime toDt = null;
        try { if (from != null && !from.isBlank()) fromDt = java.time.LocalDateTime.parse(from); } catch (Exception ignored) {}
        try { if (to != null && !to.isBlank()) toDt = java.time.LocalDateTime.parse(to); } catch (Exception ignored) {}

        byte[] excel = exportService.exportReservations(fromDt, toDt, statusEnum);
        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=reservations.xlsx")
                .body(excel);
    }
}





