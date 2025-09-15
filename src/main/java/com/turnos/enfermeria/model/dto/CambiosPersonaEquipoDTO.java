package com.turnos.enfermeria.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiosPersonaEquipoDTO {

    private Long idCambioPersonaEquipo;
    private Long idPersona;
    private Long idEquipo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCambio;

    private String tipoCambio; // 'ASIGNACION', 'REASIGNACION', 'DESVINCULACION'
    private Long equipoAnteriorId;
    private Long equipoNuevoId;
    private String observaciones;
    private String usuarioCambio;

    // Campos adicionales para el frontend
    private String nombrePersona; // Nombre completo de la persona
    private String documentoPersona; // Documento de la persona
    private String nombreEquipo; // Nombre del equipo actual
    private String nombreEquipoAnterior; // Nombre del equipo anterior
    private String nombreEquipoNuevo; // Nombre del equipo nuevo

    // Métodos de conveniencia
    public String getResumenCambio() {
        String persona = nombrePersona != null ? nombrePersona : "Persona ID: " + idPersona;

        if ("ASIGNACION".equals(tipoCambio)) {
            return persona + " asignado al equipo " +
                    (nombreEquipoNuevo != null ? nombreEquipoNuevo : "ID: " + equipoNuevoId);
        } else if ("REASIGNACION".equals(tipoCambio)) {
            return persona + " reasignado de " +
                    (nombreEquipoAnterior != null ? nombreEquipoAnterior : "ID: " + equipoAnteriorId) +
                    " a " + (nombreEquipoNuevo != null ? nombreEquipoNuevo : "ID: " + equipoNuevoId);
        } else if ("DESVINCULACION".equals(tipoCambio)) {
            return persona + " desvinculado del equipo " +
                    (nombreEquipoAnterior != null ? nombreEquipoAnterior : "ID: " + equipoAnteriorId);
        }
        return tipoCambio + " - " + persona;
    }

    public boolean esMovimientoEntreEquipos() {
        return "REASIGNACION".equals(tipoCambio) && equipoAnteriorId != null && equipoNuevoId != null;
    }
}
