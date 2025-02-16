package com.careers.services;

import com.careers.model.UserDocument;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public Mono<UserDocument> preSignUp(SignUpRequest request) {
        UserDocument doc = new UserDocument();
        doc.setEmail(request.getEmail());
        doc.setHashedPassword(hashPassword(request.getPassword()));
        doc.setVerified(false);

        return redisTemplate.opsForValue()
            .set("users-doc:temp:" + request.getEmail(), doc, Duration.ofMinutes(10))
            .thenReturn(doc);
    }

    public Mono<UserDocument> completeSignUp(String verificationCode) {
        return redisTemplate.opsForValue()
            .getAndDelete("users-doc:temp:" + verificationCode)
            .cast(UserDocument.class)
            .flatMap(tempUser -> {
                tempUser.setVerified(true);
                return redisTemplate.opsForValue()
                    .set("users-doc:" + tempUser.getUserId(), tempUser);
            });
    }

    public Flux<Boolean> batchUserInsert(List<UserDocument> users) {
        return redisTemplate.execute(connection ->
                Flux.fromIterable(users)
                        .buffer(500)
                        .flatMap(batch -> {
                            RedisStringReactiveCommands<String, Object> commands = connection.stringCommands();
                            return Flux.fromIterable(batch)
                                    .flatMap(user -> commands.set(
                                            "users-doc:" + user.getUserId(),
                                            user
                                    ));
                        })
        );
    }
}