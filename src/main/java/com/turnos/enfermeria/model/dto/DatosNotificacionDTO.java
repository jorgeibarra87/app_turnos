package com.turnos.enfermeria.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class DatosNotificacionDTO {
    private CuadroTurnoDTO cuadroData;
    private List<MiembroPerfilDTO> miembros;
    private List<TurnoDTO> turnos;
    private List<CambioCuadroDTO> historialCuadro;
    private List<CambioTurnoDTO> historialTurnos;
    private List<ProcesoDTO> procesos;
}
