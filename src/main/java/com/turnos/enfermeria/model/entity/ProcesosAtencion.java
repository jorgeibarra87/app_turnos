package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "procesos_atencion", schema = "public")
public class ProcesosAtencion {

    @Id
    @Column(name = "id_proceso_atencion", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProcesoAtencion;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_proceso", referencedColumnName = "id_proceso")
    private Procesos procesos;

    public Long getIdProceso() {
        return procesos != null ? procesos.getIdProceso() : null;
    }
}
