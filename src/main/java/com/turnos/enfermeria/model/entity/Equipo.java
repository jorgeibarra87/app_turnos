package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * $table.getTableComment()
 */
@Data
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


}
