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
    
    public LocationDto createLocation(Long ownerId, LocationDto locationDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire non trouvé avec l'ID: " + ownerId));
        
        Location location = new Location();
        location.setOwner(owner);
        location.setLabel(locationDto.getLabel());
        // L'adresse sera gérée via addressEntity si nécessaire
        location.setLatitude(locationDto.getLatitude());
        location.setLongitude(locationDto.getLongitude());
        location.setDescription(locationDto.getDescription());
        location.setIsActive(locationDto.getIsActive() != null ? locationDto.getIsActive() : true);
        
        Location savedLocation = locationRepository.save(location);
        return new LocationDto(savedLocation);
    }
    
    @Transactional(readOnly = true)
    public Optional<LocationDto> getLocationById(Long id) {
        return locationRepository.findById(id)
                .map(LocationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<LocationDto> getLocationsByOwner(Long ownerId, Pageable pageable) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire non trouvé avec l'ID: " + ownerId));
        
        return locationRepository.findByOwner(owner, pageable)
                .map(LocationDto::new);
    }
    
    @Transactional(readOnly = true)
    public List<LocationDto> getActiveLocationsByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire non trouvé avec l'ID: " + ownerId));
        
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
    
    public LocationDto updateLocation(Long id, LocationDto locationDto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lieu non trouvé avec l'ID: " + id));
        
        location.setLabel(locationDto.getLabel());
        // L'adresse sera gérée via addressEntity si nécessaire
        location.setLatitude(locationDto.getLatitude());
        location.setLongitude(locationDto.getLongitude());
        location.setDescription(locationDto.getDescription());
        location.setIsActive(locationDto.getIsActive());
        
        Location savedLocation = locationRepository.save(location);
        return new LocationDto(savedLocation);
    }
    
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new IllegalArgumentException("Lieu non trouvé avec l'ID: " + id);
        }
        locationRepository.deleteById(id);
    }
    
    public LocationDto activateLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lieu non trouvé avec l'ID: " + id));
        
        location.setIsActive(true);
        Location savedLocation = locationRepository.save(location);
        return new LocationDto(savedLocation);
    }
    
    public LocationDto deactivateLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lieu non trouvé avec l'ID: " + id));
        
        location.setIsActive(false);
        Location savedLocation = locationRepository.save(location);
        return new LocationDto(savedLocation);
    }
}
