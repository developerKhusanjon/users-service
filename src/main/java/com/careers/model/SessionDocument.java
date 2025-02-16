package com.careers.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redis.om.spring.annotations.Indexed;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@RedisHash("sessions")
public class SessionDocument {
    @Id
    private String sessionId;
    
    @Indexed
    private String userId;
    
    private String deviceFingerprint;
    private String ipAddress;
    private Instant expiresAt;
    
    @JsonIgnore
    private String refreshTokenHash;
}