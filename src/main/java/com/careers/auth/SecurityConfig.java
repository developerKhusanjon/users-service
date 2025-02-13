package com.careers.auth;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
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