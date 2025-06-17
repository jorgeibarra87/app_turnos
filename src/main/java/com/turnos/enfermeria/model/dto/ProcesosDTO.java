package com.turnos.enfermeria.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcesosDTO {



    private Long idProceso;
    private String nombre;
    private Long idMacroproceso;
    private Boolean estado = true;
}
