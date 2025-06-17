package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class EquipoDTO {
    private Long idEquipo;
    private String nombre;
    private Boolean estado = true;
}
