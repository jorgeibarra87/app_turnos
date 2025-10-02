package com.turnos.enfermeria.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ContratoDTO {

    private Long idContrato;
    private String numContrato;
    private String supervisor;
    private String apoyoSupervision;
    private String objeto;
    private String contratista;
    private LocalDate fechaInicio;
    private LocalDate fechaTerminacion;
    private Integer anio;
    private String observaciones;
    private Boolean estado = true;
}
