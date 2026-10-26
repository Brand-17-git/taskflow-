package com.taskflow.repositorios;

import com.taskflow.entidades.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
}
