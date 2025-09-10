package com.turnos.enfermeria.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TurnoDTO {
    private Long idTurno;
    private Long idCuadroTurno; // ID del cuadro de turnos al que pertenece
    private Long idPersona; // Usuario asignado
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long totalHoras;
    private String tipoTurno;
    private String estadoTurno;
    private String jornada;
    private String version;
    private String comentarios;
    private Boolean estado = true;
    private String nombrePersona;
    private String documento;
}