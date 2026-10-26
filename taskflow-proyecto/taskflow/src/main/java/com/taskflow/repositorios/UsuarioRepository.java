package com.taskflow.repositorios;

import com.taskflow.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByCorreo(String correo);

    boolean existsByNombreUsuario(String nombreUsuario);
}
