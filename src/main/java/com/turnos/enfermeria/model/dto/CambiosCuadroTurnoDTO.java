package com.turnos.enfermeria.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Long idProcesoAtencion;
    private String nombre;
    private String anio;
    private String mes;
    private String estadoCuadro; // "abierto" o "cerrado"
    private String version; // Ejemplo: "v01_0225"
    private boolean TurnoExcepcion;
    private Boolean estado;
    private String categoria;
}