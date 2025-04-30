package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class ProcesosContratoDTO {
    private Long idProcesoContrato;
    private String detalle;
    private Long idProceso;
    private Long idContrato;
}
