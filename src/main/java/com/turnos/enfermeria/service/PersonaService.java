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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PersonaService {

    private final PersonaRepository personaRepo;
    private final UsuarioRepository usuarioRepo;
    private final ModelMapper modelMapper;
    private final CambiosEquipoService cambiosEquipoService;

//    public PersonaDTO create(PersonaDTO personaDTO) {
//        Persona persona = modelMapper.map(personaDTO, Persona.class);
//
//        // Guardar persona
//        Persona personaGuardada = personaRepo.save(persona);
//
//        // 👇 Crear y guardar también el Usuario
//        Usuario usuario = new Usuario();
//        usuario.setPersona(personaGuardada); // Relación uno a uno
//        usuarioRepo.save(usuario);
//
//        return modelMapper.map(personaGuardada, PersonaDTO.class);
//    }

    public PersonaDTO create(PersonaDTO personaDTO) {
        // Buscar si ya existe por documento
        Optional<Persona> personaExistente = personaRepo.findByDocumento(personaDTO.getDocumento());

        if (personaExistente.isPresent()) {
            //Si existe, usar el método update
            Long idPersonaExistente = personaExistente.get().getIdPersona();
            return update(personaDTO, idPersonaExistente);
        }

        // Si no existe, crear nueva (código original)
        Persona persona = modelMapper.map(personaDTO, Persona.class);
        persona.setIdPersona(null); // Asegurar que sea null

        Persona personaGuardada = personaRepo.save(persona);

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setPersona(personaGuardada);
        usuarioRepo.save(usuario);

        return modelMapper.map(personaGuardada, PersonaDTO.class);
    }

    @Transactional
    public PersonaDTO update(PersonaDTO detallePersonaDTO, Long id) {
        // Obtener la entidad
        Persona personaExistente = personaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        // Actualizar campos
        if (detallePersonaDTO.getFechaNacimiento() != null) {
            personaExistente.setFechaNacimiento(detallePersonaDTO.getFechaNacimiento());
        }
        if (detallePersonaDTO.getApellidos() != null && !detallePersonaDTO.getApellidos().trim().isEmpty()) {
            personaExistente.setApellidos(detallePersonaDTO.getApellidos());
        }
        if (detallePersonaDTO.getDocumento() != null && !detallePersonaDTO.getDocumento().trim().isEmpty()) {
            personaExistente.setDocumento(detallePersonaDTO.getDocumento());
        }
        if (detallePersonaDTO.getEmail() != null && !detallePersonaDTO.getEmail().trim().isEmpty()) {
            personaExistente.setEmail(detallePersonaDTO.getEmail());
        }
        if (detallePersonaDTO.getNombreCompleto() != null && !detallePersonaDTO.getNombreCompleto().trim().isEmpty()) {
            personaExistente.setNombreCompleto(detallePersonaDTO.getNombreCompleto());
        }
        if (detallePersonaDTO.getNombres() != null && !detallePersonaDTO.getNombres().trim().isEmpty()) {
            personaExistente.setNombres(detallePersonaDTO.getNombres());
        }

        // Guardar
        Persona personaActualizada = personaRepo.save(personaExistente);

        // Manejar usuario
        Usuario usuario = usuarioRepo.findById(id).orElse(null);
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setPersona(personaActualizada);
            usuarioRepo.save(usuario);
        }

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

