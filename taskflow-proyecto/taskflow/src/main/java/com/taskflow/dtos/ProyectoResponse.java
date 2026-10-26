package com.taskflow.dtos;

import com.taskflow.entidades.EstadoProyecto;
import com.taskflow.entidades.Proyecto;

import java.time.LocalDateTime;

public record ProyectoResponse(
        Long id,
        String nombre,
        String descripcion,
        EstadoProyecto estado,
        String creadoPor,
        LocalDateTime fechaCreacion
) {
    public static ProyectoResponse de(Proyecto p) {
        return new ProyectoResponse(p.getId(), p.getNombre(), p.getDescripcion(),
                p.getEstado(), p.getCreadoPor().getNombreUsuario(), p.getFechaCreacion());
    }
}
