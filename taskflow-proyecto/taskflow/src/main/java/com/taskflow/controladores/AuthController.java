package com.taskflow.controladores;

import com.taskflow.dtos.LoginRequest;
import com.taskflow.dtos.LoginResponse;
import com.taskflow.dtos.RegistroRequest;
import com.taskflow.servicios.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión (endpoints públicos)")
@SecurityRequirements // endpoints públicos: sin candado en Swagger
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Iniciar sesión",
            description = "Valida las credenciales y devuelve un token JWT para usar como Bearer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto, devuelve el token"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Registrar usuario",
            description = "Crea un usuario nuevo con rol USUARIO. La contraseña se guarda cifrada (BCrypt).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El nombre de usuario o el correo ya existen")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registrar(@Valid @RequestBody RegistroRequest request) {
        authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Usuario registrado correctamente"));
    }
}
