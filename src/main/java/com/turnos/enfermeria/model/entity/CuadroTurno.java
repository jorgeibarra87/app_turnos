package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

//    @JsonIgnoreProperties("cuadroTurno")
//    @OneToMany(mappedBy = "cuadroTurno", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<ProcesosAtencion> procesosAtencion;

//    @JsonIgnoreProperties("cuadroTurno")
//    @OneToMany(mappedBy = "cuadroTurno", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    private List<ProcesosAtencion> procesosAtencion;

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

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "estado")
    private Boolean estado;


    // Métodos de conveniencia para obtener IDs
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

    public Long getIdEquipo() {
        return equipos != null ? equipos.getIdEquipo() : null;
    }

//    // Nuevos métodos para manejar múltiples procesos de atención
//    public List<Long> getIdsProcesosAtencion() {
//        if (procesosAtencion == null || procesosAtencion.isEmpty()) {
//            return new ArrayList<>();
//        }
//        return procesosAtencion.stream()
//                .map(ProcesosAtencion::getIdProcesoAtencion)
//                .collect(Collectors.toList());
//    }
//
//    public void addProcesoAtencion(ProcesosAtencion procesoAtencion) {
//        if (this.procesosAtencion == null) {
//            this.procesosAtencion = new ArrayList<>();
//        }
//        if (!this.procesosAtencion.contains(procesoAtencion)) {
//            this.procesosAtencion.add(procesoAtencion);
//        }
//    }
//
//    public void removeProcesoAtencion(ProcesosAtencion procesoAtencion) {
//        if (this.procesosAtencion != null) {
//            this.procesosAtencion.remove(procesoAtencion);
//        }
//    }
//
//    public boolean hasProcesoAtencion(Long idProcesoAtencion) {
//        return procesosAtencion != null &&
//                procesosAtencion.stream()
//                        .anyMatch(pa -> pa.getIdProcesoAtencion().equals(idProcesoAtencion));
//    }
//
//    public int getCantidadProcesosAtencion() {
//        return procesosAtencion != null ? procesosAtencion.size() : 0;
//    }
//
//    public String getNombresProcesosAtencion() {
//        if (procesosAtencion == null || procesosAtencion.isEmpty()) {
//            return "";
//        }
//        return procesosAtencion.stream()
//                .map(ProcesosAtencion::getDetalle)
//                .collect(Collectors.joining(", "));
//    }
}
