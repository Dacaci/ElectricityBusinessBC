package com.eb.eb_backend.service;

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
    
    @Value("${openchargemap.api.key:}")
    private String apiKey;
    
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
            // Limiter maxResults pour éviter les problèmes de mémoire (max 200)
            int limitedMaxResults = Math.min(maxResults != null ? maxResults : 100, 200);
            // Limiter aussi la distance (max 50 km)
            int limitedDistance = Math.min(distance != null ? distance : 10, 50);
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .queryParam("distance", limitedDistance)
                    .queryParam("maxresults", limitedMaxResults);
            
            // Ajouter la clé API seulement si elle est configurée
            if (apiKey != null && !apiKey.isEmpty()) {
                builder.queryParam("key", apiKey);
            }
            
            String url = builder.toUriString();
            
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
