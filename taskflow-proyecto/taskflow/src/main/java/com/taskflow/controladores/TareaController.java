package com.taskflow.controladores;

import com.taskflow.dtos.EstadoTareaRequest;
import com.taskflow.dtos.TareaRequest;
import com.taskflow.dtos.TareaResponse;
import com.taskflow.entidades.Tarea;
import com.taskflow.entidades.Usuario;
import com.taskflow.servicios.TareaService;
import com.taskflow.servicios.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Tareas", description = "Tareas personales y de equipo")
public class TareaController {

    private final TareaService tareaService;
    private final UsuarioService usuarioService;

    public TareaController(TareaService tareaService, UsuarioService usuarioService) {
        this.tareaService = tareaService;
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar mis tareas personales",
            description = "Devuelve las tareas personales creadas por el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tareas personales"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/mis-tareas")
    public List<TareaResponse> misTareas(@AuthenticationPrincipal UserDetails principal) {
        Usuario usuario = usuarioService.obtenerPorNombreUsuario(principal.getUsername());
        return tareaService.listarTareasPersonales(usuario).stream()
                .map(TareaResponse::de)
                .toList();
    }

    @Operation(summary = "Listar tareas de un proyecto",
            description = "Solo miembros del proyecto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tareas del proyecto"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no es miembro del proyecto"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/proyecto/{proyectoId}")
    public List<TareaResponse> tareasDeProyecto(@PathVariable Long proyectoId,
                                                @AuthenticationPrincipal UserDetails principal) {
        Usuario usuario = usuarioService.obtenerPorNombreUsuario(principal.getUsername());
        return tareaService.listarTareasDeProyecto(proyectoId, usuario).stream()
                .map(TareaResponse::de)
                .toList();
    }

    @Operation(summary = "Crear tarea personal",
            description = "Crea una tarea sin proyecto, asignada al propio usuario. Se ignora asignadoAId.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping("/personal")
    public ResponseEntity<TareaResponse> crearPersonal(@Valid @RequestBody TareaRequest request,
                                                       @AuthenticationPrincipal UserDetails principal) {
        Usuario usuario = usuarioService.obtenerPorNombreUsuario(principal.getUsername());
        Tarea creada = tareaService.crearTareaPersonal(aEntidad(request), usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(TareaResponse.de(creada));
    }

    /** Solo LIDER del proyecto: la regla depende del proyecto, así que la aplica el servicio. */
    @Operation(summary = "Crear tarea de equipo",
            description = "Solo el LIDER del proyecto. Si se indica asignadoAId, ese usuario debe ser miembro.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o el asignado no es miembro del proyecto"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no es LIDER del proyecto"),
            @ApiResponse(responseCode = "404", description = "Proyecto o usuario asignado no encontrado")
    })
    @PostMapping("/proyecto/{proyectoId}")
    public ResponseEntity<TareaResponse> crearDeEquipo(@PathVariable Long proyectoId,
                                                       @Valid @RequestBody TareaRequest request,
                                                       @AuthenticationPrincipal UserDetails principal) {
        Usuario usuario = usuarioService.obtenerPorNombreUsuario(principal.getUsername());
        Tarea tarea = aEntidad(request);
        if (request.asignadoAId() != null) {
            tarea.setAsignadoA(usuarioService.obtenerPorId(request.asignadoAId()));
        }
        Tarea creada = tareaService.crearTareaEquipo(tarea, usuario, proyectoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(TareaResponse.de(creada));
    }

    @Operation(summary = "Cambiar estado de una tarea",
            description = "Solo el usuario asignado o el LIDER del proyecto. En tareas personales, solo su creador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No eres el asignado ni LIDER del proyecto"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PatchMapping("/{id}/estado")
    public TareaResponse cambiarEstado(@PathVariable Long id,
                                       @Valid @RequestBody EstadoTareaRequest request,
                                       @AuthenticationPrincipal UserDetails principal) {
        Usuario usuario = usuarioService.obtenerPorNombreUsuario(principal.getUsername());
        return TareaResponse.de(tareaService.actualizarEstadoTarea(id, request.estado(), usuario));
    }

    private Tarea aEntidad(TareaRequest request) {
        Tarea tarea = new Tarea();
        tarea.setTitulo(request.titulo());
        tarea.setDescripcion(request.descripcion());
        tarea.setFechaLimite(request.fechaLimite());
        if (request.prioridad() != null) {
            tarea.setPrioridad(request.prioridad());
        }
        return tarea;
    }
}
