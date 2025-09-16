package com.turnos.enfermeria.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CambioEquipoEvent {
    private final Long idEquipo;
    private final String tipoOperacion; // "CREACION_EQUIPO", "MODIFICACION_EQUIPO", "ELIMINACION_EQUIPO"
    private final String detallesOperacion;
    private final String usuarioResponsable;

    // Constructor sin usuario
    public CambioEquipoEvent(Long idEquipo, String tipoOperacion, String detallesOperacion) {
        this.idEquipo = idEquipo;
        this.tipoOperacion = tipoOperacion;
        this.detallesOperacion = detallesOperacion;
        this.usuarioResponsable = "SISTEMA";
    }
}