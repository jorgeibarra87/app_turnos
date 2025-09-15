package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.PersonaDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.model.entity.Usuario;
import com.turnos.enfermeria.repository.PersonaRepository;
import com.turnos.enfermeria.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepo; // 👈 Agregado
    private final ModelMapper modelMapper;
    private final CambiosEquipoService cambiosEquipoService;

    public PersonaDTO create(PersonaDTO personaDTO) {
        Persona persona = modelMapper.map(personaDTO, Persona.class);

        // Guardar persona
        Persona personaGuardada = personaRepo.save(persona);

        // 👇 Crear y guardar también el Usuario
        Usuario usuario = new Usuario();
        usuario.setPersona(personaGuardada); // Relación uno a uno
        usuarioRepo.save(usuario);

        return modelMapper.map(personaGuardada, PersonaDTO.class);
    }

    public PersonaDTO update(PersonaDTO detallePersonaDTO, Long id) {
        Persona personaExistente = personaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

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

        // Guardar persona
        Persona personaActualizada = personaRepo.save(personaExistente);

        // 👇 Verificar si existe un usuario relacionado, sino crearlo
        Usuario usuario = usuarioRepo.findById(id)
                .orElseGet(() -> {
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setPersona(personaActualizada);
                    return nuevoUsuario;
                });
        usuario.setPersona(personaActualizada);
        usuarioRepo.save(usuario);

        return modelMapper.map(personaActualizada, PersonaDTO.class);
    }

    public Optional<PersonaDTO> findById(Long idPersona) {
        return personaRepo.findById(idPersona)
                .map(persona -> modelMapper.map(persona, PersonaDTO.class));
    }

    public List<PersonaDTO> findAll() {
        return personaRepo.findAll()
                .stream()
                .map(persona -> modelMapper.map(persona, PersonaDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long idPersona) {
        Persona personaEliminar = personaRepo.findById(idPersona)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        // 🔥 REGISTRAR DESVINCULACIÓN DE TODOS LOS EQUIPOS
        Usuario usuario = usuarioRepo.findById(idPersona).orElse(null);
        if (usuario != null && usuario.getEquipos() != null) {
            for (Equipo equipo : usuario.getEquipos()) {
                cambiosEquipoService.registrarCambioPersonaEquipo(
                        personaEliminar,
                        equipo, // equipoAnterior
                        null, // equipoNuevo = null porque se elimina la persona
                        "DESVINCULACION"
                );
            }
        }

        // 👇 Eliminar también el usuario asociado
        usuarioRepo.findById(idPersona)
                .ifPresent(usuarioRepo::delete);

        personaRepo.deleteById(idPersona);
    }
}

