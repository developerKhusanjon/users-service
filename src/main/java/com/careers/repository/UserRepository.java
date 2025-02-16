package com.careers.repository;

import com.careers.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends R2dbcRepository<User, UUID> {
    @Query("SELECT * FROM users WHERE email = $1 OR phone = $1")
    Mono<User> findByEmailOrPhone(String credential);

    @Query("""
    SELECT u.user_id, u.email, ui.first_name, ui.last_name
    FROM users u
    JOIN user_information ui ON u.user_id = ui.user_id
    WHERE u.user_id = $1
    FOR UPDATE SKIP LOCKED
    """)
    Mono<UserProjection> findUserDetails(UUID userId);
}