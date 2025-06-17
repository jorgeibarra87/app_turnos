package com.turnos.enfermeria.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CambiosTurnoDTO {
    private Long idCambio;
    private Long idTurno;
    private Long idCuadroTurno;
    private Long idUsuario;
    private LocalDateTime fechaCambio;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long totalHoras;
    private String tipoTurno;
    private String estadoTurno;
    private String jornada;
    private String version;
    private String comentarios;
    private Boolean estado = true;

}

