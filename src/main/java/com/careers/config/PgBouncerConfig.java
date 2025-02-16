package com.careers.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.time.Duration;

@Configuration
public class PgBouncerConfig {

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(
            new ClassPathResource("schema.sql")));
        return initializer;
    }

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${spring.r2dbc.url}") String url,
            @Value("${spring.r2dbc.username}") String user,
            @Value("${spring.r2dbc.password}") String password) {

        return ConnectionPoolBuilder.builder()
            .url(url.replace("r2dbc:pool:", ""))
            .username(user)
            .password(password)
            .maxSize(500)
            .initialSize(20)
            .maxIdleTime(Duration.ofMinutes(30))
            .build();
    }
}