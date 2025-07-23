package com.turnos.enfermeria.model.dto;

import com.turnos.enfermeria.model.entity.Equipo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class PersonaEquipoDTO {
    private Long idPersona;
    private String nombreCompleto;
    private String documento;
    private List<EquipoDTO> equipos = new ArrayList<>();

    public PersonaEquipoDTO(Long idPersona, String nombreCompleto, String documento, Equipo equipo) {
        this.idPersona = idPersona;
        this.nombreCompleto = nombreCompleto;
        this.documento = documento;
        this.equipos.add(new EquipoDTO(equipo.getIdEquipo(), equipo.getNombre(), equipo.getEstado()));
    }

    public PersonaEquipoDTO() {}
}