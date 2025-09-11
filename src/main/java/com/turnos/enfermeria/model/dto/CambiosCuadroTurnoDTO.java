package com.turnos.enfermeria.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CambiosCuadroTurnoDTO {
    private Long idCambioCuadro;
    private Long idCuadroTurno; // Solo el ID en lugar de la relación con la entidad
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCambio;
    private Long idMacroproceso;
    private Long idProcesos;
    private Long idServicios;
    private Long idSeccionesServicios;
    private Long idSubseccionesServicios;
    private List<Long> idsProcesosAtencion;
    private Long idEquipo;
    private String nombre;
    private String anio;
    private String mes;
    private String estadoCuadro; // "abierto" o "cerrado"
    private String version; // Ejemplo: "v01_0225"
    private Boolean TurnoExcepcion;
    private Boolean estado;
    private String categoria;

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