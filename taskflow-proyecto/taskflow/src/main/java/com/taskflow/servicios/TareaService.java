package com.taskflow.servicios;

import com.taskflow.entidades.EstadoTarea;
import com.taskflow.entidades.Proyecto;
import com.taskflow.entidades.Tarea;
import com.taskflow.entidades.Usuario;
import com.taskflow.repositorios.MiembroProyectoRepository;
import com.taskflow.repositorios.TareaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;
    private final MiembroProyectoRepository miembroRepository;
    private final ProyectoService proyectoService;

    public TareaService(TareaRepository tareaRepository,
                        MiembroProyectoRepository miembroRepository,
                        ProyectoService proyectoService) {
        this.tareaRepository = tareaRepository;
        this.miembroRepository = miembroRepository;
        this.proyectoService = proyectoService;
    }

    /** La tarea pertenece y queda asignada al propio usuario; no tiene proyecto. */
    @Transactional
    public Tarea crearTareaPersonal(Tarea tarea, Usuario usuario) {
        tarea.setEsPersonal(true);
        tarea.setProyecto(null);
        tarea.setCreadoPor(usuario);
        tarea.setAsignadoA(usuario);
        return tareaRepository.save(tarea);
    }

    /** Solo el LIDER del proyecto. Si hay asignado, debe ser miembro del proyecto. */
    @Transactional
    public Tarea crearTareaEquipo(Tarea tarea, Usuario usuario, Long proyectoId) {
        Proyecto proyecto = proyectoService.obtenerProyecto(proyectoId);

        if (!proyectoService.esLider(proyecto, usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo el LIDER del proyecto puede crear tareas de equipo");
        }
        if (tarea.getAsignadoA() != null
                && !miembroRepository.existsByProyectoAndUsuario(proyecto, tarea.getAsignadoA())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El usuario asignado no es miembro del proyecto");
        }

        tarea.setEsPersonal(false);
        tarea.setProyecto(proyecto);
        tarea.setCreadoPor(usuario);
        return tareaRepository.save(tarea);
    }

    @Transactional(readOnly = true)
    public List<Tarea> listarTareasPersonales(Usuario usuario) {
        return tareaRepository.findByEsPersonalTrueAndCreadoPor(usuario);
    }

    /** Verifica que el usuario sea miembro del proyecto antes de listar. */
    @Transactional(readOnly = true)
    public List<Tarea> listarTareasDeProyecto(Long proyectoId, Usuario usuario) {
        Proyecto proyecto = proyectoService.verificarAcceso(proyectoId, usuario);
        return tareaRepository.findByProyecto(proyecto);
    }

    /** Solo el asignado o el LIDER del proyecto (en tareas personales, su creador). */
    @Transactional
    public Tarea actualizarEstadoTarea(Long tareaId, EstadoTarea nuevoEstado, Usuario usuario) {
        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tarea no encontrada: " + tareaId));

        boolean esCreador = tarea.getCreadoPor().getId().equals(usuario.getId());

        // Las tareas personales de otros usuarios no deben revelarse
        if (Boolean.TRUE.equals(tarea.getEsPersonal())) {
            if (!esCreador) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tarea no encontrada: " + tareaId);
            }
        } else {
            boolean esAsignado = tarea.getAsignadoA() != null
                    && tarea.getAsignadoA().getId().equals(usuario.getId());
            boolean esLider = tarea.getProyecto() != null
                    && proyectoService.esLider(tarea.getProyecto(), usuario);
            if (!esAsignado && !esLider) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Solo el asignado o el LIDER del proyecto puede cambiar el estado");
            }
        }

        tarea.setEstado(nuevoEstado);
        return tareaRepository.save(tarea);
    }
}
