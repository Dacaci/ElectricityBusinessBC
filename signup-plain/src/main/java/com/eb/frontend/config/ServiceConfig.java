package com.eb.frontend.config;

import com.eb.signup.auth.EmailVerificationDao;
import com.eb.signup.mail.Mailer;
import com.eb.signup.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ServiceConfig {

    @Bean
    @ConditionalOnBean(DataSource.class)
    public UserDao userDao(DataSource dataSource) {
        return new UserDao(dataSource);
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    public EmailVerificationDao emailVerificationDao(DataSource dataSource) {
        return new EmailVerificationDao(dataSource);
    }

    @Bean
    public Mailer mailer(
            @Value("${spring.mail.host}") String host,
            @Value("${spring.mail.port}") int port,
            @Value("${spring.mail.from}") String from) {
        return new Mailer(host, port, from);
    }
}

