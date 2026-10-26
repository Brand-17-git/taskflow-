package com.taskflow.dtos;

import com.taskflow.entidades.RolEnProyecto;
import jakarta.validation.constraints.NotNull;

/** Si rolEnProyecto es null se asigna MIEMBRO. */
public record MiembroRequest(
        @NotNull Long usuarioId,
        RolEnProyecto rolEnProyecto
) {
}
