package com.turnos.enfermeria.model.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class PersonaDTO {

    private Long idPersona;
    private Date fechaNacimiento;
    private String apellidos;
    private String documento;
    private String email;
    private String nombreCompleto;
    private String nombres;
    private String telefono;
    private Boolean estado = true;
}
