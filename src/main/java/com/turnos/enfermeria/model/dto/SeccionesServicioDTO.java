package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class SeccionesServicioDTO {

    private Long idSeccionServicio;
    private String nombre;
    private Long idServicio;
    private Boolean estado = true;
    private String nombreServic;
}
