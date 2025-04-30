package com.turnos.enfermeria.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuadroTurnoDTO {
    private Long idCuadroTurno;
    private Long idMacroproceso;
    private Long idProceso;
    private Long idServicios;
    private Long idSeccionesServicios;
    private Long idProcesosAtencion;
    private String nombre;
    private String anio;
    private String mes;
    private String estadoCuadro = "abierto"; // "abierto" o "cerrado"
    private String version; // Ejemplo: "v01_0225"
    private Boolean turnoExcepcion = false;

}