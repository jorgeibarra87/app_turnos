package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class EquipoDTO {
    private Long idEquipo;
    private String nombre;
    private Boolean estado;

    public EquipoDTO(Long idEquipo, String nombre, Boolean estado) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.estado = estado;
    }

    public EquipoDTO() {}
}