package com.eb.frontend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;


/**
 * Service pour faire des appels HTTP vers l'API Backend
 * Le Frontend sert de proxy vers l'API Backend
 */
@Service
public class BackendProxyService {

    private static final Logger log = LoggerFactory.getLogger(BackendProxyService.class);

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;
    
    @PostConstruct
    public void init() {
        log.info("🔧 BackendProxyService initialisé avec backend.url: {}", backendUrl);
    }

    private final RestTemplate restTemplate;
    
    // Initialisation du RestTemplate avec une meilleure gestion HTTPS et timeouts pour Render
    {
        restTemplate = new RestTemplate();
        try {
            // Utiliser HttpComponentsClientHttpRequestFactory pour une meilleure gestion HTTPS
            org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient = 
                org.apache.hc.client5.http.impl.classic.HttpClients.custom()
                    .setConnectionTimeToLive(120, java.util.concurrent.TimeUnit.SECONDS)
                    .evictExpiredConnections()
                    .evictIdleConnections(120, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
            
            org.springframework.http.client.HttpComponentsClientHttpRequestFactory factory = 
                new org.springframework.http.client.HttpComponentsClientHttpRequestFactory(httpClient);
            factory.setConnectTimeout(java.time.Duration.ofSeconds(60));  // 60s pour connexion (Render sleep mode)
            factory.setConnectionRequestTimeout(java.time.Duration.ofSeconds(60));
            factory.setResponseTimeout(java.time.Duration.ofSeconds(120)); // 120s pour réponse
            
            restTemplate.setRequestFactory(factory);
            log.info("✅ RestTemplate configuré avec HttpComponentsClientHttpRequestFactory (HTTPS optimisé)");
        } catch (NoClassDefFoundError | Exception e) {
            // Fallback vers SimpleClientHttpRequestFactory si HttpComponents n'est pas disponible
            log.warn("⚠️ HttpComponents non disponible ({}), fallback vers SimpleClientHttpRequestFactory", e.getClass().getSimpleName());
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(60000);   // 60 secondes pour connexion (augmenté pour Render)
            factory.setReadTimeout(120000);      // 120 secondes pour lecture
            restTemplate.setRequestFactory(factory);
            log.info("✅ RestTemplate configuré avec SimpleClientHttpRequestFactory (timeouts augmentés)");
        }
    }

    /**
     * Fait un appel GET vers l'API Backend (retourne String pour JSON)
     */
    public ResponseEntity<String> get(String path, HttpHeaders requestHeaders) {
        return executeRequest(HttpMethod.GET, path, null, requestHeaders);
    }

    /**
     * Fait un appel GET vers l'API Backend (retourne byte[] pour fichiers binaires comme PDF)
     */
    public ResponseEntity<byte[]> getBinary(String path, HttpHeaders requestHeaders) {
        try {
            String url = backendUrl + path;
            log.info("🔄 Proxy Binary: GET {} -> Backend URL: {}", path, url);
            
            HttpHeaders headers = new HttpHeaders();
            if (requestHeaders != null) {
                requestHeaders.forEach((key, value) -> {
                    if (!key.equalsIgnoreCase("Host") && !key.equalsIgnoreCase("Origin")) {
                        headers.addAll(key, value);
                    }
                });
            }

            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                byte[].class
            );

            HttpHeaders responseHeaders = new HttpHeaders();
            response.getHeaders().forEach((key, value) -> {
                responseHeaders.addAll(key, value);
            });

            return ResponseEntity
                .status(response.getStatusCode())
                .headers(responseHeaders)
                .body(response.getBody());

        } catch (ResourceAccessException e) {
            log.error("❌ Impossible de se connecter au backend (binary) à {}: {}", backendUrl + path, e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(null);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération du fichier binaire: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Fait un appel POST vers l'API Backend
     */
    public ResponseEntity<String> post(String path, String body, HttpHeaders requestHeaders) {
        return executeRequest(HttpMethod.POST, path, body, requestHeaders);
    }

    /**
     * Fait un appel PUT vers l'API Backend
     */
    public ResponseEntity<String> put(String path, String body, HttpHeaders requestHeaders) {
        return executeRequest(HttpMethod.PUT, path, body, requestHeaders);
    }

    /**
     * Fait un appel DELETE vers l'API Backend
     */
    public ResponseEntity<String> delete(String path, HttpHeaders requestHeaders) {
        return executeRequest(HttpMethod.DELETE, path, null, requestHeaders);
    }

    /**
     * Fait un appel PATCH vers l'API Backend
     */
    public ResponseEntity<String> patch(String path, String body, HttpHeaders requestHeaders) {
        return executeRequest(HttpMethod.PATCH, path, body, requestHeaders);
    }

    /**
     * Exécute une requête HTTP vers l'API Backend
     */
    private ResponseEntity<String> executeRequest(HttpMethod method, String path, String body, HttpHeaders requestHeaders) {
        try {
            String url = backendUrl + path;
            log.info("🔄 Proxy: {} {} -> Backend URL: {}", method, path, url);
            
            // Copier les headers de la requête (sauf Host et Origin)
            HttpHeaders headers = new HttpHeaders();
            if (requestHeaders != null) {
                requestHeaders.forEach((key, value) -> {
                    if (!key.equalsIgnoreCase("Host") && !key.equalsIgnoreCase("Origin")) {
                        // Copier le header Cookie pour forwarder les JWT HttpOnly
                        headers.addAll(key, value);
                    }
                });
            }

            // Créer l'entité de la requête
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // Faire la requête
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                method,
                entity,
                String.class
            );

            // Copier les headers de la réponse (y compris Set-Cookie pour les JWT)
            HttpHeaders responseHeaders = new HttpHeaders();
            response.getHeaders().forEach((key, value) -> {
                responseHeaders.addAll(key, value);
            });

            return ResponseEntity
                .status(response.getStatusCode())
                .headers(responseHeaders)
                .body(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.warn("⚠️ Erreur HTTP du backend: {} {}", e.getStatusCode(), e.getMessage());
            HttpHeaders responseHeaders = new HttpHeaders();
            if (e.getResponseHeaders() != null) {
                e.getResponseHeaders().forEach((key, value) -> {
                    responseHeaders.addAll(key, value);
                });
            }
            return ResponseEntity
                .status(e.getStatusCode())
                .headers(responseHeaders)
                .body(e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            String errorMsg = e.getMessage();
            String fullUrl = backendUrl + path;
            if (errorMsg != null && (errorMsg.contains("Read timed out") || errorMsg.contains("Connection timed out"))) {
                log.warn("⏱️ Timeout lors de la connexion au backend: {} (le backend sur Render peut être en cours de démarrage)", fullUrl);
                return ResponseEntity
                    .status(HttpStatus.GATEWAY_TIMEOUT)
                    .body("{\"error\":\"Le backend met trop de temps à répondre. Sur Render (plan gratuit), le service peut mettre 30-60s à démarrer. Veuillez réessayer.\"}");
            } else if (errorMsg != null && errorMsg.contains("Connect timed out")) {
                log.warn("⏱️ Connexion timeout au backend: {} (le service Render est peut-être en sleep mode)", fullUrl);
                return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("{\"error\":\"Le backend ne répond pas. Sur Render (plan gratuit), le service peut être en veille. Le premier appel peut prendre 30-60s pour le réveiller.\"}");
            } else {
                log.error("❌ Impossible de se connecter au backend {}: {}", fullUrl, errorMsg);
                return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("{\"error\":\"Backend non disponible: " + (errorMsg != null ? errorMsg : "Erreur de connexion") + "\"}");
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la communication avec le backend: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Erreur lors de la communication avec le backend: " + e.getMessage() + "\"}");
        }
    }
}

