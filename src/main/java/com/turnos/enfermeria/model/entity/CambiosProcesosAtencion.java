package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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

//    @ManyToOne
//    @JoinColumn(name = "id_cambio_cuadro", referencedColumnName = "id_cambio_cuadro")
//    private CambiosCuadroTurno cambioCuadroTurno;

    @Column(name = "fecha_cambio")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCambio;

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
