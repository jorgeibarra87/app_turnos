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

    @Column(name = "detalle")
    private String detalle;

    @ManyToOne
    @JoinColumn(name = "id_proceso", referencedColumnName = "id_proceso")
    private Procesos procesos;

    @ManyToOne
    @JoinColumn(name = "id_cuadro_turno", referencedColumnName = "id_cuadro_turno")
    private CuadroTurno cuadroTurno;

//    public Long getIdProceso() {
//        return procesos != null ? procesos.getIdProceso() : null;
//    }
}
