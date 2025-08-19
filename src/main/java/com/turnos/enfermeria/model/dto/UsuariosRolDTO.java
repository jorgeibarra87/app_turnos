package com.turnos.enfermeria.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class UsuariosRolDTO {
    private Long idPersona;
    private String nombreCompleto;
    private String documento;
    private List<RolesDTO> roles;
}
