package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.PersonaDTO;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.model.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public PersonaDTO toDTO(Usuario usuario) {
        Persona persona = usuario.getPersona();
        PersonaDTO dto = new PersonaDTO();

        if (persona != null) {
            dto.setIdPersona(persona.getIdPersona());
            dto.setNombreCompleto(persona.getNombreCompleto());
            dto.setDocumento(persona.getDocumento());
            dto.setEmail(persona.getEmail());
            dto.setTelefono(persona.getTelefono());
        }

        return dto;
    }

    public List<PersonaDTO> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
