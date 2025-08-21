package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class UsuarioContratoDTO {
    private Long idUsuarioContrato;
    private Long idPersona;
    private Long idContrato;
    private Long idRol;
    private Boolean estado = true;
}
