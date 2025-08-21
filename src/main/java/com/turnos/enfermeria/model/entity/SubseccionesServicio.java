package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
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

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_seccion_servicio", referencedColumnName = "id_seccion_servicio")
    @JsonIncludeProperties("nombre")
    private SeccionesServicio seccionesServicios;

    public Long getIdSeccionServicio() {
        return seccionesServicios != null ? seccionesServicios.getIdSeccionServicio() : null;
    }

    public String getNombreSeccion() {
        return seccionesServicios != null ? seccionesServicios.getNombre() : null;
    }

}
