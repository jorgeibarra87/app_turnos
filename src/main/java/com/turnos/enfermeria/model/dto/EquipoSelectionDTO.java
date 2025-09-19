package com.turnos.enfermeria.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para la selección de categoría y subcategoría
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipoSelectionDTO {
    private String categoria;
    private String subcategoria;
    private String observaciones;
}
