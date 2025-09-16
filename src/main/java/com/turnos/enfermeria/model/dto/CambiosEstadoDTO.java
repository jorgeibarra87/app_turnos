package com.turnos.enfermeria.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CambiosEstadoDTO {
    private List<CuadroTurnoDTO> cuadrosActualizados;
    private List<TurnoDTO> turnosActualizados;
}
