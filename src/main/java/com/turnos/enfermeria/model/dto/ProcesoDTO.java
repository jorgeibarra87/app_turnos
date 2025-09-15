package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class ProcesoDTO {
    private Long idProceso;
    private String nombre;
    private String detalle;
    private Boolean estado;
}
