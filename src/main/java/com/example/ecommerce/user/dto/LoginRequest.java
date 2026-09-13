package com.example.ecommerce.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Email required")
        @Email(message = "enter a valid email address.")
        String email,

        @NotBlank(message = "Password required")
        String password
) {}