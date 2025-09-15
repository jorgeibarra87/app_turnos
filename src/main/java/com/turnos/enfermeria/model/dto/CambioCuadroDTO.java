package com.turnos.enfermeria.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CambioCuadroDTO {
    private Long idCambioCuadro;
    private Long idCuadroTurno;
    private String nombre;
    private String mes;
    private String anio;
    private String estadoCuadro;
    private String version;
    private Boolean turnoExcepcion;
    private String categoria;
    private Boolean estado;
    private LocalDateTime fechaCambio;

    // Información adicional para el correo
    private String nombreMacroproceso;
    private String nombreProceso;
    private String nombreServicio;
    private String nombreSeccionServicio;
    private String nombreSubseccionServicio;
    private String nombreEquipo;
}
