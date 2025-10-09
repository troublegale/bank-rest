package com.example.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record UserRequest(
        @NotNull @NotBlank @Length(max = 64) String firstName,
        @NotNull @NotBlank @Length(max = 64) String lastName,
        @NotNull @Email @Length(max = 128) String email,
        @NotNull @Size(min = 1) List<String> roles
) {
}
