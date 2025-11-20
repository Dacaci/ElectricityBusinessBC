package com.eb.eb_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@electricitybusiness.com}")
    private String fromEmail;
    
    public void sendVerificationEmail(String to, String verificationCode) {
        if (mailSender == null) {
            log.warn("Email service is not configured. Skipping verification email to: {}", to);
            log.info("Verification code for {}: {}", to, verificationCode);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Electricity Business - Vérification de votre email");
            message.setText("Bonjour,\n\n" +
                    "Merci de vous être inscrit sur Electricity Business.\n\n" +
                    "Votre code de vérification est : " + verificationCode + "\n\n" +
                    "Veuillez entrer ce code sur la page de vérification.\n\n" +
                    "Cordialement,\n" +
                    "L'équipe Electricity Business");
            
            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", to, e.getMessage());
        }
    }
    
    public void sendWelcomeEmail(String to, String firstName) {
        if (mailSender == null) {
            log.warn("Email service is not configured. Skipping welcome email to: {}", to);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Bienvenue sur Electricity Business !");
            message.setText("Bonjour " + firstName + ",\n\n" +
                    "Votre email a été vérifié avec succès !\n\n" +
                    "Vous pouvez maintenant vous connecter et profiter de tous nos services.\n\n" +
                    "Cordialement,\n" +
                    "L'équipe Electricity Business");
            
            mailSender.send(message);
            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", to, e.getMessage());
        }
    }
}

