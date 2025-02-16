package com.careers.repository;

import org.springframework.data.r2dbc.repository.Query;

import java.util.UUID;

public interface UserInfoRepository extends R2dbcRepository<UserInformation, UUID> {
    @Query("""
        INSERT INTO user_information 
        (user_id, first_name, last_name, title) 
        VALUES ($1, $2, $3, $4)
        ON CONFLICT (user_id) DO UPDATE
        SET first_name = $2, last_name = $3, title = $4
        RETURNING *
    """)
    Mono<UserInformation> upsertUserInfo(UUID userId, String firstName, String lastName, String title);
}