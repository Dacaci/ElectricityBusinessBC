package com.eb.frontend.controller;

import com.eb.frontend.service.BackendProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Controller proxy pour rediriger toutes les requêtes /api/* vers l'API Backend
 * Le Frontend sert de proxy, le client n'appelle plus directement l'API Backend
 */
@RestController
@RequestMapping("/api")
public class ApiProxyController {

    private final BackendProxyService backendProxyService;

    public ApiProxyController(BackendProxyService backendProxyService) {
        this.backendProxyService = backendProxyService;
    }

    /**
     * Proxy pour toutes les requêtes API (GET, POST, PUT, DELETE, PATCH)
     * Gère aussi les fichiers binaires (PDF, images, etc.)
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<?> proxyRequest(
            HttpServletRequest request,
            @RequestBody(required = false) String body
    ) {
        // Détecter si c'est une requête pour un fichier binaire (PDF, image, etc.)
        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean isBinaryRequest = path.endsWith(".pdf") || 
                                  path.contains("/medias/") ||
                                  request.getContentType() != null && request.getContentType().startsWith("multipart/");
        // Extraire le chemin de la requête (déjà fait ci-dessus)
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            path += "?" + queryString;
        }

        // Copier les headers de la requête (y compris Cookie pour les JWT HttpOnly)
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!headerName.equalsIgnoreCase("Host") && !headerName.equalsIgnoreCase("Origin")) {
                headers.add(headerName, request.getHeader(headerName));
            }
        }
        
        // S'assurer que le header Cookie est forwardé (pour les JWT HttpOnly)
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader != null && !cookieHeader.isEmpty()) {
            headers.set("Cookie", cookieHeader);
        }

        // Lire le body si présent
        if (body == null && (request.getMethod().equals("POST") || request.getMethod().equals("PUT") || request.getMethod().equals("PATCH"))) {
            body = readRequestBody(request);
        }

        // Appeler le service proxy selon la méthode HTTP
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        
        // Gérer les fichiers binaires (PDF, etc.)
        if (isBinaryRequest && method == HttpMethod.GET) {
            ResponseEntity<byte[]> binaryResponse = backendProxyService.getBinary(path, headers);
            HttpHeaders responseHeaders = new HttpHeaders();
            binaryResponse.getHeaders().forEach((key, value) -> {
                responseHeaders.addAll(key, value);
            });
            return ResponseEntity
                .status(binaryResponse.getStatusCode())
                .headers(responseHeaders)
                .body(binaryResponse.getBody());
        }
        
        // Gérer les requêtes JSON normales
        ResponseEntity<String> response;
        if (method == HttpMethod.GET) {
            response = backendProxyService.get(path, headers);
        } else if (method == HttpMethod.POST) {
            response = backendProxyService.post(path, body, headers);
        } else if (method == HttpMethod.PUT) {
            response = backendProxyService.put(path, body, headers);
        } else if (method == HttpMethod.DELETE) {
            response = backendProxyService.delete(path, headers);
        } else if (method == HttpMethod.PATCH) {
            response = backendProxyService.patch(path, body, headers);
        } else {
            return ResponseEntity.status(405).body("{\"error\":\"Method not allowed\"}");
        }

        return response;
    }

    /**
     * Lit le body de la requête HTTP
     */
    private String readRequestBody(HttpServletRequest request) {
        try {
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }
            return body.toString();
        } catch (IOException e) {
            return null;
        }
    }
}

