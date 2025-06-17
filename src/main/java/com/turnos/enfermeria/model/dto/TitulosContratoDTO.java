package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class TitulosContratoDTO {
    private Long idTitulo;
    private String titulo;
    private Boolean estado = true;
}
