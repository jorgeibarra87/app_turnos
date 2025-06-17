package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class GestorContratoDTO {
    private Long idGestorContrato;
    private Long idPersona;
    private Long idContrato;
    private Boolean estado = true;
}
