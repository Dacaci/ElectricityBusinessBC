package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.LocationDto;
import com.eb.eb_backend.service.LocationService;
import com.eb.eb_backend.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    
    private final LocationService locationService;
    private final SecurityUtil securityUtil;
    
    @PostMapping
    public ResponseEntity<LocationDto> createLocation(
            @Valid @RequestBody LocationDto locationDto,
            HttpServletRequest request) {
        try {
            Long ownerId = securityUtil.getCurrentUserId(request);
            if (ownerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            LocationDto created = locationService.createLocation(ownerId, locationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {
        return locationService.getLocationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<LocationDto>> getLocationsByOwner(
            @PathVariable Long ownerId,
            Pageable pageable) {
        try {
            Page<LocationDto> locations = locationService.getLocationsByOwner(ownerId, pageable);
            return ResponseEntity.ok(locations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/owner/{ownerId}/active")
    public ResponseEntity<List<LocationDto>> getActiveLocationsByOwner(@PathVariable Long ownerId) {
        try {
            List<LocationDto> locations = locationService.getActiveLocationsByOwner(ownerId);
            return ResponseEntity.ok(locations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<LocationDto>> searchLocations(
            @RequestParam(required = false) String q,
            Pageable pageable) {
        Page<LocationDto> locations = locationService.searchLocations(q, pageable);
        return ResponseEntity.ok(locations);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationDto locationDto) {
        try {
            LocationDto updated = locationService.updateLocation(id, locationDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<LocationDto> activateLocation(@PathVariable Long id) {
        try {
            LocationDto activated = locationService.activateLocation(id);
            return ResponseEntity.ok(activated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<LocationDto> deactivateLocation(@PathVariable Long id) {
        try {
            LocationDto deactivated = locationService.deactivateLocation(id);
            return ResponseEntity.ok(deactivated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
