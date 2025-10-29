package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.StationLocationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenChargeMapService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${openchargemap.api.url:https://api.openchargemap.io/v3/poi}")
    private String apiUrl;
    
    /**
     * Récupère les bornes de recharge depuis OpenChargeMap dans une zone géographique
     * 
     * @param latitude Latitude du centre
     * @param longitude Longitude du centre
     * @param distance Distance en km
     * @param maxResults Nombre maximum de résultats (max 10000)
     * @return Liste des stations OpenChargeMap
     */
    public List<Map<String, Object>> getChargingStations(BigDecimal latitude, BigDecimal longitude, 
                                                          Integer distance, Integer maxResults) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .queryParam("distance", distance != null ? distance : 10)
                    .queryParam("maxresults", maxResults != null ? Math.min(maxResults, 10000) : 10000)
                    .queryParam("key", "bf16e0f8-2117-4dd4-b2a9-f5cdfcaa7eb4");
            
            String url = builder.toUriString();
            log.info("Appel à OpenChargeMap: {}", url);
            
            // L'API OpenChargeMap retourne directement un tableau
            List<Map<String, Object>> stations = restTemplate.getForObject(url, List.class);
            
            if (stations != null) {
                log.info("Nombre de stations récupérées depuis OpenChargeMap: {}", stations.size());
                return stations;
            } else {
                log.warn("Aucune station récupérée depuis OpenChargeMap");
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des stations OpenChargeMap", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Récupère toutes les bornes de France depuis OpenChargeMap
     * 
     * @return Liste des stations en France
     */
    public List<Map<String, Object>> getAllFranceStations() {
        // Centre approximatif de la France
        BigDecimal franceLat = new BigDecimal("46.6034");
        BigDecimal franceLng = new BigDecimal("1.8883");
        
        // Distance pour couvrir toute la France (env. 1000 km de rayon)
        return getChargingStations(franceLat, franceLng, 1000, 10000);
    }
}
