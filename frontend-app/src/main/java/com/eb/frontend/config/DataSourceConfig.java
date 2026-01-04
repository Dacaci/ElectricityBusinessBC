package com.eb.frontend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

// DataSource désactivée - Le frontend ne doit PAS accéder directement à la base de données
// Tout passe par le backend API (architecture propre)
// Cette classe est gardée pour compatibilité mais le Bean n'est plus créé
@Configuration
public class DataSourceConfig {
    // DataSource Bean supprimé - architecture proxy pure
    // Le frontend sert uniquement de proxy vers le backend
    // Si vous avez besoin d'accéder aux données, passez par l'API backend
}

