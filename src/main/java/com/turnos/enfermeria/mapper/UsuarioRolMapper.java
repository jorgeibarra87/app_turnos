package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.PersonaRolDTO;
import com.turnos.enfermeria.model.dto.RolesDTO;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.model.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioRolMapper {
    public PersonaRolDTO toDTO(Usuario usuario) {
        Persona persona = usuario.getPersona();
        PersonaRolDTO dto = new PersonaRolDTO();

        if (persona != null) {
            dto.setIdPersona(persona.getIdPersona());
            dto.setNombreCompleto(persona.getNombreCompleto());
        }

        List<RolesDTO> rolesDTO = usuario.getRoles().stream().map(rol -> {
            RolesDTO t = new RolesDTO();
            t.setIdRol(rol.getIdRol());
            t.setRol(rol.getRol());
            t.setDescripcion(rol.getDescripcion());
            return t;
        }).collect(Collectors.toList());

        dto.setRoles(rolesDTO);


        return dto;
    }
}
