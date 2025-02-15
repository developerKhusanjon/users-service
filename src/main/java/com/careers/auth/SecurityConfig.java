package com.careers.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(ex -> ex
                .pathMatchers("/auth/**").permitAll()
                .anyExchange().authenticated()
            )
            .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.HTTP_BASIC)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((exchange, e) -> 
                    Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED))
                )
                .accessDeniedHandler((exchange, e) -> 
                    Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN))
                )
            )
            .build();
    }

    @Bean
    public KeyPair keyPair() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            return gen.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate key pair", e);
        }
    }
}