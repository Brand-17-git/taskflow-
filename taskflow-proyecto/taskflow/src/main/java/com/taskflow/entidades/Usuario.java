package com.taskflow.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @NotBlank
    @Email
    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @NotBlank
    @Size(min = 8)
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
