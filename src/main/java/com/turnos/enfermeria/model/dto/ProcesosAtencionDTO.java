package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class ProcesosAtencionDTO {

    private Long idProcesoAtencion;
    private String detalle;
    private Long idProceso;
    private Boolean estado = true;
}
