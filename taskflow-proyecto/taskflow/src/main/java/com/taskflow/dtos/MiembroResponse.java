package com.taskflow.dtos;

import com.taskflow.entidades.MiembroProyecto;
import com.taskflow.entidades.RolEnProyecto;

import java.time.LocalDateTime;

public record MiembroResponse(
        Long id,
        Long proyectoId,
        Long usuarioId,
        String nombreUsuario,
        RolEnProyecto rolEnProyecto,
        LocalDateTime fechaUnion
) {
    public static MiembroResponse de(MiembroProyecto m) {
        return new MiembroResponse(m.getId(), m.getProyecto().getId(), m.getUsuario().getId(),
                m.getUsuario().getNombreUsuario(), m.getRolEnProyecto(), m.getFechaUnion());
    }
}
