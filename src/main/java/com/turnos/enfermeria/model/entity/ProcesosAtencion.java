package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
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

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_proceso", referencedColumnName = "id_proceso")
    @JsonIncludeProperties("nombre")
    private Procesos procesos;

    @ManyToOne
    @JoinColumn(name = "id_cuadro_turno", referencedColumnName = "id_cuadro_turno")
    @JsonIncludeProperties("nombre")
    private CuadroTurno cuadroTurno;

//    public Long getIdProceso() {
//        return procesos != null ? procesos.getIdProceso() : null;
//    }

    public Long getIdCuadroTurno() {
        return cuadroTurno != null ? cuadroTurno.getIdCuadroTurno() : null;
    }
public String getNombreCuadro() {
    return cuadroTurno != null ? cuadroTurno.getNombre() : null;
}

    public String getNombreProceso() {
        return procesos != null ? procesos.getNombre() : null;
    }
}
