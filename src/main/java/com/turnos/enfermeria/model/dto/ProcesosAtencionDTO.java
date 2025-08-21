package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class ProcesosAtencionDTO {

    private Long idProcesoAtencion;
    private String detalle;
    private Long idProceso;
    private Long idCuadroTurno;
    private Boolean estado = true;
    private String nombreCuadro;
    private String nombreProceso;
}
