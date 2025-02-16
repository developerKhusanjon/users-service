package com.careers.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class JwtRedisValidator {
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            DecodedJWT jwt = JWT.decode(token);
            return redisTemplate.opsForValue().get("sessions:" + jwt.getId());
        })
        .flatMap(session -> 
            session != null ? Mono.just(true) : Mono.just(false)
        )
        .onErrorResume(e -> Mono.just(false));
    }
}