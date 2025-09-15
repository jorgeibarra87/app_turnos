package com.turnos.enfermeria.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiosEquipoDTO {

    private Long idCambioEquipo;
    private Long idEquipo; // Solo el ID en lugar de la relación con la entidad

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCambio;

    private String tipoCambio; // 'CREACION', 'MODIFICACION', 'ELIMINACION'
    private String nombreAnterior;
    private String nombreNuevo;
    private Boolean estadoAnterior;
    private Boolean estadoNuevo;
    private String observaciones;
    private String usuarioCambio;

    // Campos adicionales para el frontend
    private String nombreEquipo; // Nombre actual del equipo
    private Boolean estadoActual; // Estado actual del equipo

    // Métodos de conveniencia
    public String getResumenCambio() {
        if ("CREACION".equals(tipoCambio)) {
            return "Equipo creado: " + (nombreNuevo != null ? nombreNuevo : "Sin nombre");
        } else if ("MODIFICACION".equals(tipoCambio)) {
            StringBuilder resumen = new StringBuilder("Modificación: ");
            if (nombreAnterior != null && !nombreAnterior.equals(nombreNuevo)) {
                resumen.append("Nombre: '").append(nombreAnterior).append("' → '").append(nombreNuevo).append("' ");
            }
            if (estadoAnterior != null && !estadoAnterior.equals(estadoNuevo)) {
                resumen.append("Estado: ").append(estadoAnterior ? "Activo" : "Inactivo")
                        .append(" → ").append(estadoNuevo ? "Activo" : "Inactivo");
            }
            return resumen.toString();
        } else if ("ELIMINACION".equals(tipoCambio)) {
            return "Equipo eliminado: " + (nombreAnterior != null ? nombreAnterior : "Sin nombre");
        }
        return tipoCambio;
    }
}
