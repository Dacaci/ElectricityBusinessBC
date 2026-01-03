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

    // Lire directement depuis l'environnement - pas de @Value qui peut poser problème
    private String backendUrl;

    private final RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        // PRIORITÉ 1: Variable d'environnement BACKEND_URL (c'est comme ça que Render le passe)
        String envBackendUrl = System.getenv("BACKEND_URL");
        log.info("🔍 BackendProxyService - BACKEND_URL (env): {}", envBackendUrl != null ? envBackendUrl : "NON DÉFINI");
        
        if (envBackendUrl != null && !envBackendUrl.isEmpty() && !envBackendUrl.equals("null")) {
            this.backendUrl = envBackendUrl.trim();
            log.info("✅ BackendProxyService - URL depuis BACKEND_URL (env): {}", backendUrl);
        } else {
            log.warn("⚠️ BACKEND_URL non défini dans les variables d'environnement, recherche dans les propriétés système...");
            // PRIORITÉ 2: Propriété système backend.url (passée via -D dans Dockerfile)
            String sysBackendUrl = System.getProperty("backend.url");
            log.info("🔍 BackendProxyService - backend.url (sys prop): {}", sysBackendUrl != null ? sysBackendUrl : "NON DÉFINI");
            
            if (sysBackendUrl != null && !sysBackendUrl.isEmpty()) {
                this.backendUrl = sysBackendUrl.trim();
                log.info("✅ BackendProxyService - URL depuis backend.url (sys prop): {}", backendUrl);
            } else {
                log.warn("⚠️ backend.url non défini dans les propriétés système, recherche dans application.properties...");
                // FALLBACK: application.properties (via @Value si besoin)
                // Mais on essaie de lire depuis application.properties manuellement
                try {
                    java.util.Properties props = new java.util.Properties();
                    java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties");
                    if (is != null) {
                        props.load(is);
                        String propUrl = props.getProperty("backend.url");
                        if (propUrl != null && !propUrl.isEmpty()) {
                            this.backendUrl = propUrl.trim();
                            log.info("✅ BackendProxyService - URL depuis application.properties: {}", backendUrl);
                        } else {
                            // En production, ne pas utiliser localhost - forcer l'utilisation de BACKEND_URL
                            String renderBackendUrl = System.getenv("BACKEND_URL");
                            if (renderBackendUrl != null && !renderBackendUrl.isEmpty()) {
                                this.backendUrl = renderBackendUrl.trim();
                                log.info("✅ BackendProxyService - URL depuis BACKEND_URL (fallback): {}", backendUrl);
                            } else {
                                this.backendUrl = "https://localhost:8080";
                                log.warn("⚠️ BackendProxyService - Fallback vers https://localhost:8080 (dev uniquement)");
                            }
                        }
                        is.close();
                    } else {
                        // En production, ne pas utiliser localhost
                        String renderBackendUrl = System.getenv("BACKEND_URL");
                        if (renderBackendUrl != null && !renderBackendUrl.isEmpty()) {
                            this.backendUrl = renderBackendUrl.trim();
                            log.info("✅ BackendProxyService - URL depuis BACKEND_URL (fallback): {}", backendUrl);
                        } else {
                            this.backendUrl = "https://localhost:8080";
                            log.warn("⚠️ BackendProxyService - Fallback vers https://localhost:8080 (dev uniquement, pas de application.properties)");
                        }
                    }
                } catch (Exception e) {
                    // En production, ne pas utiliser localhost
                    String renderBackendUrl = System.getenv("BACKEND_URL");
                    if (renderBackendUrl != null && !renderBackendUrl.isEmpty()) {
                        this.backendUrl = renderBackendUrl.trim();
                        log.info("✅ BackendProxyService - URL depuis BACKEND_URL (fallback après erreur): {}", backendUrl);
                    } else {
                        this.backendUrl = "https://localhost:8080";
                        log.warn("⚠️ BackendProxyService - Fallback vers https://localhost:8080 (dev uniquement, erreur lecture config): {}", e.getMessage());
                    }
                }
            }
        }
        
        log.info("🔧 BackendProxyService initialisé avec URL finale: {}", backendUrl);
        
        // Vérifier que l'URL backend n'est pas la même que le frontend
        String frontendUrl = System.getenv("RENDER_EXTERNAL_URL");
        log.info("🔍 Frontend URL (RENDER_EXTERNAL_URL): {}", frontendUrl != null ? frontendUrl : "NON DÉFINI");
        
        if (frontendUrl != null && backendUrl.equals(frontendUrl)) {
            log.error("❌ ERREUR CRITIQUE: Backend URL et Frontend URL sont identiques ! {}", backendUrl);
            log.error("   Cela va créer une boucle infinie ! Vérifiez la variable d'environnement BACKEND_URL dans render.yaml");
        } else {
            log.info("✅ Backend URL et Frontend URL sont différents - Configuration correcte");
            log.info("   - Frontend URL (Render): {}", frontendUrl != null ? frontendUrl : "Non défini");
            log.info("   - Backend URL (finale): {}", backendUrl);
        }
        
        // Vérifier que l'URL commence par https://
        if (!backendUrl.startsWith("https://")) {
            log.error("❌ ERREUR: Backend URL ne commence pas par https:// ! URL: {}", backendUrl);
            log.error("   Sur Render, toutes les URLs doivent être en HTTPS");
        }
        
        // Logger le type de factory utilisée pour RestTemplate
        if (restTemplate.getRequestFactory() instanceof org.springframework.http.client.HttpComponentsClientHttpRequestFactory) {
            log.info("✅ RestTemplate configuré avec HttpComponentsClientHttpRequestFactory (HTTPS optimisé)");
        } else {
            log.info("✅ RestTemplate configuré avec SimpleClientHttpRequestFactory");
        }
        
        // Test de connexion au backend au démarrage
        testBackendConnection();
    }
    
    /**
     * Teste la connexion au backend au démarrage
     */
    private void testBackendConnection() {
        try {
            log.info("🔍 Test de connexion au backend: {}", backendUrl);
            String testUrl = backendUrl + "/api/test";
            org.springframework.http.ResponseEntity<String> response = restTemplate.getForEntity(testUrl, String.class);
            log.info("✅ Backend accessible - Status: {}", response.getStatusCode());
        } catch (Exception e) {
            log.warn("⚠️ Backend non accessible au démarrage: {} - Cela peut être normal si le backend est en sleep mode sur Render", e.getMessage());
        }
    }

    private final RestTemplate restTemplate;
    
    // Initialisation du RestTemplate avec une meilleure gestion HTTPS et timeouts pour Render
    {
        RestTemplate template = new RestTemplate();
        try {
            // Utiliser HttpComponentsClientHttpRequestFactory pour une meilleure gestion HTTPS
            // Configuration des timeouts pour Render (plan gratuit = sleep mode)
            org.apache.hc.core5.util.Timeout connectTimeout = org.apache.hc.core5.util.Timeout.ofSeconds(90);  // 90s pour connexion
            org.apache.hc.core5.util.Timeout responseTimeout = org.apache.hc.core5.util.Timeout.ofSeconds(180); // 180s pour réponse
            
            org.apache.hc.client5.http.config.RequestConfig requestConfig = org.apache.hc.client5.http.config.RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectTimeout)
                .setResponseTimeout(responseTimeout)
                .build();
            
            org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient = 
                org.apache.hc.client5.http.impl.classic.HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .evictExpiredConnections()
                    .evictIdleConnections(org.apache.hc.core5.util.TimeValue.ofSeconds(180))
                    .build();
            
            org.springframework.http.client.HttpComponentsClientHttpRequestFactory factory = 
                new org.springframework.http.client.HttpComponentsClientHttpRequestFactory(httpClient);
            
            template.setRequestFactory(factory);
            log.info("✅ RestTemplate configuré avec HttpComponentsClientHttpRequestFactory (timeouts: 90s connect, 180s response)");
        } catch (NoClassDefFoundError | Exception e) {
            log.warn("⚠️ HttpComponents non disponible ({}), fallback vers SimpleClientHttpRequestFactory", e.getClass().getSimpleName());
            // Fallback vers SimpleClientHttpRequestFactory si HttpComponents n'est pas disponible
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(90000);   // 90 secondes pour connexion (augmenté pour Render sleep mode)
            factory.setReadTimeout(180000);      // 180 secondes pour lecture (augmenté pour Render)
            template.setRequestFactory(factory);
            log.info("✅ RestTemplate configuré avec SimpleClientHttpRequestFactory (timeouts: 90s connect, 180s read)");
        }
        this.restTemplate = template;
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
     * Exécute une requête HTTP vers l'API Backend avec retry automatique
     */
    private ResponseEntity<String> executeRequest(HttpMethod method, String path, String body, HttpHeaders requestHeaders) {
        return executeRequestWithRetry(method, path, body, requestHeaders, 2); // 2 retries max
    }
    
    /**
     * Exécute une requête HTTP vers l'API Backend avec retry automatique
     */
    private ResponseEntity<String> executeRequestWithRetry(HttpMethod method, String path, String body, HttpHeaders requestHeaders, int maxRetries) {
            String url = backendUrl + path;
        
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    log.info("🔄 Retry {} pour {} {}", attempt, method, path);
                    // Attendre avant de réessayer (2 secondes par tentative)
                    Thread.sleep(2000 * attempt);
                } else {
                    log.info("🔄 Proxy: {} {} -> Backend URL: {}", method, path, url);
                }
            
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

                if (attempt > 0) {
                    log.info("✅ Requête réussie après {} tentative(s)", attempt + 1);
                }

            return ResponseEntity
                .status(response.getStatusCode())
                .headers(responseHeaders)
                .body(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
                log.warn("⚠️ Erreur HTTP du backend: {} {} - URL: {}", e.getStatusCode(), e.getMessage(), url);
            
            // Gestion spéciale pour les erreurs 502 (Bad Gateway) - backend non accessible
            if (e.getStatusCode().value() == 502) {
                log.error("❌ Erreur 502 Bad Gateway - Le backend n'est pas accessible à l'URL: {}", url);
                log.error("   Vérifiez que le backend est démarré sur Render et n'est pas en sleep mode");
                log.error("   Sur Render (plan gratuit), le premier appel peut prendre 30-90 secondes pour réveiller le service");
                
                // Retry si on a encore des tentatives
                if (attempt < maxRetries) {
                    log.info("🔄 Retry pour erreur 502...");
                    continue;
                }
                
                return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("{\"error\":\"Le backend n'est pas accessible (502 Bad Gateway). Sur Render (plan gratuit), le service peut être en veille. Le premier appel peut prendre jusqu'à 90 secondes pour le réveiller. Veuillez réessayer dans quelques instants.\",\"backendUrl\":\"" + url + "\"}");
            }
            
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
                String exceptionType = e.getClass().getSimpleName();
                String exceptionCause = e.getCause() != null ? e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage() : "null";
                
                // Si on a encore des tentatives, réessayer
                if (attempt < maxRetries) {
                    log.warn("⚠️ Tentative {} échouée: {} (Type: {}, Cause: {}) - Retry...", 
                        attempt + 1, errorMsg, exceptionType, exceptionCause);
                    continue; // Réessayer
                }
                
                // Plus de tentatives, retourner l'erreur
                log.error("❌ ResourceAccessException après {} tentative(s) - Type: {}, Cause: {}, Message: {}, URL: {}", 
                    maxRetries + 1, exceptionType, exceptionCause, errorMsg, fullUrl);
                
                if (errorMsg != null && (errorMsg.contains("Read timed out") || errorMsg.contains("Connection timed out") || errorMsg.contains("timeout"))) {
                    log.warn("⏱️ Timeout lors de la connexion au backend: {} (le backend sur Render peut être en cours de démarrage ou en sleep mode)", fullUrl);
                    return ResponseEntity
                        .status(HttpStatus.GATEWAY_TIMEOUT)
                        .body("{\"error\":\"Le backend met trop de temps à répondre. Sur Render (plan gratuit), le service peut être en veille et prendre jusqu'à 90s pour démarrer. Veuillez réessayer dans quelques secondes.\"}");
                } else if (errorMsg != null && errorMsg.contains("Connect timed out")) {
                    log.warn("⏱️ Connexion timeout au backend: {} (le service Render est peut-être en sleep mode)", fullUrl);
                    return ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body("{\"error\":\"Le backend ne répond pas. Sur Render (plan gratuit), le service peut être en veille. Le premier appel peut prendre jusqu'à 90s pour le réveiller.\"}");
                } else {
                    log.error("❌ Impossible de se connecter au backend {}: {} (Type: {}, Cause: {})", fullUrl, errorMsg, exceptionType, exceptionCause);
                    return ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body("{\"error\":\"Backend non disponible: " + (errorMsg != null ? errorMsg : "Erreur de connexion") + "\"}");
                }
        } catch (Exception e) {
                // Si on a encore des tentatives, réessayer
                if (attempt < maxRetries) {
                    log.warn("⚠️ Tentative {} échouée avec exception: {} - Retry...", attempt + 1, e.getMessage());
                    continue; // Réessayer
                }
                
                // Plus de tentatives, retourner l'erreur
                log.error("❌ Erreur lors de la communication avec le backend après {} tentative(s): {}", maxRetries + 1, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Erreur lors de la communication avec le backend: " + e.getMessage() + "\"}");
        }
        }
        
        // Ne devrait jamais arriver ici, mais au cas où
        log.error("❌ Toutes les tentatives ont échoué pour {} {}", method, path);
        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body("{\"error\":\"Impossible de se connecter au backend après " + (maxRetries + 1) + " tentative(s)\"}");
    }
}

