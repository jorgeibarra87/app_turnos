package com.turnos.enfermeria.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PersonaEquipoDTO {
    private Long idPersona;
    private String nombreCompleto;
    private String documento;
    //private String telefono;
    private List<EquipoDTO> equipos;
}
