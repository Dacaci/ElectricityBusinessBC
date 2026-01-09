package com.eb.eb_backend.config;

import com.eb.eb_backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Active @PreAuthorize
public class SimpleSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SimpleSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Autoriser explicitement OPTIONS (preflight CORS) - doit être avant anyRequest()
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                // Autoriser les endpoints d'authentification
                .requestMatchers("/api/auth/**").permitAll()
                // Autoriser la création de compte
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/users").permitAll()
                // Autoriser la consultation publique des stations (pour la carte) - GET uniquement
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/stations/**").permitAll()
                // Autoriser l'accès aux fichiers médias uploadés (pour afficher les images)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/medias/file/**").permitAll()
                // Autoriser l'upload de médias (nécessite authentification mais géré par @PreAuthorize)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/medias/upload").authenticated()
                // Les autres endpoints nécessitent l'authentification (gérés par @PreAuthorize)
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // IMPORTANT: Pour les cookies HTTPOnly, on ne peut pas utiliser "*" avec allowCredentials=true
        // On autorise toutes les origines avec setAllowedOriginPatterns au lieu de setAllowedOrigins
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        // IMPORTANT: Avec allowCredentials=true, on ne peut pas utiliser "*" pour les headers
        // Il faut lister explicitement les headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-Requested-With",
            "Cookie"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "Set-Cookie"));
        // IMPORTANT: Activer les credentials pour envoyer les cookies HTTPOnly
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}



