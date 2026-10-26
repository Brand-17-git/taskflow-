package com.taskflow.controladores;

import com.taskflow.dtos.MiembroRequest;
import com.taskflow.dtos.MiembroResponse;
import com.taskflow.dtos.ProyectoRequest;
import com.taskflow.dtos.ProyectoResponse;
import com.taskflow.entidades.Proyecto;
import com.taskflow.entidades.Usuario;
import com.taskflow.servicios.ProyectoService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Proyectos", description = "Gestión de proyectos y sus miembros")
public class ProyectoController {

    private final ProyectoService proyectoService;
    private final UsuarioService usuarioService;

    public ProyectoController(ProyectoService proyectoService, UsuarioService usuarioService) {
        this.proyectoService = proyectoService;
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar mis proyectos",
            description = "Devuelve los proyectos en los que el usuario autenticado es miembro.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de proyectos del usuario"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public List<ProyectoResponse> listar(@AuthenticationPrincipal UserDetails principal) {
        Usuario usuario = usuarioService.obtenerPorNombreUsuario(principal.getUsername());
        return proyectoService.listarProyectosDeUsuario(usuario).stream()
                .map(ProyectoResponse::de)
                .toList();
    }

    @Operation(summary = "Crear proyecto",
            description = "Solo ADMIN. El creador queda registrado como LIDER del proyecto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Proyecto creado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no es ADMIN")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody ProyectoRequest request,
                                                  @AuthenticationPrincipal UserDetails principal) {
        Usuario usuario = usuarioService.obtenerPorNombreUsuario(principal.getUsername());
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(request.nombre());
        proyecto.setDescripcion(request.descripcion());
        if (request.estado() != null) {
            proyecto.setEstado(request.estado());
        }

        Proyecto creado = proyectoService.crearProyecto(proyecto, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProyectoResponse.de(creado));
    }

    @Operation(summary = "Ver proyecto",
            description = "Solo miembros del proyecto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Datos del proyecto"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario no es miembro del proyecto"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/{id}")
    public ProyectoResponse ver(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails principal) {
        Usuario usuario = usuarioService.obtenerPorNombreUsuario(principal.getUsername());
        return ProyectoResponse.de(proyectoService.verificarAcceso(id, usuario));
    }

    /** ADMIN o LIDER del proyecto: la regla depende del proyecto, así que la aplica el servicio. */
    @Operation(summary = "Agregar miembro al proyecto",
            description = "Solo ADMIN o LIDER del proyecto. Si no se indica rol, se asigna MIEMBRO.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Miembro agregado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No eres ADMIN ni LIDER del proyecto"),
            @ApiResponse(responseCode = "404", description = "Proyecto o usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "El usuario ya es miembro del proyecto")
    })
    @PostMapping("/{id}/miembros")
    public ResponseEntity<MiembroResponse> agregarMiembro(@PathVariable Long id,
                                                          @Valid @RequestBody MiembroRequest request,
                                                          @AuthenticationPrincipal UserDetails principal) {
        Usuario solicitante = usuarioService.obtenerPorNombreUsuario(principal.getUsername());
        var miembro = proyectoService.agregarMiembro(
                id, request.usuarioId(), request.rolEnProyecto(), solicitante);
        return ResponseEntity.status(HttpStatus.CREATED).body(MiembroResponse.de(miembro));
    }
}
