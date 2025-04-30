package com.turnos.enfermeria.model.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.util.List;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "subsecciones_servicio", schema = "public")
public class SubseccionesServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subseccion_servicio", nullable = false)
    private Long idSubseccionServicio;

    @Column(name = "nombre")
    private String nombre;


    @ManyToOne
    @JoinColumn(name = "id_seccion_servicio", referencedColumnName = "id_seccion_servicio")
    private SeccionesServicio seccionesServicios;

    public Long getIdSeccionServicio() {
        return seccionesServicios != null ? seccionesServicios.getIdSeccionServicio() : null;
    }

}
