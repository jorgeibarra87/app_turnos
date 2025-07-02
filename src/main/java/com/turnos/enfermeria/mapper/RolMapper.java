package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.RolesDTO;
import com.turnos.enfermeria.model.dto.TitulosFormacionAcademicaDTO;
import com.turnos.enfermeria.model.entity.Roles;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RolMapper {
    public RolesDTO toDTO(Roles roles){
        if(roles == null) return null;

        RolesDTO dto = new RolesDTO();
        dto.setIdRol(roles.getIdRol());
        dto.setRol(roles.getRol());
        dto.setDescripcion(roles.getDescripcion());

        return dto;
    }

    public List<RolesDTO> toDTOList(List<Roles> roles) {
        return roles.stream()
                .map(this:: toDTO)
                .collect(Collectors.toList());
    }
}
