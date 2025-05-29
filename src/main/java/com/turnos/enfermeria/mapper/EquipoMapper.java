package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.EquipoDTO;
import com.turnos.enfermeria.model.entity.Equipo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EquipoMapper {

    public EquipoDTO toDTO(Equipo equipo) {
        if (equipo == null) return null;

        EquipoDTO dto = new EquipoDTO();
        dto.setIdEquipo(equipo.getIdEquipo());
        dto.setNombre(equipo.getNombre());
//        dto.setIdCuadroTurno(
//                equipo.getCuadroTurno() != null ? equipo.getCuadroTurno().getIdCuadroTurno() : null
//        );

        return dto;
    }

    public List<EquipoDTO> toDTOList(List<Equipo> equipos) {
        return equipos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
