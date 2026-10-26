package com.taskflow.dtos;

import com.taskflow.entidades.EstadoProyecto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** estado es opcional: si es null, el proyecto se crea como ACTIVO. */
public record ProyectoRequest(
        @NotBlank @Size(max = 100) String nombre,
        @Size(max = 500) String descripcion,
        EstadoProyecto estado
) {
}
