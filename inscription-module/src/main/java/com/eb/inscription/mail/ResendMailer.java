package com.eb.inscription.mail;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ResendMailer {

    private static final String RESEND_API_URL = "https://api.resend.com/emails";
    private static final String DEFAULT_FROM_EMAIL = "onboarding@resend.dev";
    private static final String SUBJECT = "Electricity Business - Votre code de vérification";
    private static final int CONNECT_TIMEOUT_SECONDS = 10;
    private static final int REQUEST_TIMEOUT_SECONDS = 30;

    private final String apiKey;
    private final String fromEmail;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ResendMailer(String apiKey, String fromEmail) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail != null ? fromEmail : DEFAULT_FROM_EMAIL;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public void sendCode(String to, String code) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("⚠️ Resend API key not configured. Skipping email to: " + to);
            System.out.println("📧 Verification code for " + to + ": " + code);
            return;
        }

        try {
            Map<String, Object> payload = buildPayload(to, code);
            String jsonPayload = objectMapper.writeValueAsString(payload);
            HttpRequest request = buildHttpRequest(jsonPayload);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("✅ Email de vérification envoyé avec succès à : " + to + " via Resend");
            } else {
                System.err.println("❌ Échec d'envoi d'email Resend. Status: " + response.statusCode() +
                        ", Response: " + response.body());
                throw new Exception("Échec d'envoi d'email Resend. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi d'email Resend à " + to + " : " + e.getMessage());
            throw e;
        }
    }

    private Map<String, Object> buildPayload(String to, String code) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("from", fromEmail);
        payload.put("to", to);
        payload.put("subject", SUBJECT);
        payload.put("html", buildHtmlContent(code));
        payload.put("text", buildTextContent(code));
        return payload;
    }

    private String buildHtmlContent(String code) {
        return String.format(
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
    }

    private String buildTextContent(String code) {
        return String.format(
                "Bonjour,\n\n" +
                "Merci de vous être inscrit sur Electricity Business.\n\n" +
                "Votre code de vérification est : %s\n\n" +
                "Ce code est valable pendant 15 minutes.\n\n" +
                "Si vous n'avez pas demandé ce code, vous pouvez ignorer cet email.\n\n" +
                "Cordialement,\n" +
                "L'équipe Electricity Business",
                code
        );
    }

    private HttpRequest buildHttpRequest(String jsonPayload) {
        return HttpRequest.newBuilder()
                .uri(URI.create(RESEND_API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                .build();
    }
}
