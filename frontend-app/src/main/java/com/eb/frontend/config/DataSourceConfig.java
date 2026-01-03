package com.eb.frontend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/eb}")
    private String jdbcUrl;

    @Value("${spring.datasource.username:eb}")
    private String username;

    @Value("${spring.datasource.password:eb}")
    private String password;

    @Value("${spring.datasource.hikari.maximum-pool-size:5}")
    private int maxPoolSize;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.datasource.enabled", havingValue = "true", matchIfMissing = true)
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setDriverClassName("org.postgresql.Driver");
        config.setConnectionTimeout(30000);
        config.setInitializationFailTimeout(-1);
        return new HikariDataSource(config);
    }
}

