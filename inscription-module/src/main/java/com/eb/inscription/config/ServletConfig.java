package com.eb.inscription.config;

import com.eb.inscription.servlet.RegisterServlet;
import com.eb.inscription.servlet.VerifyServlet;
import com.eb.inscription.servlet.VerifySuccessServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour enregistrer les Servlets du module d'inscription (sans framework)
 * dans le contexte Spring Boot
 */
@Configuration
public class ServletConfig {

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5441/eb}")
    private String dbUrl;

    @Value("${spring.datasource.username:eb}")
    private String dbUsername;

    @Value("${spring.datasource.password:eb}")
    private String dbPassword;
    
    // Variables d'environnement Render pour la base de données
    @Value("${DB_URL:}")
    private String dbUrlEnv;
    
    @Value("${DB_HOST:}")
    private String dbHost;
    
    @Value("${DB_PORT:5441}")
    private String dbPort;
    
    @Value("${DB_NAME:eb}")
    private String dbName;
    
    @Value("${DB_USER:eb}")
    private String dbUserEnv;
    
    @Value("${DB_PASS:eb}")
    private String dbPassEnv;

    /**
     * Enregistre le RegisterServlet
     */
    @Bean
    public ServletRegistrationBean<RegisterServlet> registerServletRegistration() {
        ServletRegistrationBean<RegisterServlet> registration = new ServletRegistrationBean<>(
            new RegisterServlet(), "/register-servlet");
        registration.setName("RegisterServlet");
        registration.setLoadOnStartup(1);
        
        String finalDbUrl = dbUrlEnv;
        if (finalDbUrl == null || finalDbUrl.isEmpty()) {
            if (dbHost != null && !dbHost.isEmpty()) {
                finalDbUrl = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbName);
            } else {
                finalDbUrl = dbUrl;
            }
        }
        
        String finalDbUser = (dbUserEnv != null && !dbUserEnv.isEmpty()) ? dbUserEnv : dbUsername;
        String finalDbPass = (dbPassEnv != null && !dbPassEnv.isEmpty()) ? dbPassEnv : dbPassword;
        
        registration.addInitParameter("db.url", finalDbUrl);
        registration.addInitParameter("db.username", finalDbUser);
        registration.addInitParameter("db.password", finalDbPass);
        
        return registration;
    }

    /**
     * Enregistre le VerifyServlet
     */
    @Bean
    public ServletRegistrationBean<VerifyServlet> verifyServletRegistration() {
        ServletRegistrationBean<VerifyServlet> registration = new ServletRegistrationBean<>(
            new VerifyServlet(), "/verify-servlet");
        registration.setName("VerifyServlet");
        registration.setLoadOnStartup(2);
        
        String finalDbUrl = dbUrlEnv;
        if (finalDbUrl == null || finalDbUrl.isEmpty()) {
            if (dbHost != null && !dbHost.isEmpty()) {
                finalDbUrl = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbName);
            } else {
                finalDbUrl = dbUrl;
            }
        }
        
        String finalDbUser = (dbUserEnv != null && !dbUserEnv.isEmpty()) ? dbUserEnv : dbUsername;
        String finalDbPass = (dbPassEnv != null && !dbPassEnv.isEmpty()) ? dbPassEnv : dbPassword;
        
        registration.addInitParameter("db.url", finalDbUrl);
        registration.addInitParameter("db.username", finalDbUser);
        registration.addInitParameter("db.password", finalDbPass);
        
        return registration;
    }

    /**
     * Enregistre le VerifySuccessServlet
     */
    @Bean
    public ServletRegistrationBean<VerifySuccessServlet> verifySuccessServletRegistration() {
        ServletRegistrationBean<VerifySuccessServlet> registration = new ServletRegistrationBean<>(
            new VerifySuccessServlet(), "/verify-success-servlet");
        registration.setName("VerifySuccessServlet");
        registration.setLoadOnStartup(3);
        
        return registration;
    }
}

