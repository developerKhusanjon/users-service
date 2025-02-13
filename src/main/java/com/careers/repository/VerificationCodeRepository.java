package com.careers.repository;

import com.careers.model.VerificationCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VerificationCodeRepository extends R2dbcRepository<VerificationCode, UUID> {
    @Query("SELECT * FROM verification_codes WHERE email_phone = $1 ORDER BY created_at DESC LIMIT 1")
    Mono<VerificationCode> findLatestByEmailPhone(String emailPhone);
}