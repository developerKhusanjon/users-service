package com.careers.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerificationRequest {
    @NotBlank
    private String to; // Email or phone number
}