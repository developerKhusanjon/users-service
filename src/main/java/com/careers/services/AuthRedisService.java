package com.careers.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthRedisService {
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ReactiveRedisSearchOperations searchOps;

    public Mono<AuthResponse> signIn(SignInRequest request) {
        String searchQuery = request.isEmail() ? 
            "@email:" + request.credential() : 
            "@phone:{" + request.credential() + "}";
        
        return searchOps.search("users-idx", searchQuery)
            .singleOrEmpty()
            .switchIfEmpty(Mono.error(new BadCredentialsException()))
            .flatMap(doc -> validatePassword(doc, request.password()))
            .flatMap(user -> generateAndStoreTokens(user));
    }

    private Mono<UserDocument> validatePassword(SearchResult doc, String password) {
        return Mono.just(doc)
            .filter(d -> BCrypt.checkpw(password, d.getHashedPassword()))
            .switchIfEmpty(Mono.error(new BadCredentialsException()));
    }

    private Mono<AuthResponse> generateAndStoreTokens(UserDocument user) {
        String accessToken = JWT.create()
            .withSubject(user.getUserId())
            .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
            .sign(Algorithm.HMAC256(secret));

        SessionDocument session = new SessionDocument(
            UUID.randomUUID().toString(),
            user.getUserId(),
            deviceFingerprint,
            Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return redisTemplate.opsForValue()
            .set("sessions:" + session.getSessionId(), session)
            .thenReturn(new AuthResponse(accessToken, session.getSessionId()));
    }
}