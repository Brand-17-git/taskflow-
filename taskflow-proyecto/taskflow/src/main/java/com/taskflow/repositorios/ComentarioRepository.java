package com.taskflow.repositorios;

import com.taskflow.entidades.Comentario;
import com.taskflow.entidades.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByTarea(Tarea tarea);
}
