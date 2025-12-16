package com.eb.eb_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ResendEmailService {
    
    private static final String RESEND_API_URL = "https://api.resend.com/emails";
    private final String apiKey;
    private final String fromEmail;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public ResendEmailService(
            @Value("${resend.api.key:}") String apiKey,
            @Value("${resend.from.email:onboarding@resend.dev}") String fromEmail) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        log.info("ResendEmailService initialisé avec fromEmail: {}", fromEmail);
    }
    
    /**
     * Envoie un email de vérification avec un code OTP via l'API Resend
     * @param to Adresse email du destinataire
     * @param code Code OTP à 6 chiffres
     * @return true si l'email a été envoyé avec succès
     */
    public boolean sendVerificationEmail(String to, String code) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Resend API key not configured. Skipping email to: {}", to);
            log.info("Verification code for {}: {}", to, code);
            return false;
        }
        
        try {
            // Préparer le payload JSON pour Resend
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", fromEmail);
            payload.put("to", to);
            payload.put("subject", "Electricity Business - Votre code de vérification");
            
            // Contenu HTML
            String htmlContent = String.format(
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                "<h2 style=\"color: #1E40AF; margin-bottom: 20px;\">Electricity Business</h2>" +
                "<p>Bonjour,</p>" +
                "<p>Merci de vous être inscrit sur Electricity Business.</p>" +
                "<div style=\"background-color: #f0f9ff; border-left: 4px solid #1E40AF; padding: 20px; margin: 20px 0; text-align: center;\">" +
                "<p style=\"margin: 0; font-size: 32px; font-weight: bold; color: #1E40AF; letter-spacing: 8px;\">%s</p>" +
                "</div>" +
                "<p>Ce code est valable pendant <strong>15 minutes</strong>.</p>" +
                "<p>Si vous n'avez pas demandé ce code, vous pouvez ignorer cet email.</p>" +
                "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\">" +
                "<p style=\"color: #666; font-size: 12px;\">Cordialement,<br>L'équipe Electricity Business</p>" +
                "</div>" +
                "</body>" +
                "</html>",
                code
            );
            
            // Version texte
            String textContent = String.format(
                "Bonjour,\n\n" +
                "Merci de vous être inscrit sur Electricity Business.\n\n" +
                "Votre code de vérification est : %s\n\n" +
                "Ce code est valable pendant 15 minutes.\n\n" +
                "Si vous n'avez pas demandé ce code, vous pouvez ignorer cet email.\n\n" +
                "Cordialement,\n" +
                "L'équipe Electricity Business",
                code
            );
            
            payload.put("html", htmlContent);
            payload.put("text", textContent);
            
            // Convertir en JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            // Créer la requête HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RESEND_API_URL))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            // Envoyer la requête
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("✅ Email de vérification envoyé avec succès à : {} via Resend", to);
                return true;
            } else {
                log.error("❌ Échec d'envoi d'email Resend. Status: {}, Response: {}", 
                         response.statusCode(), response.body());
                return false;
            }
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi d'email Resend à {} : {}", to, e.getMessage(), e);
            return false;
        }
    }
}


