package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.PersonaEquipoDTO;
import com.turnos.enfermeria.model.dto.UsuariosEquipoDTO;
import com.turnos.enfermeria.model.dto.UsuariosRolDTO;
import com.turnos.enfermeria.model.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuariosEquipoMapper {

    @Autowired
    private EquipoMapper equipoMapper;

    public PersonaEquipoDTO toDTO(Usuario usuario) {
        PersonaEquipoDTO dto = new PersonaEquipoDTO();
        dto.setIdPersona(usuario.getIdPersona());
        dto.setNombreCompleto(
                usuario.getPersona() != null ? usuario.getPersona().getNombreCompleto() : null
        );
        dto.setDocumento(
                usuario.getPersona().getDocumento() != null ? usuario.getPersona().getDocumento() : null
        );
        // Mapear la lista de Equipo a EquipoDTO
        if (usuario.getEquipos() != null) {
            dto.setEquipos(usuario.getEquipos().stream()
                    .map(equipoMapper::toDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setEquipos(null);
        }
        return dto;
    }
    public List<PersonaEquipoDTO> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
