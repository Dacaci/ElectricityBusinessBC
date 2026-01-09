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
        // Extraire le chemin de la requête
        String path = request.getRequestURI().substring(request.getContextPath().length());
        // Logger pour diagnostic
        System.out.println("🔄 Proxy Frontend: " + request.getMethod() + " " + path + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
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
                if ("Set-Cookie".equalsIgnoreCase(key)) {
                    // Modifier les cookies pour qu'ils fonctionnent sur le domaine du frontend
                    for (String cookieValue : value) {
                        String modifiedCookie = modifyCookieForFrontendDomain(cookieValue);
                        responseHeaders.add("Set-Cookie", modifiedCookie);
                    }
                } else {
                    responseHeaders.addAll(key, value);
                }
            });
            return ResponseEntity
                .status(binaryResponse.getStatusCode())
                .headers(responseHeaders)
                .body(binaryResponse.getBody());
        }
        
        // Gérer les requêtes JSON normales
        ResponseEntity<String> response;
        try {
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
            System.out.println("✅ Proxy Frontend: Réponse du backend - Status: " + response.getStatusCode());
            
            // Intercepter et modifier les cookies Set-Cookie de la réponse pour les adapter au domaine du frontend
            HttpHeaders responseHeaders = new HttpHeaders();
            response.getHeaders().forEach((key, value) -> {
                if ("Set-Cookie".equalsIgnoreCase(key)) {
                    // Modifier les cookies pour qu'ils fonctionnent sur le domaine du frontend
                    for (String cookieValue : value) {
                        // Extraire le nom et la valeur du cookie
                        String modifiedCookie = modifyCookieForFrontendDomain(cookieValue);
                        responseHeaders.add("Set-Cookie", modifiedCookie);
                    }
                } else if (!"Content-Encoding".equalsIgnoreCase(key)) {
                    // Ne pas forwarder Content-Encoding car RestTemplate décompresse automatiquement
                    // S'assurer que Content-Type est toujours présent pour les réponses JSON
                    if ("Content-Type".equalsIgnoreCase(key)) {
                        // Vérifier que le Content-Type est valide
                        String contentType = value != null && !value.isEmpty() ? value.get(0) : null;
                        if (contentType == null || !contentType.contains("application/json")) {
                            // Forcer application/json si absent ou incorrect
                            responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
                        } else {
                            responseHeaders.addAll(key, value);
                        }
                    } else {
                        responseHeaders.addAll(key, value);
                    }
                }
            });
            
            // S'assurer que Content-Type est toujours présent pour les réponses JSON
            if (!responseHeaders.containsKey("Content-Type")) {
                responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
            }
            
            return ResponseEntity
                .status(response.getStatusCode())
                .headers(responseHeaders)
                .body(response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Proxy Frontend: Erreur lors du proxy: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"error\":\"Erreur interne du proxy: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Endpoint de diagnostic pour vérifier la configuration du backend
     */
    @GetMapping("/diagnostic/backend-config")
    public ResponseEntity<String> getBackendConfig() {
        try {
            String backendUrl = backendProxyService.getBackendUrl();
            String envBackendUrl = System.getenv("BACKEND_URL");
            String sysBackendUrl = System.getProperty("backend.url");
            String frontendUrl = System.getenv("RENDER_EXTERNAL_URL");
            
            String config = String.format(
                "{\"backendUrl\":\"%s\",\"envBackendUrl\":\"%s\",\"sysBackendUrl\":\"%s\",\"frontendUrl\":\"%s\",\"isSame\":%s}",
                backendUrl != null ? backendUrl : "null",
                envBackendUrl != null ? envBackendUrl : "null",
                sysBackendUrl != null ? sysBackendUrl : "null",
                frontendUrl != null ? frontendUrl : "null",
                frontendUrl != null && backendUrl != null && frontendUrl.equals(backendUrl)
            );
            
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Modifie le cookie Set-Cookie pour qu'il fonctionne sur le domaine du frontend
     * Supprime le domaine du backend et ajuste les attributs SameSite et Secure si nécessaire
     */
    private String modifyCookieForFrontendDomain(String cookieValue) {
        if (cookieValue == null || cookieValue.isEmpty()) {
            return cookieValue;
        }
        
        // Supprimer le domaine du backend (Domain=electricity-business-backend-jvc9.onrender.com)
        // et s'assurer que SameSite=None et Secure=true sont présents pour cross-domain
        String modified = cookieValue;
        
        // Supprimer le domaine spécifique du backend
        modified = modified.replaceAll("(?i);\\s*Domain=[^;]+", "");
        
        // S'assurer que Secure est présent (obligatoire pour SameSite=None en HTTPS)
        if (!modified.toLowerCase().contains("secure")) {
            modified += "; Secure";
        }
        
        // S'assurer que SameSite=None est présent pour cross-domain
        if (!modified.toLowerCase().contains("samesite")) {
            modified += "; SameSite=None";
        } else {
            // Remplacer SameSite=Lax ou SameSite=Strict par SameSite=None
            modified = modified.replaceAll("(?i);\\s*SameSite=(Lax|Strict)", "; SameSite=None");
        }
        
        return modified;
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

