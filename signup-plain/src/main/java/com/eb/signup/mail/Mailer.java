package com.eb.signup.mail;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class Mailer {
  private final Session session;
  private final String from;
  private final boolean requiresAuth;
  private final String username;
  private final String password;
  
  /**
   * Constructeur pour Mailer avec support de l'authentification SMTP optionnelle
   * @param host Serveur SMTP (ex: smtp.gmail.com, sandbox.smtp.mailtrap.io, mailhog)
   * @param port Port SMTP (ex: 587, 465, 2525, 1025)
   * @param from Adresse email expéditrice
   * @param username Nom d'utilisateur SMTP (optionnel, null si pas d'auth)
   * @param password Mot de passe SMTP (optionnel, null si pas d'auth)
   */
  public Mailer(String host, int port, String from, String username, String password) {
    var props = new Properties();
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", String.valueOf(port));
    
    // Détecter si l'authentification est nécessaire
    this.requiresAuth = (username != null && !username.isBlank() && 
                        password != null && !password.isBlank());
    this.username = username;
    this.password = password;
    this.from = from;
    
    if (requiresAuth) {
      // Configuration pour l'authentification SMTP
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.starttls.required", "true");
      
      // Configuration spécifique pour Gmail
      if (host.contains("gmail.com")) {
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        // Gmail nécessite des propriétés supplémentaires
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
      }
      
      // Pour les ports SSL/TLS (465)
      if (port == 465) {
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.ssl.enable", "true");
      }
      
      // Authentification personnalisée
      session = Session.getInstance(props, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      });
    } else {
      // Pas d'authentification (ex: MailHog en développement)
      props.put("mail.smtp.auth", "false");
      session = Session.getInstance(props);
    }
  }
  
  /**
   * Constructeur simplifié sans authentification (pour compatibilité)
   */
  public Mailer(String host, int port, String from) {
    this(host, port, from, null, null);
  }
  
  /**
   * Envoie un code de vérification par email
   * @param to Adresse email du destinataire
   * @param code Code de vérification à envoyer
   * @throws MessagingException Si l'envoi échoue
   */
  public void sendCode(String to, String code) throws MessagingException {
    try {
      var msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(from));
      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      msg.setSubject("Electricity Business - Votre code de vérification");
      
      // Message HTML plus professionnel
      String htmlContent = String.format(
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
      
      // Créer un message multipart avec HTML et texte
      jakarta.mail.internet.MimeMultipart multipart = new jakarta.mail.internet.MimeMultipart("alternative");
      
      // Partie texte
      jakarta.mail.internet.MimeBodyPart textPart = new jakarta.mail.internet.MimeBodyPart();
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
      textPart.setText(textContent, "utf-8");
      multipart.addBodyPart(textPart);
      
      // Partie HTML
      jakarta.mail.internet.MimeBodyPart htmlPart = new jakarta.mail.internet.MimeBodyPart();
      htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
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
}
