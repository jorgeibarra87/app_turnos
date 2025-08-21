package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class ServicioDTO {
    private Long idServicio;
    private String nombre;
    private String tipo;
    private Long idBloqueServicio;
    private Long idProceso;
    private Boolean estado = true;
    private String nombreBloqueServicio;
    private String nombreProceso;
}
