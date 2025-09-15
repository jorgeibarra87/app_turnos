package com.turnos.enfermeria.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class CambioCuadroEvent {
    private final Long idCuadroTurno;
    private final String tipoOperacion;
    private final String detallesOperacion;
    private final String usuarioResponsable; // opcional

    // Constructor sin usuario (para compatibilidad)
    public CambioCuadroEvent(Long idCuadroTurno, String tipoOperacion, String detallesOperacion) {
        this.idCuadroTurno = idCuadroTurno;
        this.tipoOperacion = tipoOperacion;
        this.detallesOperacion = detallesOperacion;
        this.usuarioResponsable = "SISTEMA"; // valor por defecto
    }
}
