package com.turnos.enfermeria.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MacroprocesosDTO {
    private Long idMacroproceso;
    private String nombre;
    private Boolean estado = true;
}
