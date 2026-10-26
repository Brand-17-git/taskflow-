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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "miembros_proyecto",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_miembro_proyecto_usuario",
                columnNames = {"proyecto_id", "usuario_id"}))
@Getter
@Setter
public class MiembroProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rol_en_proyecto", nullable = false)
    private RolEnProyecto rolEnProyecto = RolEnProyecto.MIEMBRO;

    @Column(name = "fecha_union", nullable = false, updatable = false)
    private LocalDateTime fechaUnion;

    @PrePersist
    protected void alCrear() {
        this.fechaUnion = LocalDateTime.now();
    }
}
