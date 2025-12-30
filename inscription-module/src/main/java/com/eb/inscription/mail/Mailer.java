package com.eb.inscription.mail;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.Properties;

public class Mailer {

    private static final String SUBJECT = "Electricity Business - Votre code de vérification";
    private static final int SSL_PORT = 465;

    private final Session session;
    private final String from;
    private final boolean requiresAuth;

    public Mailer(String host, int port, String from, String username, String password) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        this.requiresAuth = (username != null && !username.isBlank() &&
                password != null && !password.isBlank());
        this.from = from;

        if (requiresAuth) {
            configureAuth(props, host, port, username, password);
        } else {
            props.put("mail.smtp.auth", "false");
            this.session = Session.getInstance(props);
        }
    }

    public Mailer(String host, int port, String from) {
        this(host, port, from, null, null);
    }

    private void configureAuth(Properties props, String host, int port, String username, String password) {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        if (host.contains("gmail.com")) {
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        }

        if (port == SSL_PORT) {
            props.put("mail.smtp.socketFactory.port", String.valueOf(SSL_PORT));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.ssl.enable", "true");
        }

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendCode(String to, String code) throws MessagingException {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(SUBJECT);

            MimeMultipart multipart = new MimeMultipart("alternative");

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(buildTextContent(code), "utf-8");
            multipart.addBodyPart(textPart);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(buildHtmlContent(code), "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            msg.setContent(multipart);
            Transport.send(msg);
            System.out.println("✅ Email de vérification envoyé avec succès à : " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email à " + to + " : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private String buildHtmlContent(String code) {
        return String.format(
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                "<h2 style=\"color: #1E40AF;\">Electricity Business</h2>" +
                "<p>Bonjour,</p>" +
                "<p>Merci de vous être inscrit sur Electricity Business.</p>" +
                "<div style=\"background-color: #f0f9ff; border-left: 4px solid #1E40AF; padding: 15px; margin: 20px 0;\">" +
                "<p style=\"margin: 0; font-size: 24px; font-weight: bold; color: #1E40AF; text-align: center; letter-spacing: 5px;\">" +
                "%s" +
                "</p>" +
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
}
