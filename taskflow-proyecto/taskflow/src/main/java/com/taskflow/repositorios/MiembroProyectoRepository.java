package com.taskflow.repositorios;

import com.taskflow.entidades.MiembroProyecto;
import com.taskflow.entidades.Proyecto;
import com.taskflow.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MiembroProyectoRepository extends JpaRepository<MiembroProyecto, Long> {

    Optional<MiembroProyecto> findByProyectoAndUsuario(Proyecto proyecto, Usuario usuario);

    boolean existsByProyectoAndUsuario(Proyecto proyecto, Usuario usuario);

    List<MiembroProyecto> findByUsuario(Usuario usuario);
}
