package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class TipoAtencionDTO {
    private Long idTipoAtencion;
    private String nombreTipoAtencion;
    private String descripcionTipoAtencion;
    private String estadoTipoAtencion;
    private Long idContrato;
    private Boolean estado = true;
}
