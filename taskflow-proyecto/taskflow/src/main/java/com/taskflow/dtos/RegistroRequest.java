package com.taskflow.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank @Size(min = 3, max = 50) String nombreUsuario,
        @NotBlank @Email String correo,
        @NotBlank @Size(min = 8) String contrasena
) {
}
