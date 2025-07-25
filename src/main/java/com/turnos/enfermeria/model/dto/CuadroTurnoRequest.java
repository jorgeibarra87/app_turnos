package com.turnos.enfermeria.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO actualizado para la creación de cuadros de turno con múltiples procesos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuadroTurnoRequest {

    private String nombre;
    private Long idMacroproceso;
    private Long idProceso;
    private Long idServicio;
    private Long idSeccionServicio;
    private Long idSubseccionServicio;
    private List<Long> idsProcesosAtencion; // CAMBIO: de Long a List<Long>
    private Long idEquipo;
    private String anio;
    private String mes;
    private Boolean turnoExcepcion = false;
    private String categoria;
    private Boolean estado = true;
    private String estadoCuadro = "abierto";
    private String version;

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