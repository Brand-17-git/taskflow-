package com.taskflow.dtos;

import com.taskflow.entidades.EstadoTarea;
import jakarta.validation.constraints.NotNull;

public record EstadoTareaRequest(@NotNull EstadoTarea estado) {
}
