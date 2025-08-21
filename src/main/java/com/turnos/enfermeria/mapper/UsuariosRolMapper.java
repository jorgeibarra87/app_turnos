package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.RolesDTO;
import com.turnos.enfermeria.model.dto.UsuariosRolDTO;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.model.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuariosRolMapper {

    public UsuariosRolDTO toDTO(Usuario usuario) {
        Persona persona = usuario.getPersona();
        UsuariosRolDTO dto = new UsuariosRolDTO();

        if (persona != null) {
            dto.setIdPersona(persona.getIdPersona());
            dto.setNombreCompleto(persona.getNombreCompleto());
            dto.setDocumento(persona.getDocumento());
        }

        List<RolesDTO> rolesDTO = usuario.getRoles().stream().map(rol -> {
            RolesDTO t = new RolesDTO();
            t.setIdRol(rol.getIdRol());
            t.setRol(rol.getRol());
            return t;
        }).collect(Collectors.toList());

        dto.setRoles(rolesDTO);

        return dto;
    }

    public List<UsuariosRolDTO> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
