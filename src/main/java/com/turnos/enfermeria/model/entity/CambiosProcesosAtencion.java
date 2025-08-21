package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "cambios_procesos_atencion", schema = "public")
public class CambiosProcesosAtencion {

    @Id
    @Column(name = "id_cambio_proceso_atencion", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCambioProcesoAtencion;

    @ManyToOne
    @JoinColumn(name = "id_cambio_cuadro", referencedColumnName = "id_cambio_cuadro")
    private CambiosCuadroTurno cambioCuadroTurno;

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_proceso", referencedColumnName = "id_proceso")
    private Procesos procesos;

    @ManyToOne
    @JoinColumn(name = "id_cuadro_turno", referencedColumnName = "id_cuadro_turno")
    private CuadroTurno cuadroTurno;

}
