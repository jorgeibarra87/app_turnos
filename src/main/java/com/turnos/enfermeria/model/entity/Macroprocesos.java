package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "macroproceso", schema = "public")
public class Macroprocesos {
    @Id
    @Column(name = "id_macroproceso", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMacroproceso;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "estado")
    private Boolean estado;


}
