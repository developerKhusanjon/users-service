package com.careers.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Data
@Table("users")
public class User {
    @Id
    private UUID id;
    private String email;
    private String phone;
    private String hashedPassword;
    private boolean verified;
    private Instant createdAt;
    private Instant updatedAt;
}
