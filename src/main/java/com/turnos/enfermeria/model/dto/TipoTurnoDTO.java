package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class TipoTurnoDTO {
    private Long idTipoTurno;
    private String especialidad;
    private boolean presencial;
    private boolean disponibilidad;
    private Long idContrato;
    private Boolean estado = true;
}
