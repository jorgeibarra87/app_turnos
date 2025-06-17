package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class TipoFormacionAcademicaDTO {
    private Long idTipoFormacionAcademica;
    private String tipo;
    private Boolean estado = true;
}
