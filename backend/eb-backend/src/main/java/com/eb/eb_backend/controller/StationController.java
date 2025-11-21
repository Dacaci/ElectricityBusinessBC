package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.StationDto;
import com.eb.eb_backend.dto.StationLocationDto;
import com.eb.eb_backend.service.OpenChargeMapService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {
    
    private final StationService stationService;
    private final SecurityUtil securityUtil;
    private final OpenChargeMapService openChargeMapService;
    
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
    
    @GetMapping("/external/france")
    public ResponseEntity<List<Map<String, Object>>> getFranceStationsFromOCM() {
        try {
            List<Map<String, Object>> stations = openChargeMapService.getAllFranceStations();
            return ResponseEntity.ok(stations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/external")
    public ResponseEntity<List<Map<String, Object>>> getStationsFromOCM(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(required = false, defaultValue = "10") Integer distance,
            @RequestParam(required = false, defaultValue = "100") Integer maxResults) {
        try {
            // Limiter maxResults pour éviter les problèmes de mémoire
            int limitedMaxResults = Math.min(maxResults != null ? maxResults : 100, 200);
            // Limiter aussi la distance pour éviter trop de résultats
            int limitedDistance = Math.min(distance != null ? distance : 10, 50);
            
            List<Map<String, Object>> stations = openChargeMapService.getChargingStations(
                latitude, longitude, limitedDistance, limitedMaxResults);
            return ResponseEntity.ok(stations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    
    @PatchMapping("/{id}")
    public ResponseEntity<StationDto> patchStation(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> updates) {
        try {
            // Si on modifie uniquement isActive
            if (updates.containsKey("isActive") && updates.size() == 1) {
                Boolean isActive = (Boolean) updates.get("isActive");
                StationDto updated = isActive ? 
                    stationService.activateStation(id) : 
                    stationService.deactivateStation(id);
                return ResponseEntity.ok(updated);
            }
            // Si on modifie uniquement hourlyRate
            if (updates.containsKey("hourlyRate") && updates.size() == 1) {
                Object rateObj = updates.get("hourlyRate");
                java.math.BigDecimal hourlyRate = null;
                if (rateObj instanceof Number) {
                    hourlyRate = java.math.BigDecimal.valueOf(((Number) rateObj).doubleValue());
                }
                if (hourlyRate != null) {
                    StationDto updated = stationService.updateHourlyRate(id, hourlyRate);
                    return ResponseEntity.ok(updated);
                }
            }
            // Sinon on pourrait gérer d'autres champs partiels ici
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
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
