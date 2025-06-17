package com.turnos.enfermeria.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioDTO {

    @NotNull(message = "El ID de persona es obligatorio")
    private Long idPersona;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    private Boolean estado = true; // Valor por defecto
//    private Long idRol;
//    private Long idTitulo;
//    private Long idEquipo;

}
