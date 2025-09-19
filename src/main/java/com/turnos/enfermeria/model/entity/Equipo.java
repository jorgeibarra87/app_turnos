package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * $table.getTableComment()
 */
@Getter
@Setter
@Entity
@Table(name = "equipo", schema = "public")
public class Equipo {

    @Id
    @Column(name = "id_equipo", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipo;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

}
