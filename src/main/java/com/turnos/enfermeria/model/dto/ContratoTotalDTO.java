package com.turnos.enfermeria.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ContratoTotalDTO {

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

    // IDs de las entidades relacionadas
    private List<Long> tiposAtencionIds;
    private List<Long> tiposTurnoIds;
    private List<Long> procesosIds;
    private List<Long> titulosIds;
}