package com.taskflow.dtos;

import com.taskflow.entidades.Prioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * prioridad null -> MEDIA.
 * asignadoAId solo se usa en tareas de equipo (en las personales se ignora).
 */
public record TareaRequest(
        @NotBlank @Size(max = 150) String titulo,
        @Size(max = 500) String descripcion,
        Prioridad prioridad,
        LocalDate fechaLimite,
        Long asignadoAId
) {
}
