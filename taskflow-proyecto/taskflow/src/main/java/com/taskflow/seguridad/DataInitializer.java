package com.taskflow.seguridad;

import com.taskflow.entidades.Rol;
import com.taskflow.entidades.Usuario;
import com.taskflow.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * SOLO DESARROLLO: crea un usuario ADMIN inicial si no existe, para poder probar
 * los endpoints restringidos. Cambia o elimina esto antes de desplegar.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String nombreUsuario;
    private final String correo;
    private final String contrasena;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${taskflow.admin.usuario}") String nombreUsuario,
                           @Value("${taskflow.admin.correo}") String correo,
                           @Value("${taskflow.admin.contrasena}") String contrasena) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)
                || usuarioRepository.existsByCorreo(correo)) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setNombreUsuario(nombreUsuario);
        admin.setCorreo(correo);
        admin.setContrasena(passwordEncoder.encode(contrasena));
        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);
    }
}
