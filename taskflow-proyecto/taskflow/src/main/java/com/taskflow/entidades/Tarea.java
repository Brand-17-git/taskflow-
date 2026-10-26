package com.taskflow.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
@Getter
@Setter
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false)
    private Prioridad prioridad = Prioridad.MEDIA;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoTarea estado = EstadoTarea.PENDIENTE;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @NotNull
    @Column(name = "es_personal", nullable = false)
    private Boolean esPersonal = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_a_id")
    private Usuario asignadoA;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private Usuario creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
