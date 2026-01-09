package com.eb.eb_backend.service;

import com.eb.eb_backend.entity.*;
import com.eb.eb_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour l'export complet des données utilisateur (RGPD - Droit à la portabilité)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDataExportService {
    
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final StationRepository stationRepository;
    private final ReservationRepository reservationRepository;
    
    /**
     * Exporte toutes les données d'un utilisateur au format JSON
     * Conforme au RGPD - Article 20 (Droit à la portabilité des données)
     */
    public Map<String, Object> exportUserData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + userId));
        
        Map<String, Object> exportData = new HashMap<>();
        
        // 1. Données du profil utilisateur
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("email", user.getEmail());
        profile.put("firstName", user.getFirstName());
        profile.put("lastName", user.getLastName());
        profile.put("phone", user.getPhone());
        profile.put("dateOfBirth", user.getDateOfBirth());
        profile.put("address", user.getAddress());
        profile.put("postalCode", user.getPostalCode());
        profile.put("city", user.getCity());
        profile.put("status", user.getStatus().toString());
        profile.put("createdAt", user.getCreatedAt());
        profile.put("updatedAt", user.getUpdatedAt());
        exportData.put("profile", profile);
        
        // 2. Lieux de recharge possédés
        List<Location> locations = locationRepository.findByOwner(user, org.springframework.data.domain.Pageable.unpaged()).getContent();
        List<Map<String, Object>> locationsData = locations.stream().map(loc -> {
            Map<String, Object> locMap = new HashMap<>();
            locMap.put("id", loc.getId());
            locMap.put("label", loc.getLabel());
            locMap.put("description", loc.getDescription());
            locMap.put("latitude", loc.getLatitude());
            locMap.put("longitude", loc.getLongitude());
            locMap.put("isActive", loc.getIsActive());
            locMap.put("createdAt", loc.getCreatedAt());
            locMap.put("updatedAt", loc.getUpdatedAt());
            return locMap;
        }).collect(Collectors.toList());
        exportData.put("locations", locationsData);
        
        // 3. Bornes de recharge possédées
        List<Station> stations = stationRepository.findByOwner(user, org.springframework.data.domain.Pageable.unpaged()).getContent();
        List<Map<String, Object>> stationsData = stations.stream().map(station -> {
            Map<String, Object> stationMap = new HashMap<>();
            stationMap.put("id", station.getId());
            stationMap.put("name", station.getName());
            stationMap.put("locationId", station.getLocation().getId());
            stationMap.put("hourlyRate", station.getHourlyRate());
            stationMap.put("status", station.getStatus().toString());
            stationMap.put("power", station.getPower());
            stationMap.put("plugType", station.getPlugType());
            stationMap.put("instructions", station.getInstructions());
            stationMap.put("onFoot", station.getOnFoot());
            stationMap.put("createdAt", station.getCreatedAt());
            stationMap.put("updatedAt", station.getUpdatedAt());
            return stationMap;
        }).collect(Collectors.toList());
        exportData.put("stations", stationsData);
        
        // 4. Réservations effectuées (en tant que conducteur)
        List<Reservation> reservations = reservationRepository.findByUser(user, org.springframework.data.domain.Pageable.unpaged()).getContent();
        List<Map<String, Object>> reservationsData = reservations.stream().map(res -> {
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("id", res.getId());
            resMap.put("stationId", res.getStation().getId());
            resMap.put("stationName", res.getStation().getName());
            resMap.put("startTime", res.getStartTime());
            resMap.put("endTime", res.getEndTime());
            resMap.put("totalAmount", res.getTotalAmount());
            resMap.put("status", res.getStatus().toString());
            resMap.put("notes", res.getNotes());
            resMap.put("createdAt", res.getCreatedAt());
            resMap.put("updatedAt", res.getUpdatedAt());
            return resMap;
        }).collect(Collectors.toList());
        exportData.put("reservations", reservationsData);
        
        // 5. Métadonnées de l'export
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("exportDate", java.time.LocalDateTime.now());
        metadata.put("userId", userId);
        metadata.put("format", "JSON");
        metadata.put("rgpdCompliant", true);
        exportData.put("metadata", metadata);
        
        return exportData;
    }
}

