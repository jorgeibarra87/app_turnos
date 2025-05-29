package com.turnos.enfermeria.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;



/**
 * $table.getTableComment()
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "cuadro_turno", schema = "public")
public class CuadroTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuadro_turno", nullable = false)
    private Long idCuadroTurno;

    @ManyToOne
    @JoinColumn(name = "id_macroproceso", referencedColumnName = "id_macroproceso")
    private Macroprocesos macroProcesos;

    @ManyToOne
    @JoinColumn(name = "id_proceso", referencedColumnName = "id_proceso")
    private Procesos procesos;

    @ManyToOne
    @JoinColumn(name = "id_servicio", referencedColumnName = "id_servicio")
    private Servicio servicios;

    @ManyToOne
    @JoinColumn(name = "id_seccion_servicio", referencedColumnName = "id_seccion_servicio")
    private SeccionesServicio seccionesServicios;

    @ManyToOne
    @JoinColumn(name = "id_proceso_atencion", referencedColumnName = "id_proceso_atencion")
    private ProcesosAtencion procesosAtencion;

    @ManyToOne
    @JoinColumn(name = "id_equipo", referencedColumnName = "id_equipo")
    private Equipo equipos;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "anio")
    private String anio;

    @Column(name = "mes")
    private String mes;

    //abierto,cerrado
    @Column(name = "estado_cuadro")
    private String estadoCuadro = "abierto";

    //v01_0225
    @Column(name = "version")
    private String version;

    @Column(name = "turno_excepcion")
    private Boolean turnoExcepcion = false;

    public Long getIdMacroproceso() {
        return macroProcesos != null ? macroProcesos.getIdMacroproceso() : null;
    }
    public Long getIdProceso() {
        return procesos != null ? procesos.getIdProceso() : null;
    }
    public Long getIdServicios() {
        return servicios != null ? servicios.getIdServicio() : null;
    }
    public Long getIdSeccionServicio() {
        return seccionesServicios != null ? seccionesServicios.getIdSeccionServicio() : null;
    }
    public Long getIdProcesosAtencion() {
        return procesosAtencion != null ? procesosAtencion.getIdProcesoAtencion() : null;
    }
}
