package com.pm.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupRequestDTO(
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String roleId
) {
}
