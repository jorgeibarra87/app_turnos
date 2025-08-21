package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class BloqueServicioDTO {

    private Long idBloqueServicio;
    private String nombre;
    private Boolean estado = true;
}
