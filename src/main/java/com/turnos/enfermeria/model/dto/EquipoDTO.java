package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class EquipoDTO {
    private Long idEquipo;
    private String nombre;
    private Boolean estado;
    private String observaciones;

    public EquipoDTO(Long idEquipo, String nombre, Boolean estado, String observaciones) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public EquipoDTO() {}
}