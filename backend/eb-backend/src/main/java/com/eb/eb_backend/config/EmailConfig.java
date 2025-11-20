package com.eb.eb_backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.mail.host", matchIfMissing = false)
    public JavaMailSender javaMailSender() {
        // Ce bean ne sera créé que si spring.mail.host est défini
        // Sinon, JavaMailSender sera null et EmailService s'adaptera
        return new JavaMailSenderImpl();
    }
}

