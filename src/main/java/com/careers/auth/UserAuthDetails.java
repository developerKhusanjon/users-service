package com.careers.auth;

import java.io.Serializable;
import java.util.UUID;

public record UserAuthDetails(
    UUID id,
    String email, 
    String phone, 
    String hashedPassword
) implements Serializable {}