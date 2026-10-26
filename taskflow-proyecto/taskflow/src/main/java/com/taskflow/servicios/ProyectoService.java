package com.taskflow.servicios;

import com.taskflow.entidades.MiembroProyecto;
import com.taskflow.entidades.Proyecto;
import com.taskflow.entidades.Rol;
import com.taskflow.entidades.RolEnProyecto;
import com.taskflow.entidades.Usuario;
import com.taskflow.repositorios.MiembroProyectoRepository;
import com.taskflow.repositorios.ProyectoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final MiembroProyectoRepository miembroRepository;
    private final UsuarioService usuarioService;

    public ProyectoService(ProyectoRepository proyectoRepository,
                           MiembroProyectoRepository miembroRepository,
                           UsuarioService usuarioService) {
        this.proyectoRepository = proyectoRepository;
        this.miembroRepository = miembroRepository;
        this.usuarioService = usuarioService;
    }

    /** Solo ADMIN. El creador queda registrado automáticamente como LIDER del proyecto. */
    @Transactional
    public Proyecto crearProyecto(Proyecto proyecto, Usuario creador) {
        if (creador.getRol() != Rol.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo un ADMIN puede crear proyectos");
        }
        proyecto.setCreadoPor(creador);
        Proyecto guardado = proyectoRepository.save(proyecto);

        MiembroProyecto lider = new MiembroProyecto();
        lider.setProyecto(guardado);
        lider.setUsuario(creador);
        lider.setRolEnProyecto(RolEnProyecto.LIDER);
        miembroRepository.save(lider);

        return guardado;
    }

    /** Proyectos en los que el usuario es miembro. */
    @Transactional(readOnly = true)
    public List<Proyecto> listarProyectosDeUsuario(Usuario usuario) {
        return miembroRepository.findByUsuario(usuario).stream()
                .map(MiembroProyecto::getProyecto)
                .toList();
    }

    /**
     * Solo ADMIN (global) o LIDER del proyecto.
     * Se añade el parámetro 'solicitante' porque sin él no se puede verificar el permiso.
     */
    @Transactional
    public MiembroProyecto agregarMiembro(Long proyectoId, Long usuarioId,
                                          RolEnProyecto rol, Usuario solicitante) {
        Proyecto proyecto = obtenerProyecto(proyectoId);

        if (solicitante.getRol() != Rol.ADMIN && !esLider(proyecto, solicitante)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo un ADMIN o el LIDER del proyecto puede agregar miembros");
        }

        Usuario nuevo = usuarioService.obtenerPorId(usuarioId);
        if (miembroRepository.existsByProyectoAndUsuario(proyecto, nuevo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El usuario ya es miembro del proyecto");
        }

        MiembroProyecto miembro = new MiembroProyecto();
        miembro.setProyecto(proyecto);
        miembro.setUsuario(nuevo);
        miembro.setRolEnProyecto(rol != null ? rol : RolEnProyecto.MIEMBRO);
        return miembroRepository.save(miembro);
    }

    /** Lanza 403 si el usuario no es miembro; devuelve el proyecto si tiene acceso. */
    @Transactional(readOnly = true)
    public Proyecto verificarAcceso(Long proyectoId, Usuario usuario) {
        Proyecto proyecto = obtenerProyecto(proyectoId);
        if (!miembroRepository.existsByProyectoAndUsuario(proyecto, usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No eres miembro de este proyecto");
        }
        return proyecto;
    }

    @Transactional(readOnly = true)
    public Proyecto obtenerProyecto(Long proyectoId) {
        return proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Proyecto no encontrado: " + proyectoId));
    }

    @Transactional(readOnly = true)
    public boolean esLider(Proyecto proyecto, Usuario usuario) {
        return miembroRepository.findByProyectoAndUsuario(proyecto, usuario)
                .map(m -> m.getRolEnProyecto() == RolEnProyecto.LIDER)
                .orElse(false);
    }
}
