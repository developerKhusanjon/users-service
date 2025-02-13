package com.careers.repository;

import org.springframework.data.r2dbc.repository.Query;

public interface VerificationCacheRepository extends ReactiveCrudRepository<VerificationCache, String> {
    @Query("SELECT * FROM verification_cache WHERE emailPhone = $1")
    Mono<VerificationCache> findByEmailPhone(String emailPhone);
}