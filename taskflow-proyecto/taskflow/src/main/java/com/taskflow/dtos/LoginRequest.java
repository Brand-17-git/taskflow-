package com.taskflow.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String nombreUsuario,
        @NotBlank String contrasena
) {
}
