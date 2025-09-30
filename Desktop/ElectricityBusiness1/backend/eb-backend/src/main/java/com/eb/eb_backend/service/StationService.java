package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.StationDto;
import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.LocationRepository;
import com.eb.eb_backend.repository.StationRepository;
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
public class StationService {
    
    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    
    public StationDto createStation(Long ownerId, StationDto stationDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire non trouvé avec l'ID: " + ownerId));
        
        Location location = locationRepository.findById(stationDto.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Lieu non trouvé avec l'ID: " + stationDto.getLocationId()));
        
        // Vérifier que le lieu appartient au propriétaire
        if (!location.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Le lieu n'appartient pas au propriétaire");
        }
        
        // Vérifier l'unicité du nom pour ce propriétaire
        if (stationRepository.existsByOwnerAndName(owner, stationDto.getName())) {
            throw new IllegalArgumentException("Une borne avec ce nom existe déjà pour ce propriétaire");
        }
        
        Station station = new Station();
        station.setOwner(owner);
        station.setLocation(location);
        station.setName(stationDto.getName());
        station.setHourlyRate(stationDto.getHourlyRate());
        station.setPlugType(stationDto.getPlugType());
        station.setIsActive(stationDto.getIsActive() != null ? stationDto.getIsActive() : true);
        
        Station savedStation = stationRepository.save(station);
        return new StationDto(savedStation);
    }
    
    @Transactional(readOnly = true)
    public Optional<StationDto> getStationById(Long id) {
        return stationRepository.findById(id)
                .map(StationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<StationDto> getStationsByOwner(Long ownerId, Pageable pageable) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire non trouvé avec l'ID: " + ownerId));
        
        return stationRepository.findByOwner(owner, pageable)
                .map(StationDto::new);
    }
    
    @Transactional(readOnly = true)
    public List<StationDto> getActiveStationsByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire non trouvé avec l'ID: " + ownerId));
        
        return stationRepository.findByOwnerAndIsActiveTrue(owner)
                .stream()
                .map(StationDto::new)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<StationDto> getStationsByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Lieu non trouvé avec l'ID: " + locationId));
        
        return stationRepository.findByLocationAndIsActiveTrue(location)
                .stream()
                .map(StationDto::new)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Page<StationDto> searchStations(String query, Pageable pageable) {
        return stationRepository.findBySearchQuery(query, pageable)
                .map(StationDto::new);
    }
    
    public StationDto updateStation(Long id, StationDto stationDto) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borne non trouvée avec l'ID: " + id));
        
        // Vérifier l'unicité du nom si changé
        if (!station.getName().equals(stationDto.getName()) && 
            stationRepository.existsByOwnerAndName(station.getOwner(), stationDto.getName())) {
            throw new IllegalArgumentException("Une borne avec ce nom existe déjà pour ce propriétaire");
        }
        
        station.setName(stationDto.getName());
        station.setHourlyRate(stationDto.getHourlyRate());
        station.setPlugType(stationDto.getPlugType());
        station.setIsActive(stationDto.getIsActive());
        
        Station savedStation = stationRepository.save(station);
        return new StationDto(savedStation);
    }
    
    public void deleteStation(Long id) {
        if (!stationRepository.existsById(id)) {
            throw new IllegalArgumentException("Borne non trouvée avec l'ID: " + id);
        }
        stationRepository.deleteById(id);
    }
    
    public StationDto activateStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borne non trouvée avec l'ID: " + id));
        
        station.setIsActive(true);
        Station savedStation = stationRepository.save(station);
        return new StationDto(savedStation);
    }
    
    public StationDto deactivateStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borne non trouvée avec l'ID: " + id));
        
        station.setIsActive(false);
        Station savedStation = stationRepository.save(station);
        return new StationDto(savedStation);
    }
}
