package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.PersonaDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.repository.PersonaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PersonaService {

    private final PersonaRepository personaRepo;
    private final ModelMapper modelMapper;

    public PersonaDTO create(PersonaDTO personaDTO) {
        Persona persona = modelMapper.map(personaDTO, Persona.class);
        persona.setIdPersona(personaDTO.getIdPersona());
        persona.setFechaNacimiento(personaDTO.getFechaNacimiento());
        persona.setApellidos(personaDTO.getApellidos());
        persona.setDocumento(personaDTO.getDocumento());
        persona.setEmail(personaDTO.getEmail());
        persona.setNombreCompleto(personaDTO.getNombreCompleto());
        persona.setNombres(personaDTO.getNombres());

        Persona personaGuardado = personaRepo.save(persona);

        return modelMapper.map(personaGuardado, PersonaDTO.class);

    }

    public PersonaDTO update(PersonaDTO detallePersonaDTO, Long id) {
        Persona personaExistente = personaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        PersonaDTO personaDTO = modelMapper.map(personaExistente, PersonaDTO.class);

        // Actualizar los campos si no son nulos
        if (detallePersonaDTO.getIdPersona()!= null) {
            personaExistente.setIdPersona(detallePersonaDTO.getIdPersona());
        }
        if (detallePersonaDTO.getFechaNacimiento() != null) {
            personaExistente.setFechaNacimiento(detallePersonaDTO.getFechaNacimiento());
        }
        if (detallePersonaDTO.getApellidos()!= null) {
            personaExistente.setApellidos(detallePersonaDTO.getApellidos());
        }
        if (detallePersonaDTO.getDocumento()!= null) {
            personaExistente.setDocumento(detallePersonaDTO.getDocumento());
        }
        if (detallePersonaDTO.getEmail()!= null) {
            personaExistente.setEmail(detallePersonaDTO.getEmail());
        }
        if (detallePersonaDTO.getNombreCompleto()!= null) {
            personaExistente.setNombreCompleto(detallePersonaDTO.getNombreCompleto());
        }
        if (detallePersonaDTO.getNombres()!= null) {
            personaExistente.setNombres(detallePersonaDTO.getNombres());
        }

        // Guardar en la base de datos
        Persona personaActualizada = personaRepo.save(personaExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(personaActualizada, PersonaDTO.class);
    }

    public Optional<PersonaDTO> findById(Long idPersona) {
        return personaRepo.findById(idPersona)
                .map(persona -> modelMapper.map(persona, PersonaDTO.class)); // Convertir a DTO
    }

    public List<PersonaDTO> findAll() {
        return personaRepo.findAll()
                .stream()
                .map(persona -> modelMapper.map(persona, PersonaDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long idPersona) {
        // Buscar el bloque en la base de datos
        Persona prsonaEliminar = personaRepo.findById(idPersona)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        // Convertir la entidad a DTO
        PersonaDTO bloqueServicioDTO = modelMapper.map(prsonaEliminar, PersonaDTO.class);

        personaRepo.deleteById(idPersona);
    }
}

