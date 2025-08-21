package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.PersonaTituloDTO;
import com.turnos.enfermeria.model.dto.TitulosFormacionAcademicaDTO;
import com.turnos.enfermeria.model.dto.UsuariosTituloDTO;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.model.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuariosTituloMapper {
    public PersonaTituloDTO toDTO(Usuario usuario) {
        Persona persona = usuario.getPersona();
        PersonaTituloDTO dto = new PersonaTituloDTO();

        if (persona != null) {
            dto.setIdPersona(persona.getIdPersona());
            dto.setNombreCompleto(persona.getNombreCompleto());
            dto.setDocumento(persona.getDocumento());
           //dto.setTelefono(persona.getTelefono());
        }

        List<TitulosFormacionAcademicaDTO> titulosDTO = usuario.getTitulosFormacionAcademica().stream().map(titulo -> {
            TitulosFormacionAcademicaDTO t = new TitulosFormacionAcademicaDTO();
            t.setIdTitulo(titulo.getIdTitulo());
            t.setTitulo(titulo.getTitulo());
            t.setIdTipoFormacionAcademica(titulo.getIdTipoFormacionAcademica());
            return t;
        }).collect(Collectors.toList());

        dto.setTitulos(titulosDTO);

        return dto;
    }

    public List<PersonaTituloDTO> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
