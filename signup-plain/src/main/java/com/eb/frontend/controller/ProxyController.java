package com.eb.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;

/**
 * Proxy Controller pour rediriger toutes les requêtes /api/* vers le backend
 * Permet d'avoir le frontend et le backend sur le même domaine pour les cookies HTTPOnly
 */
@RestController
@RequestMapping("/api")
public class ProxyController {

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Proxy pour toutes les requêtes API
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS})
    public ResponseEntity<?> proxyRequest(
            HttpServletRequest request,
            @RequestBody(required = false) String body,
            HttpMethod method
    ) {
        try {
            // Construire l'URL du backend
            String path = request.getRequestURI().substring(request.getContextPath().length());
            String queryString = request.getQueryString();
            String targetUrl = backendUrl + path + (queryString != null ? "?" + queryString : "");

            // Copier les headers de la requête
            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // Ne pas copier les headers Host et Origin
                if (!headerName.equalsIgnoreCase("Host") && !headerName.equalsIgnoreCase("Origin")) {
                    headers.add(headerName, request.getHeader(headerName));
                }
            }

            // Créer l'entité de la requête
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // Faire la requête vers le backend
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    URI.create(targetUrl),
                    method,
                    entity,
                    String.class
            );

            // Copier les headers de la réponse
            HttpHeaders responseHeaders = new HttpHeaders();
            responseEntity.getHeaders().forEach((key, value) -> {
                // Copier tous les headers, y compris Set-Cookie
                responseHeaders.addAll(key, value);
            });

            // Retourner la réponse
            return ResponseEntity
                    .status(responseEntity.getStatusCode())
                    .headers(responseHeaders)
                    .body(responseEntity.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Gérer les erreurs HTTP (4xx, 5xx)
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            // Gérer les autres erreurs
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la communication avec le backend: " + e.getMessage());
        }
    }
}

