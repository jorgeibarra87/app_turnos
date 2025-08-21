package com.turnos.enfermeria.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioContratoTotalDTO {

    private String documento;
    private String nombre;
    private String telefono;
    private String email;
    private String profesion;
    private String contrato;
    private String rol;
}
