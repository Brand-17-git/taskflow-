package com.taskflow.repositorios;

import com.taskflow.entidades.Proyecto;
import com.taskflow.entidades.Tarea;
import com.taskflow.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByProyecto(Proyecto proyecto);

    List<Tarea> findByEsPersonalTrueAndCreadoPor(Usuario creadoPor);

    List<Tarea> findByAsignadoA(Usuario asignadoA);
}
