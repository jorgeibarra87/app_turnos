package com.turnos.enfermeria.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class DatosNotificacionEquipoDTO {
    private EquipoDTO equipo;
    private List<MiembroPerfilDTO> miembros;
    private List<CambiosEquipoDTO> historialEquipo;
    private List<CambiosPersonaEquipoDTO> historialPersonas;
}