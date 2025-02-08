package com.careers.repository;

import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
public class UserDocumentRepository {
    
    private final ReactiveRedisTemplate<String, UserDocument> redisTemplate;
    private static final String USER_PREFIX = "user:doc:";

    public Mono<Boolean> saveUserDocument(UserDocument user) {
        return redisTemplate.opsForJson().set(
            USER_PREFIX + user.getUserId(), 
            user, 
            Duration.ofDays(7)
        );
    }

    public Mono<UserDocument> findByEmail(String email) {
        return redisTemplate.opsForJson().get(USER_PREFIX + "email:" + email);
    }

    @Scheduled(fixedRate = 30000)
    public void syncSearchIndex() {
        redisTemplate.execute(RedisScript.of(
            "FT.CREATE users_idx ON JSON PREFIX 1 user:doc: SCHEMA $.email AS email TEXT $.phone AS phone TAG"
        )).subscribe();
    }
}