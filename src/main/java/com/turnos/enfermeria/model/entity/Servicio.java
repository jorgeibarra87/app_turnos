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

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_bloque_servicio", referencedColumnName = "id_bloque_servicio")
    @JsonIncludeProperties("nombre")
    private BloqueServicio bloqueServicios;

    @ManyToOne
    @JoinColumn(name = "id_proceso", referencedColumnName = "id_proceso")
    @JsonIncludeProperties("nombre")
    private Procesos procesos;

    public Long getIdBloqueServicio() {
        return bloqueServicios != null ? bloqueServicios.getIdBloqueServicio() : null;
    }

    public Long getIdProceso() {
        return procesos != null ? procesos.getIdProceso() : null;
    }

    public String getNombreBloqueServicio() {
        return bloqueServicios != null ? bloqueServicios.getNombre() : null;
    }

    public String getNombreProceso() {
        return procesos != null ? procesos.getNombre() : null;
    }
}
