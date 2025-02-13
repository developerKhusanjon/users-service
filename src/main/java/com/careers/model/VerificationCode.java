package com.careers.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("verification_codes")
public class VerificationCode {
    @Id
    private UUID id;
    
    @Column("email_phone")
    private String emailPhone;
    
    @Column("code")
    private String code;
    
    @Column("expires_at")
    private Instant expiresAt;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("used_at")
    private Instant usedAt;
}