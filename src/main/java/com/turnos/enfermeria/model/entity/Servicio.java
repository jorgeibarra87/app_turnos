package com.turnos.enfermeria.model.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.util.List;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "servicio", schema = "public")
public class Servicio {

    @Id
    @Column(name = "id_servicio", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServicio;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "tipo")
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "id_bloque_servicio", referencedColumnName = "id_bloque_servicio")
    private BloqueServicio bloqueServicios;

    @ManyToOne
    @JoinColumn(name = "id_proceso", referencedColumnName = "id_proceso")
    private Procesos procesos;

    public Long getIdBloqueServicio() {
        return bloqueServicios != null ? bloqueServicios.getIdBloqueServicio() : null;
    }

    public Long getIdProceso() {
        return procesos != null ? procesos.getIdProceso() : null;
    }
}
