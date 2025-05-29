package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cambios_cuadro_turno", schema = "public")
public class CambiosCuadroTurno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cambio_cuadro", nullable = false)
    private Long idCambioCuadro;

    @ManyToOne
    @JoinColumn(name = "id_cuadro_turno", referencedColumnName = "id_cuadro_turno")
    private CuadroTurno cuadroTurno;

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
    private ProcesosAtencion procesoAtencion;

    @ManyToOne
    @JoinColumn(name = "id_equipo", referencedColumnName = "id_equipo")
    private Equipo equipos;

    @Column(name = "fecha_cambio")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCambio;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "mes")
    private String mes;

    @Column(name = "anio")
    private String anio;

    @Column(name = "estado_cuadro")
    private String estadoCuadro;

    @Column(name = "version")
    private String version;

    @Column(name = "turno_excepcion")
    private Boolean TurnoExcepcion;

    public Long getIdCuadroTurno() {
        return cuadroTurno != null ? cuadroTurno.getIdCuadroTurno() : null;
    }
    public Long getIdMacroproceso() {
        return macroProcesos != null ? macroProcesos.getIdMacroproceso() : null;
    }
    public Long getIdProceso() {
        return procesos != null ? procesos.getIdProceso() : null;
    }
    public Long getIdServicio() {
        return servicios != null ? servicios.getIdServicio() : null;
    }
    public Long getIdSeccionServicio() {
        return seccionesServicios != null ? seccionesServicios.getIdSeccionServicio() : null;
    }
    public Long getIdProcesoAtencion() {
        return procesoAtencion != null ? procesoAtencion.getIdProcesoAtencion() : null;
    }

}
