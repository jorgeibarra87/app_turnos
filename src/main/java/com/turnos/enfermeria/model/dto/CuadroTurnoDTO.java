package com.turnos.enfermeria.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuadroTurnoDTO {
    private Long idCuadroTurno;
    private Long idMacroproceso;
    private Long idProceso;
    private Long idServicio;
    private Long idSeccionesServicios;
    private Long idSubseccionServicio;
    private List<Long> idsProcesosAtencion; // Cambiado de Long a List<Long>
    private Long idEquipo;
    private String nombre;
    private String anio;
    private String mes;
    private String estadoCuadro = "abierto"; // "abierto" o "cerrado"
    private String version; // Ejemplo: "v01_0225"
    private Boolean turnoExcepcion = false;
    private String categoria;
    private Boolean estado = true;
    private String nombreEquipo;
    private String nombreProceso;
    private String nombreServicio;
    private String nombreSeccionServicio;
    private String nombreSubseccionServicio;
    private String nombreMacroproceso;
    private String observaciones;

    // Métodos de conveniencia para manejar procesos de atención
    public void addIdProcesoAtencion(Long idProcesoAtencion) {
        if (this.idsProcesosAtencion == null) {
            this.idsProcesosAtencion = new ArrayList<>();
        }
        if (!this.idsProcesosAtencion.contains(idProcesoAtencion)) {
            this.idsProcesosAtencion.add(idProcesoAtencion);
        }
    }

    public void removeIdProcesoAtencion(Long idProcesoAtencion) {
        if (this.idsProcesosAtencion != null) {
            this.idsProcesosAtencion.remove(idProcesoAtencion);
        }
    }

    public boolean hasProcesoAtencion(Long idProcesoAtencion) {
        return idsProcesosAtencion != null && idsProcesosAtencion.contains(idProcesoAtencion);
    }

    public int getCantidadProcesosAtencion() {
        return idsProcesosAtencion != null ? idsProcesosAtencion.size() : 0;
    }

}