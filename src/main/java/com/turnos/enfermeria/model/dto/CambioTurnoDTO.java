package com.turnos.enfermeria.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CambioTurnoDTO {
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

    // Información adicional
    private String nombrePersona;
    private String documentoPersona;
}
