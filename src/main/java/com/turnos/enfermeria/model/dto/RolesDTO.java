package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class RolesDTO {

    private Long idRol;
    private String rol;
    private String descripcion;
    private Boolean estado = true;
}
