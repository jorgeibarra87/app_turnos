package com.turnos.enfermeria.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcesosDTO {

    private Long idProceso;
    private String nombre;
    private Long idMacroproceso;
    private Boolean estado = true;
    private String nombreMacroproceso;

    public ProcesosDTO(Long idProceso, String nombre, Long idMacroproceso, Boolean estado) {
        this.idProceso = idProceso;
        this.nombre = nombre;
        this.idMacroproceso = idMacroproceso;
        this.estado = estado;
    }
}
