package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class UsuariosEquipoDTO {
    private Long idPersona;
    private String nombreCompleto;
    private String documento;
}
