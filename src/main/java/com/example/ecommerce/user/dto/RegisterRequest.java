package com.example.ecommerce.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Email required")
        @Email(message = "enter a valid email address.")
        String email,

        @NotBlank(message = "Password required")
        @Size(min = 8, message = "The password must be at least 8 characters.")
        String password,

        @NotBlank(message = "Name  required.")
        String firstName,

        @NotBlank(message = "Surname required.")
        String lastName,

        @NotBlank(message = "Phone is required  ")
        @Size(min = 10, max = 10, message = "The phone must be 10 characters.\"")
        String phone
) {}