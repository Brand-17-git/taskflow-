package com.taskflow.dtos;

import com.taskflow.entidades.EstadoTarea;
import com.taskflow.entidades.Prioridad;
import com.taskflow.entidades.Tarea;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TareaResponse(
        Long id,
        String titulo,
        String descripcion,
        Prioridad prioridad,
        EstadoTarea estado,
        LocalDate fechaLimite,
        Boolean esPersonal,
        Long proyectoId,
        String asignadoA,
        String creadoPor,
        LocalDateTime fechaCreacion
) {
    public static TareaResponse de(Tarea t) {
        return new TareaResponse(
                t.getId(), t.getTitulo(), t.getDescripcion(), t.getPrioridad(), t.getEstado(),
                t.getFechaLimite(), t.getEsPersonal(),
                t.getProyecto() != null ? t.getProyecto().getId() : null,
                t.getAsignadoA() != null ? t.getAsignadoA().getNombreUsuario() : null,
                t.getCreadoPor().getNombreUsuario(),
                t.getFechaCreacion());
    }
}
