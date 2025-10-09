package com.example.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record RegistrationRequest(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull @Email String email,
        @NotNull @Length(min = 8, max = 128) String password
) {

}
