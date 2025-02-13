package com.careers.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return extractToken(exchange.getRequest())
            .flatMap(token -> jwtService.validateToken(token)
                .flatMap(user -> authenticateUser(exchange, user))
            )
            .onErrorResume(e -> chain.filter(exchange))
            .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<String> extractToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
            .filter(header -> header.startsWith("Bearer "))
            .map(header -> header.substring(7));
    }

    private Mono<Void> authenticateUser(ServerWebExchange exchange, User user) {
        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder
            .withAuthentication(new JwtAuthenticationToken(
                user,
                user.getAuthorities()
            )));
    }
}

