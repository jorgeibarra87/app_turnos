package com.turnos.enfermeria.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CambioTurnoEvent {
    private Long idTurno;
    private String tipoOperacion;
    private String detallesOperacion;
    private String usuarioResponsable; // opcional
}
