package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.UsuarioContratoDTO;
import com.turnos.enfermeria.model.dto.UsuarioContratoTotalDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UsuarioContratoService {

    private final UsuarioContratoRepository usuarioContratoRepository;
    private final ModelMapper modelMapper;
    private final PersonaRepository personaRepository;
    private final TitulosFormacionAcademicaRepository titulosFormacionAcademicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContratoRepository contratoRepository;
    private final RolesRepository rolesRepository;

    public UsuarioContratoDTO create(UsuarioContratoDTO usuarioContratoDTO) {

        Usuario usuario = usuarioRepository.findById(usuarioContratoDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("El usuario es obligatorio."));

        Contrato contrato = contratoRepository.findById(usuarioContratoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("El contrato es obligatorio."));

        Roles roles = rolesRepository.findById(usuarioContratoDTO.getIdRol())
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));

        UsuarioContrato usuarioContrato = modelMapper.map(usuarioContratoDTO, UsuarioContrato.class);
        usuarioContrato.setIdUsuarioContrato(usuarioContratoDTO.getIdUsuarioContrato());
        usuarioContrato.setUsuario(usuario);
        usuarioContrato.setContrato(contrato);
        usuarioContrato.setRoles(roles);

        UsuarioContrato usuarioContratoGuardado = usuarioContratoRepository.save(usuarioContrato);
        return modelMapper.map(usuarioContratoGuardado, UsuarioContratoDTO.class);
    }

    public UsuarioContratoDTO update(UsuarioContratoDTO detalleUsuarioContratoDTO, Long id) {
        UsuarioContrato usuarioContratoExistente = usuarioContratoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("usuario no encontrado"));

        Usuario usuario = usuarioRepository.findById(detalleUsuarioContratoDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("El usuario es obligatorio."));

        Contrato contrato = contratoRepository.findById(detalleUsuarioContratoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("El contrato es obligatorio."));

        UsuarioContratoDTO usuarioContratoDTO = modelMapper.map(usuarioContratoExistente, UsuarioContratoDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleUsuarioContratoDTO.getIdUsuarioContrato()!= null) {
            usuarioContratoExistente.setIdUsuarioContrato(detalleUsuarioContratoDTO.getIdUsuarioContrato());
        }
        if (detalleUsuarioContratoDTO.getIdPersona() != null) {
            usuarioContratoExistente.setUsuario(usuario);
        }
        if (detalleUsuarioContratoDTO.getIdContrato() != null) {
            usuarioContratoExistente.setContrato(contrato);
        }

        // Guardar en la base de datos
        UsuarioContrato usuarioContratoActualizado = usuarioContratoRepository.save(usuarioContratoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(usuarioContratoActualizado, UsuarioContratoDTO.class);
    }

    public Optional<UsuarioContratoDTO> findById(Long idUsuarioContrato) {
        return usuarioContratoRepository.findById(idUsuarioContrato)
                .map(usuarioContrato -> modelMapper.map(usuarioContrato, UsuarioContratoDTO.class)); // Convertir a DTO
    }

    public List<UsuarioContratoDTO> findAll() {
        return usuarioContratoRepository.findAll()
                .stream()
                .map(usuarioContrato -> modelMapper.map(usuarioContrato, UsuarioContratoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idUsuarioContrato) {
        // Buscar el bloque en la base de datos
        UsuarioContrato usuarioContratoEliminar = usuarioContratoRepository.findById(idUsuarioContrato)
                .orElseThrow(() -> new EntityNotFoundException("usuario no encontrado"));

        // Convertir la entidad a DTO
        UsuarioContratoDTO usuarioContratoDTO = modelMapper.map(usuarioContratoEliminar, UsuarioContratoDTO.class);

        usuarioContratoRepository.deleteById(idUsuarioContrato);
    }


    public UsuarioContratoTotalDTO obtenerInformacionUsuarioCompleta(String documento) {
        List<UsuarioContratoTotalDTO> resultados = usuarioContratoRepository.findAllUsuarioInfoByDocumento(documento);

        if (resultados.isEmpty()) {
            throw new EntityNotFoundException("Usuario no encontrado con documento: " + documento);
        }

        UsuarioContratoTotalDTO base = resultados.get(0);

        String profesiones = resultados.stream()
                .map(UsuarioContratoTotalDTO::getProfesion)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        String contratos = resultados.stream()
                .map(UsuarioContratoTotalDTO::getContrato)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        String roles = resultados.stream()
                .map(UsuarioContratoTotalDTO::getRol)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        return new UsuarioContratoTotalDTO(
                base.getDocumento(),
                base.getNombre(),
                base.getTelefono(),
                base.getEmail(),
                profesiones.isEmpty() ? "Sin profesión" : profesiones,
                contratos.isEmpty() ? "Sin contrato" : contratos,
                roles.isEmpty() ? "Sin rol" : roles
        );
    }

    @Transactional
    public UsuarioContratoTotalDTO saveUsuarioContratoCompleto(UsuarioContratoTotalDTO usuarioContratoTotalDTO) {
        try {
            // 1. Buscar o crear la persona
            Persona persona = personaRepository.findByDocumento(usuarioContratoTotalDTO.getDocumento())
                    .orElseGet(() -> {
                        Persona nuevaPersona = new Persona();
                        nuevaPersona.setDocumento(usuarioContratoTotalDTO.getDocumento());
                        nuevaPersona.setNombreCompleto(usuarioContratoTotalDTO.getNombre());
                        nuevaPersona.setTelefono(usuarioContratoTotalDTO.getTelefono());
                        nuevaPersona.setEmail(usuarioContratoTotalDTO.getEmail());
                        nuevaPersona.setEstado(true);
                        return personaRepository.save(nuevaPersona);
                    });

            // 2. Buscar o crear el usuario
            Usuario usuario = usuarioRepository.findByPersona_IdPersona(persona.getIdPersona());
//                    .orElseGet(() -> {
//                        Usuario nuevoUsuario = new Usuario();
//                        nuevoUsuario.setPersona(persona);
//                        nuevoUsuario.setEstado(true);
//                        // Aquí puedes agregar otros campos del usuario si es necesario
//                        return usuarioRepository.save(nuevoUsuario);
//                    });

            // 3. Procesar y guardar títulos de formación académica
            if (usuarioContratoTotalDTO.getProfesion() != null &&
                    !usuarioContratoTotalDTO.getProfesion().equals("Sin profesión")) {

                String[] profesiones = usuarioContratoTotalDTO.getProfesion().split(", ");
                for (String profesion : profesiones) {
                    if (!profesion.trim().isEmpty()) {
                        // Verificar si ya existe la relación
//                        boolean existeRelacion = titulosFormacionAcademicaRepository
//                                .existsByUsuario_IdPersonaAndTitulo(persona.getIdPersona(), profesion.trim());

                        TitulosFormacionAcademica titulo = new TitulosFormacionAcademica();
                        titulo.setTitulo(profesion.trim());
                        titulo.setEstado(true);
                        titulosFormacionAcademicaRepository.save(titulo);
//                        if (!existeRelacion) {
//                            TitulosFormacionAcademica titulo = new TitulosFormacionAcademica();
////                            titulo.setUsuario(usuario);
//                            titulo.setTitulo(profesion.trim());
//                            titulo.setEstado(true);
//                            titulosFormacionAcademicaRepository.save(titulo);
//                        }
                    }
                }
            }

            // 4. Procesar y guardar contratos
            if (usuarioContratoTotalDTO.getContrato() != null &&
                    !usuarioContratoTotalDTO.getContrato().equals("Sin contrato")) {

                String[] contratos = usuarioContratoTotalDTO.getContrato().split(", ");
                String[] rolesArray = usuarioContratoTotalDTO.getRol() != null &&
                        !usuarioContratoTotalDTO.getRol().equals("Sin rol") ?
                        usuarioContratoTotalDTO.getRol().split(", ") : new String[]{};

                for (int i = 0; i < contratos.length; i++) {
                    String numeroContrato = contratos[i].trim();
                    if (!numeroContrato.isEmpty()) {
                        // Buscar o crear el contrato
                        Contrato contrato = contratoRepository.findByNumContrato(numeroContrato)
                                .orElseGet(() -> {
                                    Contrato nuevoContrato = new Contrato();
                                    nuevoContrato.setNumContrato(numeroContrato);
                                    nuevoContrato.setEstado(true);
                                    // Aquí puedes agregar otros campos del contrato si es necesario
                                    return contratoRepository.save(nuevoContrato);
                                });

                        // Determinar el rol para este contrato
                        Roles rol = null;
                        if (i < rolesArray.length && !rolesArray[i].trim().isEmpty()) {
                            int finalI = i;
                            rol = rolesRepository.findByRol(rolesArray[i].trim())
                                    .orElseGet(() -> {
                                        Roles nuevoRol = new Roles();
                                        nuevoRol.setRol(rolesArray[finalI].trim());
                                        nuevoRol.setEstado(true);
                                        return rolesRepository.save(nuevoRol);
                                    });
                        }

                        // Verificar si ya existe la relación usuario-contrato
                        boolean existeUsuarioContrato = usuarioContratoRepository
                                .existsByUsuario_IdPersonaAndContrato_IdContrato(
                                        persona.getIdPersona(),
                                        contrato.getIdContrato()
                                );

                        if (!existeUsuarioContrato) {
                            UsuarioContrato usuarioContrato = new UsuarioContrato();
                            usuarioContrato.setUsuario(usuario);
                            usuarioContrato.setContrato(contrato);
                            usuarioContrato.setRoles(rol);
                            usuarioContrato.setEstado(true);
                            usuarioContratoRepository.save(usuarioContrato);
                        }
                    }
                }
            }

            // 5. Retornar la información completa actualizada
            return obtenerInformacionUsuarioCompleta(usuarioContratoTotalDTO.getDocumento());

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el usuario contrato completo: " + e.getMessage(), e);
        }
    }

    // Método auxiliar para actualizar información existente
    @Transactional
    public UsuarioContratoTotalDTO updateUsuarioContratoCompleto(String documento,
                                                                 UsuarioContratoTotalDTO usuarioContratoTotalDTO) {
        try {
            // Buscar la persona existente
            Persona persona = personaRepository.findByDocumento(documento)
                    .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada con documento: " + documento));

            // Actualizar datos de la persona
            persona.setNombreCompleto(usuarioContratoTotalDTO.getNombre());
            persona.setTelefono(usuarioContratoTotalDTO.getTelefono());
            persona.setEmail(usuarioContratoTotalDTO.getEmail());
            personaRepository.save(persona);

            // Buscar el usuario
            Usuario usuario = usuarioRepository.findById(persona.getIdPersona())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            // Actualizar títulos de formación académica
            if (usuarioContratoTotalDTO.getProfesion() != null &&
                    !usuarioContratoTotalDTO.getProfesion().equals("Sin profesión")) {

                // Eliminar títulos existentes
                //titulosFormacionAcademicaRepository.deleteByUsuario_IdPersona(usuario.getIdPersona());

                // Agregar nuevos títulos
                String[] profesiones = usuarioContratoTotalDTO.getProfesion().split(", ");
                for (String profesion : profesiones) {
                    if (!profesion.trim().isEmpty()) {
                        TitulosFormacionAcademica titulo = new TitulosFormacionAcademica();
                        //titulo.setUsuario(usuario);
                        titulo.setTitulo(profesion.trim());
                        titulo.setEstado(true);
                        titulosFormacionAcademicaRepository.save(titulo);
                    }
                }
            }

            // Retornar la información actualizada
            return obtenerInformacionUsuarioCompleta(documento);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el usuario contrato completo: " + e.getMessage(), e);
        }
    }
}
