package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class SubseccionesServicioDTO {
    private Long idSubseccionServicio;
    private String nombre;
    private Long idSeccionServicio;
    private Boolean estado = true;
    public String nombreSeccion;
}
