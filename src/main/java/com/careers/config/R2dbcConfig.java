package com.careers.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() {
        return ConnectionPoolBuilder.builder()
                .url("r2dbc:postgresql://postgres:5432/usersdb")
                .username("admin")
                .password("secret")
                .maxSize(200)
                .initialSize(20)
                .maxIdleTime(Duration.ofMinutes(15))
                .build();
    }
}