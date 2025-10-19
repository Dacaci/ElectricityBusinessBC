package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.StationDto;
import com.eb.eb_backend.dto.StationLocationDto;
import com.eb.eb_backend.service.StationService;
import com.eb.eb_backend.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {
    
    private final StationService stationService;
    private final SecurityUtil securityUtil;
    
    @PostMapping
    public ResponseEntity<StationDto> createStation(
            @Valid @RequestBody StationDto stationDto,
            HttpServletRequest request) {
        try {
            Long ownerId = securityUtil.getCurrentUserId(request);
            if (ownerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            StationDto created = stationService.createStation(ownerId, stationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StationDto> getStationById(@PathVariable Long id) {
        return stationService.getStationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<StationDto>> getStationsByOwner(
            @PathVariable Long ownerId,
            Pageable pageable) {
        try {
            Page<StationDto> stations = stationService.getStationsByOwner(ownerId, pageable);
            return ResponseEntity.ok(stations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/owner/{ownerId}/active")
    public ResponseEntity<List<StationDto>> getActiveStationsByOwner(@PathVariable Long ownerId) {
        try {
            List<StationDto> stations = stationService.getActiveStationsByOwner(ownerId);
            return ResponseEntity.ok(stations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<StationDto>> getStationsByLocation(@PathVariable Long locationId) {
        try {
            List<StationDto> stations = stationService.getStationsByLocation(locationId);
            return ResponseEntity.ok(stations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<StationDto>> searchStations(
            @RequestParam(required = false) String q,
            Pageable pageable) {
        Page<StationDto> stations = stationService.searchStations(q, pageable);
        return ResponseEntity.ok(stations);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StationDto> updateStation(
            @PathVariable Long id,
            @Valid @RequestBody StationDto stationDto) {
        try {
            StationDto updated = stationService.updateStation(id, stationDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("non trouvée")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        try {
            stationService.deleteStation(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/map")
    public ResponseEntity<List<StationLocationDto>> getStationsForMap() {
        List<StationLocationDto> stations = stationService.getAllStationsForMap();
        return ResponseEntity.ok(stations);
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<List<StationLocationDto>> getNearbyStations(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "10") Double radiusKm) {
        List<StationLocationDto> stations = stationService.getNearbyStations(latitude, longitude, radiusKm);
        return ResponseEntity.ok(stations);
    }
    
    /**
     * Rechercher les bornes disponibles par ville et période
     * Équivalent SQL du projet: 
     * SELECT * FROM bornes b JOIN lieux l ... LEFT JOIN reservations r ... WHERE r.id IS NULL
     */
    @GetMapping("/available")
    public ResponseEntity<List<StationDto>> getAvailableStations(
            @RequestParam String city,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        try {
            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startTime);
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endTime);
            
            List<StationDto> stations = stationService.findAvailableStationsByCityAndPeriod(city, start, end);
            return ResponseEntity.ok(stations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Rechercher les bornes par statut pour un propriétaire
     * Équivalent SQL: WHERE b.status = 'PENDING' AND u.id = ownerId
     */
    @GetMapping("/owner/{ownerId}/status/{status}")
    public ResponseEntity<List<StationDto>> getStationsByStatus(
            @PathVariable Long ownerId,
            @PathVariable String status) {
        try {
            com.eb.eb_backend.entity.StationStatus stationStatus = 
                com.eb.eb_backend.entity.StationStatus.valueOf(status.toUpperCase());
            List<StationDto> stations = stationService.findStationsByOwnerAndStatus(ownerId, stationStatus);
            return ResponseEntity.ok(stations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
