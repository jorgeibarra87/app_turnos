package com.turnos.enfermeria.model.dto;

import com.turnos.enfermeria.model.entity.CambiosCuadroTurno;
import com.turnos.enfermeria.model.entity.CuadroTurno;
import com.turnos.enfermeria.model.entity.Procesos;
import lombok.Data;

@Data
public class CambiosProcesosAtencionDTO {

    private Long idCambioProcesoAtencion;
    private CambiosCuadroTurno cambiosCuadroTurno;
    private String detalle;
    private Procesos procesos;
    private CuadroTurno cuadroTurno;
    private Boolean estado = true;

}