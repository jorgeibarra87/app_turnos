package com.turnos.enfermeria.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiembroPerfilDTO {
    private Long idPersona;
    private String nombreCompleto;
    private List<String> titulos;
}
