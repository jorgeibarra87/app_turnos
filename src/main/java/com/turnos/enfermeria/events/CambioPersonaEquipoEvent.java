package com.turnos.enfermeria.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CambioPersonaEquipoEvent {
    private final Long idPersona;
    private final Long idEquipo;
    private final String tipoOperacion; // "ASIGNACION_PERSONA", "DESVINCULACION_PERSONA", "REASIGNACION_PERSONA"
    private final String detallesOperacion;
    private final String usuarioResponsable;

    public CambioPersonaEquipoEvent(Long idPersona, Long idEquipo, String tipoOperacion, String detallesOperacion) {
        this.idPersona = idPersona;
        this.idEquipo = idEquipo;
        this.tipoOperacion = tipoOperacion;
        this.detallesOperacion = detallesOperacion;
        this.usuarioResponsable = "SISTEMA";
    }
}