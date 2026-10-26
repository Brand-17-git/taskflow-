package com.taskflow.servicios;

import com.taskflow.dtos.LoginRequest;
import com.taskflow.dtos.LoginResponse;
import com.taskflow.dtos.RegistroRequest;
import com.taskflow.entidades.Rol;
import com.taskflow.entidades.Usuario;
import com.taskflow.repositorios.UsuarioRepository;
import com.taskflow.seguridad.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.nombreUsuario(), request.contrasena()));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        Usuario usuario = usuarioRepository.findByNombreUsuario(request.nombreUsuario())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        return new LoginResponse(jwtService.generarToken(usuario));
    }

    @Transactional
    public void registrar(RegistroRequest request) {
        if (usuarioRepository.existsByNombreUsuario(request.nombreUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.nombreUsuario());
        usuario.setCorreo(request.correo());
        usuario.setContrasena(passwordEncoder.encode(request.contrasena()));
        usuario.setRol(Rol.USUARIO);
        usuarioRepository.save(usuario);
    }
}
