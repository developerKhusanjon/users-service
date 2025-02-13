package com.careers.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final KeyPair keyPair;
    private final ReactiveRedisTemplate<String, UserAuthDetails> redisTemplate;
    private final UserRepository userRepository;
    
    @Value("${jwt.access-expiration}")
    private Duration accessExpiration;
    
    @Value("${jwt.refresh-expiration}")
    private Duration refreshExpiration;

    public AuthResponse generateTokens(User user) {
        Instant now = Instant.now();
        String accessToken = Jwts.builder()
            .setSubject(user.getId().toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(accessExpiration)))
            .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
            .compact();

        String refreshToken = UUID.randomUUID().toString();
        
        UserAuthDetails authDetails = new UserAuthDetails(
            user.getId(),
            user.getEmail(),
            user.getPhone(),
            user.getHashedPassword()
        );

        return redisTemplate.opsForValue().set(
                "auth:" + user.getId(), 
                authDetails, 
                refreshExpiration
            )
            .thenReturn(new AuthResponse(
                accessToken, 
                refreshToken, 
                now.plus(accessExpiration)
            ))
            .block();
    }

    public Mono<User> validateToken(String token) {
        return Mono.fromCallable(() -> {
                Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(keyPair.getPublic())
                    .build()
                    .parseClaimsJws(token);
                
                return UUID.fromString(claims.getBody().getSubject());
            })
            .onErrorResume(e -> Mono.error(new JwtException("Invalid token")))
            .flatMap(this::getUserAuthDetails)
            .flatMap(authDetails -> validateTokenAgainstDetails(token, authDetails));
    }

    private Mono<UserAuthDetails> getUserAuthDetails(UUID userId) {
        return redisTemplate.opsForValue().get("auth:" + userId)
            .switchIfEmpty(Mono.defer(() -> 
                userRepository.findById(userId)
                    .flatMap(user -> {
                        UserAuthDetails details = new UserAuthDetails(
                            user.getId(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getHashedPassword()
                        );
                        return redisTemplate.opsForValue()
                            .set("auth:" + userId, details, refreshExpiration)
                            .thenReturn(details);
                    })
            ));
    }

    private Mono<User> validateTokenAgainstDetails(String token, UserAuthDetails details) {
        return userRepository.findById(details.id())
            .filter(user -> user.getHashedPassword().equals(details.hashedPassword()))
            .switchIfEmpty(Mono.error(new JwtException("Invalid credentials")));
    }
    
    public Mono<Void> invalidateTokens(UUID userId) {
        return redisTemplate.delete("auth:" + userId).then();
    }
}

