package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.StationDto;
import com.eb.eb_backend.dto.StationLocationDto;
import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.LocationRepository;
import com.eb.eb_backend.repository.ReservationRepository;
import com.eb.eb_backend.repository.StationRepository;
import com.eb.eb_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StationService {
    
    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ReservationRepository reservationRepository;
    
    public StationDto createStation(Long ownerId, StationDto dto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable: " + ownerId));
        
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Lieu introuvable: " + dto.getLocationId()));
        
        if (!location.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Lieu non autorisé");
        }
        
        if (stationRepository.existsByOwnerAndName(owner, dto.getName())) {
            throw new IllegalArgumentException("Nom déjà utilisé");
        }
        
        Station station = new Station();
        station.setLocation(location);
        station.setName(dto.getName());
        station.setHourlyRate(dto.getHourlyRate());
        station.setPlugType(dto.getPlugType() != null ? dto.getPlugType() : "TYPE_2S");
        station.setStatus(dto.getStatus() != null ? dto.getStatus() : com.eb.eb_backend.entity.StationStatus.ACTIVE);
        station.setPower(dto.getPower());
        station.setInstructions(dto.getInstructions());
        station.setOnFoot(dto.getOnFoot() != null ? dto.getOnFoot() : false);
        
        return new StationDto(stationRepository.save(station));
    }
    
    @Transactional(readOnly = true)
    public Optional<StationDto> getStationById(Long id) {
        return stationRepository.findById(id)
                .map(StationDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<StationDto> getStationsByOwner(Long ownerId, Pageable pageable) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable: " + ownerId));
        return stationRepository.findByOwner(owner, pageable).map(StationDto::new);
    }
    
    @Transactional(readOnly = true)
    public List<StationDto> getActiveStationsByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable: " + ownerId));
        return stationRepository.findByOwnerAndStatusActive(owner)
                .stream()
                .map(StationDto::new)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<StationDto> getStationsByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Lieu introuvable: " + locationId));
        return stationRepository.findByLocationAndStatus(location, com.eb.eb_backend.entity.StationStatus.ACTIVE)
                .stream()
                .map(StationDto::new)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Page<StationDto> searchStations(String query, Pageable pageable) {
        return stationRepository.findBySearchQuery(query, pageable)
                .map(StationDto::new);
    }
    
    public StationDto updateStation(Long id, StationDto dto) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borne introuvable: " + id));
        
        if (!station.getName().equals(dto.getName()) && 
            stationRepository.existsByOwnerAndName(station.getLocation().getOwner(), dto.getName())) {
            throw new IllegalArgumentException("Nom déjà utilisé");
        }
        
        station.setName(dto.getName());
        station.setHourlyRate(dto.getHourlyRate());
        station.setPlugType(dto.getPlugType() != null ? dto.getPlugType() : "TYPE_2S");
        station.setStatus(dto.getStatus());
        station.setPower(dto.getPower());
        station.setInstructions(dto.getInstructions());
        station.setOnFoot(dto.getOnFoot());
        
        return new StationDto(stationRepository.save(station));
    }
    
    public void deleteStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borne introuvable: " + id));
        
        // Vérifier s'il existe des réservations actives (PENDING ou CONFIRMED) pour cette borne
        // Même si une réservation est annulée, on garde l'historique donc on vérifie aussi
        long activeReservationsCount = reservationRepository.findByStation(station, Pageable.unpaged())
                .stream()
                .filter(r -> r.getStatus() == com.eb.eb_backend.entity.Reservation.ReservationStatus.PENDING ||
                            r.getStatus() == com.eb.eb_backend.entity.Reservation.ReservationStatus.CONFIRMED)
                .count();
        
        if (activeReservationsCount > 0) {
            throw new IllegalStateException("Impossible de supprimer une borne qui a des réservations actives (PENDING ou CONFIRMED)");
        }
        
        stationRepository.deleteById(id);
    }
    
    public StationDto activateStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borne introuvable: " + id));
        station.setStatus(com.eb.eb_backend.entity.StationStatus.ACTIVE);
        return new StationDto(stationRepository.save(station));
    }
    
    public StationDto deactivateStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borne introuvable: " + id));
        station.setStatus(com.eb.eb_backend.entity.StationStatus.INACTIVE);
        return new StationDto(stationRepository.save(station));
    }
    
    public StationDto updateHourlyRate(Long id, BigDecimal hourlyRate) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borne introuvable: " + id));
        station.setHourlyRate(hourlyRate);
        return new StationDto(stationRepository.save(station));
    }
    
    @Transactional(readOnly = true)
    public List<StationLocationDto> getAllStationsForMap() {
        List<Station> stations = stationRepository.findByStatus(com.eb.eb_backend.entity.StationStatus.ACTIVE);
        return stations.stream()
                .map(StationLocationDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<StationLocationDto> getNearbyStations(BigDecimal latitude, BigDecimal longitude, Double radiusKm) {
        double latDelta = radiusKm / 111.0;
        double lonDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude.doubleValue())));
        
        BigDecimal minLat = latitude.subtract(BigDecimal.valueOf(latDelta));
        BigDecimal maxLat = latitude.add(BigDecimal.valueOf(latDelta));
        BigDecimal minLon = longitude.subtract(BigDecimal.valueOf(lonDelta));
        BigDecimal maxLon = longitude.add(BigDecimal.valueOf(lonDelta));
        
        List<Station> stations = stationRepository.findByStatus(com.eb.eb_backend.entity.StationStatus.ACTIVE);
        
        return stations.stream()
                .filter(station -> {
                    BigDecimal stationLat = station.getLocation().getLatitude();
                    BigDecimal stationLon = station.getLocation().getLongitude();
                    
                    if (stationLat.compareTo(minLat) >= 0 && stationLat.compareTo(maxLat) <= 0 &&
                        stationLon.compareTo(minLon) >= 0 && stationLon.compareTo(maxLon) <= 0) {
                        
                        double distance = calculateHaversineDistance(
                            latitude.doubleValue(), longitude.doubleValue(),
                            stationLat.doubleValue(), stationLon.doubleValue()
                        );
                        
                        return distance <= radiusKm;
                    }
                    return false;
                })
                .map(StationLocationDto::new)
                .collect(Collectors.toList());
    }
    
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0; // Rayon de la Terre en kilomètres
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return earthRadius * c;
    }
    
    @Transactional(readOnly = true)
    public List<StationDto> findAvailableStationsByCityAndPeriod(
            String city, 
            java.time.LocalDateTime startTime, 
            java.time.LocalDateTime endTime) {
        
        List<Station> stationsInCity = stationRepository.findAll().stream()
                .filter(s -> s.getStatus() == com.eb.eb_backend.entity.StationStatus.ACTIVE)
                .collect(Collectors.toList());
        
        return stationsInCity.stream()
                .filter(station -> {
                    boolean hasConflict = station.getReservations().stream()
                            .anyMatch(r -> r.getStartTime().isBefore(endTime) && r.getEndTime().isAfter(startTime));
                    return !hasConflict;
                })
                .map(StationDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<StationDto> findStationsByOwnerAndStatus(Long ownerId, com.eb.eb_backend.entity.StationStatus status) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable: " + ownerId));
        return stationRepository.findByOwner(owner, Pageable.unpaged()).stream()
                .filter(s -> s.getStatus() == status)
                .map(StationDto::new)
                .collect(Collectors.toList());
    }
}
