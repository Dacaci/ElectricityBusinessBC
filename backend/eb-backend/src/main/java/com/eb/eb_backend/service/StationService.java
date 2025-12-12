package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.StationDto;
import com.eb.eb_backend.dto.StationLocationDto;
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
        // Le propriétaire est maintenant déterminé via location.owner
        station.setLocation(location);
        station.setName(stationDto.getName());
        station.setHourlyRate(stationDto.getHourlyRate());
        station.setPlugType(stationDto.getPlugType() != null ? stationDto.getPlugType() : "TYPE_2S");
        station.setStatus(stationDto.getStatus() != null ? stationDto.getStatus() : com.eb.eb_backend.entity.StationStatus.ACTIVE);
        station.setPower(stationDto.getPower());
        station.setInstructions(stationDto.getInstructions());
        station.setOnFoot(stationDto.getOnFoot() != null ? stationDto.getOnFoot() : false);
        
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
            stationRepository.existsByOwnerAndName(station.getLocation().getOwner(), stationDto.getName())) {
            throw new IllegalArgumentException("Une borne avec ce nom existe déjà pour ce propriétaire");
        }
        
        station.setName(stationDto.getName());
        station.setHourlyRate(stationDto.getHourlyRate());
        station.setPlugType(stationDto.getPlugType() != null ? stationDto.getPlugType() : "TYPE_2S");
        station.setStatus(stationDto.getStatus());
        station.setPower(stationDto.getPower());
        station.setInstructions(stationDto.getInstructions());
        station.setOnFoot(stationDto.getOnFoot());
        
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
        
        station.setStatus(com.eb.eb_backend.entity.StationStatus.ACTIVE);
        Station savedStation = stationRepository.save(station);
        return new StationDto(savedStation);
    }
    
    public StationDto deactivateStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borne non trouvée avec l'ID: " + id));
        
        station.setStatus(com.eb.eb_backend.entity.StationStatus.INACTIVE);
        Station savedStation = stationRepository.save(station);
        return new StationDto(savedStation);
    }
    
    public StationDto updateHourlyRate(Long id, BigDecimal hourlyRate) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borne non trouvée avec l'ID: " + id));
        
        station.setHourlyRate(hourlyRate);
        Station savedStation = stationRepository.save(station);
        return new StationDto(savedStation);
    }
    
    @Transactional(readOnly = true)
    public List<StationLocationDto> getAllStationsForMap() {
        List<Station> stations = stationRepository.findByIsActiveTrue();
        return stations.stream()
                .map(StationLocationDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<StationLocationDto> getNearbyStations(BigDecimal latitude, BigDecimal longitude, Double radiusKm) {
        // Formule de Haversine pour calculer la distance entre deux points géographiques
        // Rayon de la Terre en kilomètres (utilisé dans le calcul)
        // double earthRadius = 6371.0; // Commenté car non utilisé dans cette implémentation simplifiée
        
        // Conversion du rayon de km en degrés (approximation)
        double latDelta = radiusKm / 111.0; // 1 degré de latitude ≈ 111 km
        double lonDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude.doubleValue())));
        
        // Créer une bounding box pour filtrer les stations
        BigDecimal minLat = latitude.subtract(BigDecimal.valueOf(latDelta));
        BigDecimal maxLat = latitude.add(BigDecimal.valueOf(latDelta));
        BigDecimal minLon = longitude.subtract(BigDecimal.valueOf(lonDelta));
        BigDecimal maxLon = longitude.add(BigDecimal.valueOf(lonDelta));
        
        // Récupérer toutes les stations actives dans la bounding box
        List<Station> stations = stationRepository.findByIsActiveTrue();
        
        return stations.stream()
                .filter(station -> {
                    BigDecimal stationLat = station.getLocation().getLatitude();
                    BigDecimal stationLon = station.getLocation().getLongitude();
                    
                    // Vérifier si la station est dans la bounding box
                    if (stationLat.compareTo(minLat) >= 0 && stationLat.compareTo(maxLat) <= 0 &&
                        stationLon.compareTo(minLon) >= 0 && stationLon.compareTo(maxLon) <= 0) {
                        
                        // Calculer la distance exacte avec la formule de Haversine
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
    
    /**
     * Rechercher les bornes disponibles par ville et période
     * Équivalent SQL:
     * SELECT * FROM bornes b 
     * JOIN lieux l ON l.id = b.id_lieux
     * LEFT JOIN reservations r ON r.id_borne = b.id 
     *   AND r.date_debut < endTime AND r.date_fin > startTime
     * WHERE b.city = city AND r.id IS NULL
     */
    @Transactional(readOnly = true)
    public List<StationDto> findAvailableStationsByCityAndPeriod(
            String city, 
            java.time.LocalDateTime startTime, 
            java.time.LocalDateTime endTime) {
        
        // Récupérer toutes les stations avec status ACTIVE
        // Note: La recherche par ville n'est plus possible sans l'entité Address
        // On retourne toutes les stations actives
        List<Station> stationsInCity = stationRepository.findAll().stream()
                .filter(station -> 
                    station.getStatus() == com.eb.eb_backend.entity.StationStatus.ACTIVE
                )
                .collect(Collectors.toList());
        
        // Filtrer les stations qui n'ont pas de réservations conflictuelles
        return stationsInCity.stream()
                .filter(station -> {
                    // Vérifier si la station a des réservations qui se chevauchent avec la période
                    boolean hasConflict = station.getReservations().stream()
                            .anyMatch(reservation -> {
                                // Une réservation est en conflit si :
                                // - Elle commence avant la fin de la période demandée
                                // - ET elle se termine après le début de la période demandée
                                return reservation.getStartTime().isBefore(endTime)
                                        && reservation.getEndTime().isAfter(startTime);
                            });
                    
                    return !hasConflict; // Disponible si pas de conflit
                })
                .map(StationDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Rechercher les bornes avec le statut spécifique pour un propriétaire
     * Équivalent SQL:
     * SELECT * FROM utilisateurs u
     * JOIN lieux l ON l.id_utilisateur = u.id
     * JOIN bornes b ON b.id_lieux = l.id
     * WHERE b.status = 'PENDING' AND u.id = ownerId
     */
    @Transactional(readOnly = true)
    public List<StationDto> findStationsByOwnerAndStatus(Long ownerId, com.eb.eb_backend.entity.StationStatus status) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire non trouvé avec l'ID: " + ownerId));
        
        return stationRepository.findByOwner(owner, Pageable.unpaged()).stream()
                .filter(station -> station.getStatus() == status)
                .map(StationDto::new)
                .collect(Collectors.toList());
    }
}
