package com.careers.model;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Document
public class UserDocument {
    @Id
    private String userId;

    @Indexed
    @Searchable
    private String email;

    @Indexed
    @Searchable
    private String phone;

    @Indexed
    private Instant lastLogin;
    
    private String hashedPassword;
    private boolean verified;
    private String firstName;
    private String lastName;
    private String title;
    private String avatarUrl;
}