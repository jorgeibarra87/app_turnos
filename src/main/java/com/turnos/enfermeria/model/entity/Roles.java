package com.turnos.enfermeria.model.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.util.List;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "roles", schema = "public")
public class Roles {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "descripcion")
    private String descripcion;

}
