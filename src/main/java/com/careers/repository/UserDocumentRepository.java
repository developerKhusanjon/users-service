package com.careers.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDocumentRepository {
    private final ReactiveRedisTemplate<String, UserDocument> redisTemplate;
    
    public Mono<Boolean> save(UserDocument user) {
        return redisTemplate.opsForJson().set(
            "user:doc:" + user.getUserId(),
            user,
            Duration.ofDays(7)
        );
    }

    public Mono<UserDocument> findById(String userId) {
        return redisTemplate.opsForJson().get("user:doc:" + userId);
    }

    public Flux<UserDocument> searchByEmail(String email) {
        return redisTemplate.opsForSearch().search(
            UserDocument.INDEX,
            "@email:" + email,
            UserDocument.class
        );
    }
}