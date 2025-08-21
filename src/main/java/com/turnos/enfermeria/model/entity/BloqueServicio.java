package com.turnos.enfermeria.model.entity;

import lombok.Data;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * $table.getTableComment()
 */
@Getter
@Setter
@Entity
@Table(name = "bloque_servicio", schema = "public")
public class BloqueServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloque_servicio", nullable = false)
    private Long idBloqueServicio;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "estado")
    private Boolean estado;

}
