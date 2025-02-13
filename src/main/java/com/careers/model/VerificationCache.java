package com.careers.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@RedisHash("verification_cache")
@TimeToLive(unit = TimeUnit.MINUTES)
public class VerificationCache {
    @Id
    private String emailPhone;
    private String code;
    private Instant createdAt;
}