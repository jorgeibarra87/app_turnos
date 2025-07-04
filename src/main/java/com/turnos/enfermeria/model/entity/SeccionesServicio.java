package com.turnos.enfermeria.model.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.util.List;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "secciones_servicio", schema = "public")
public class SeccionesServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seccion_servicio", nullable = false)
    private Long idSeccionServicio;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_servicio", referencedColumnName = "id_servicio")
    private Servicio servicio;

    public Long getIdServicio() {
        return servicio != null ? servicio.getIdServicio() : null;
    }
}
