package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.LocationDto;
import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.LocationRepository;
import com.eb.eb_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {
    
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    
    public LocationDto createLocation(Long ownerId, LocationDto dto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable: " + ownerId));
        
        Location location = new Location();
        location.setOwner(owner);
        location.setLabel(dto.getLabel());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        location.setDescription(dto.getDescription());
        location.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        
        return new LocationDto(locationRepository.save(location));
    }
    
    @Transactional(readOnly = true)
    public Optional<LocationDto> getLocationById(Long id) {
        return locationRepository.findById(id)
                .map(LocationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<LocationDto> getLocationsByOwner(Long ownerId, Pageable pageable) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable: " + ownerId));
        return locationRepository.findByOwner(owner, pageable).map(LocationDto::new);
    }
    
    @Transactional(readOnly = true)
    public List<LocationDto> getActiveLocationsByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable: " + ownerId));
        return locationRepository.findByOwnerAndIsActiveTrue(owner)
                .stream()
                .map(LocationDto::new)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Page<LocationDto> searchLocations(String query, Pageable pageable) {
        return locationRepository.findBySearchQuery(query, pageable)
                .map(LocationDto::new);
    }
    
    public LocationDto updateLocation(Long id, LocationDto dto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lieu introuvable: " + id));
        
        location.setLabel(dto.getLabel());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        location.setDescription(dto.getDescription());
        location.setIsActive(dto.getIsActive());
        
        return new LocationDto(locationRepository.save(location));
    }
    
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new IllegalArgumentException("Lieu introuvable: " + id);
        }
        locationRepository.deleteById(id);
    }
    
    public LocationDto activateLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lieu introuvable: " + id));
        location.setIsActive(true);
        return new LocationDto(locationRepository.save(location));
    }
    
    public LocationDto deactivateLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lieu introuvable: " + id));
        location.setIsActive(false);
        return new LocationDto(locationRepository.save(location));
    }
}
