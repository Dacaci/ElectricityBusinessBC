package com.eb.frontend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;


/**
 * Service pour faire des appels HTTP vers l'API Backend
 * Le Frontend sert de proxy vers l'API Backend
 */
@Service
public class BackendProxyService {

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    private final RestTemplate restTemplate;

    public BackendProxyService() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setRequestFactory(
            new org.springframework.http.client.SimpleClientHttpRequestFactory() {{
                setConnectTimeout(30000);
                setReadTimeout(60000);
            }}
        );
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

        } catch (Exception e) {
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
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Erreur lors de la communication avec le backend: " + e.getMessage() + "\"}");
        }
    }
}

