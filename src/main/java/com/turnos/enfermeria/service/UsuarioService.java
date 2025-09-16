package com.turnos.enfermeria.service;

import com.turnos.enfermeria.events.CambioPersonaEquipoEvent;
import com.turnos.enfermeria.mapper.*;
import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UsuarioService {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepo;
    private final PersonaRepository personaRepository;
    private final RolesRepository rolesRepository;
    private final TitulosFormacionAcademicaRepository titulosFormacionAcademicaRepository;
    private final EquipoRepository equipoRepository;
    private final ModelMapper modelMapper;
    private final UsuariosEquipoMapper usuariosEquipoMapper;
    private final UsuariosTituloMapper usuariosTituloMapper;
    private final TituloFormacionAcademicaMapper tituloFormacionAcademicaMapper;
    private final EquipoMapper equipoMapper;
    private final RolMapper rolMapper;
    private final UsuarioRolMapper usuarioRolMapper;
    private final UsuarioMapper usuarioMapper;
    private final CambiosEquipoService cambiosEquipoService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UsuarioDTO create(UsuarioDTO usuarioDTO) {
        // Verificar que la persona exista
        Persona persona = personaRepository.findById(usuarioDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("No existe persona con ID: " + usuarioDTO.getIdPersona()));

        // Verificar si ya tiene usuario
        Optional<Usuario> usuarioExistente = usuarioRepo.findById(usuarioDTO.getIdPersona());

        if (usuarioExistente.isPresent()) {
            throw new IllegalStateException("La persona ya tiene un usuario asociado");
        }
        // Crear nuevo usuario con mismo ID de persona
        Usuario usuario = new Usuario();
        //usuario.setIdPersona(usuarioDTO.getIdPersona()); // Mismo ID que la persona
        usuario.setPersona(persona); // Establecer relación 1:1
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setEstado(usuarioDTO.getEstado() != null ? usuarioDTO.getEstado() : true);

        // Guardar el usuario
        Usuario usuarioGuardado = usuarioRepo.save(usuario);

        return modelMapper.map(usuarioGuardado, UsuarioDTO.class);
    }


    public UsuarioDTO update(UsuarioDTO detalleUsuarioDTO, Long id) {

        Persona persona = personaRepository.findById(detalleUsuarioDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("No existe persona con ID: " + detalleUsuarioDTO.getIdPersona()));

        Usuario usuarioExistente = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("usuario no encontrado"));

        UsuarioDTO usuarioDTO = modelMapper.map(usuarioExistente, UsuarioDTO.class);


        if (detalleUsuarioDTO.getIdPersona() != null) {
            usuarioExistente.setPersona(persona);
        }
        if (detalleUsuarioDTO.getEstado() != null) {
            usuarioExistente.setEstado(detalleUsuarioDTO.getEstado());
        }
        if (detalleUsuarioDTO.getPassword() != null) {
            usuarioExistente.setPassword(passwordEncoder.encode(detalleUsuarioDTO.getPassword()));
        }
//        if (detalleUsuarioDTO.getIdRol() != null) {
//            usuarioExistente.setRoles((List<Roles>) roles);
//        }
//        if (detalleUsuarioDTO.getIdTitulo() != null) {
//            usuarioExistente.setTitulosFormacionAcademica((List<TitulosFormacionAcademica>) titulosFormacionAcademica);
//        }
//        if (detalleUsuarioDTO.getIdEquipo() != null) {
//            usuarioExistente.setEquipos((List<Equipo>) equipo);
//        }

        // Guardar en la base de datos
        Usuario usuarioActualizado = usuarioRepo.save(usuarioExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(usuarioActualizado, UsuarioDTO.class);
    }

    public Optional<UsuarioDTO> findById(Long idPersona) {
        return usuarioRepo.findById(idPersona)
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class)); // Convertir a DTO
    }

    public List<UsuarioDTO> findAll() {
        return usuarioRepo.findAll()
                .stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idPersona) {
        // Buscar el bloque en la base de datos
        Usuario usuarioEliminar = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new EntityNotFoundException("usuario no encontrado"));

        // Convertir la entidad a DTO
        UsuarioDTO usuarioDTO = modelMapper.map(usuarioEliminar, UsuarioDTO.class);

        usuarioRepo.deleteById(idPersona);
    }

    public PersonaEquipoDTO agregarEquipoAUsuario(Long idPersona, Long idEquipo) {
        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // 🔥 REGISTRAR CAMBIO ANTES DE AGREGAR
        if (!usuario.getEquipos().contains(equipo)) {
            cambiosEquipoService.registrarCambioPersonaEquipo(
                    usuario.getPersona(),
                    null, // equipoAnterior = null porque es una nueva asignación
                    equipo, // equipoNuevo
                    "ASIGNACION"
            );
        }

        usuario.getEquipos().add(equipo);
        Usuario usuarioActualizado = usuarioRepo.save(usuario);

        return usuariosEquipoMapper.toDTO(usuarioActualizado);
    }

    public EquipoDTO agregarUsuarioAEquipo(Long idEquipo, Long idPersona) {
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.getEquipos().add(equipo); // o equipo.getUsuarios().add(usuario); según cómo esté mapeado
        usuarioRepo.save(usuario); // O equipoRepository.save(equipo) si el dueño es Equipo

        return equipoMapper.toDTO(equipo);
    }

    public List<EquipoDTO> obtenerEquiposDeUsuario(Long idPersona) {
        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return equipoMapper.toDTOList(usuario.getEquipos());
    }



    public List<PersonaEquipoDTO> obtenerUsuariosPorEquipo(Long idEquipo) {
        List<Usuario> usuarios = usuarioRepo.findDistinctByEquipos_IdEquipo(idEquipo);

        List<PersonaEquipoDTO> resultado = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            Persona persona = usuario.getPersona();

            PersonaEquipoDTO dto = new PersonaEquipoDTO();
            dto.setIdPersona(persona.getIdPersona());
            dto.setNombreCompleto(persona.getNombreCompleto());
            dto.setDocumento(persona.getDocumento());

            // Para evitar duplicados
            List<EquipoDTO> equipos = usuario.getEquipos().stream()
                    .map(equipo -> new EquipoDTO(equipo.getIdEquipo(), equipo.getNombre(), equipo.getEstado()))
                    .distinct()
                    .collect(Collectors.toList());

            dto.setEquipos(equipos);

            resultado.add(dto);
        }

        return resultado;
    }

    @Transactional
    public EquipoDTO actualizarUsuariosDeEquipo(Long idEquipo, List<Long> nuevosUsuariosIds) {
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        //OBTENER SOLO USUARIOS ACTUALES DE ESTE EQUIPO
        List<Usuario> usuariosActualesDelEquipo = usuarioRepo.findDistinctByEquipos_IdEquipo(idEquipo);

        // LIMPIAR LAS RELACIONES DE ESTE EQUIPO
        for (Usuario usuario : usuariosActualesDelEquipo) {
            // Registrar desvinculación si no va a seguir asignado
            boolean sigueAsignado = nuevosUsuariosIds.contains(usuario.getPersona().getIdPersona());
            if (!sigueAsignado) {
                cambiosEquipoService.registrarCambioPersonaEquipo(
                        usuario.getPersona(),
                        equipo, // equipoAnterior
                        null, // equipoNuevo = null
                        "DESVINCULACION"
                );

                // PUBLICAR EVENTO DE DESVINCULACIÓN
                eventPublisher.publishEvent(new CambioPersonaEquipoEvent(
                        usuario.getPersona().getIdPersona(),
                        idEquipo,
                        "DESVINCULACIÓN DE PERSONA",
                        "Se ha desvinculado a " + usuario.getPersona().getNombreCompleto() +
                                " del equipo " + equipo.getNombre()
                ));
            }

            // remover ESTE equipo específico del usuario
            usuario.getEquipos().remove(equipo);
        }

        // Guardar usuarios actuales (con relaciones removidas)
        usuarioRepo.saveAll(usuariosActualesDelEquipo);

        //OBTENER Y PROCESAR NUEVOS USUARIOS
        List<Usuario> nuevosUsuarios = usuarioRepo.findAllById(nuevosUsuariosIds);
        for (Usuario usuario : nuevosUsuarios) {
            // Registrar nueva asignación solo si no estaba previamente asignado
            boolean yaEstabaAsignado = usuariosActualesDelEquipo.stream()
                    .anyMatch(u -> u.getPersona().getIdPersona().equals(usuario.getPersona().getIdPersona()));

            if (!yaEstabaAsignado) {
                cambiosEquipoService.registrarCambioPersonaEquipo(
                        usuario.getPersona(),
                        null, // equipoAnterior = null
                        equipo, // equipoNuevo
                        "ASIGNACION"
                );

                // PUBLICAR EVENTO DE ASIGNACIÓN
                eventPublisher.publishEvent(new CambioPersonaEquipoEvent(
                        usuario.getPersona().getIdPersona(),
                        idEquipo,
                        "ASIGNACIÓN DE PERSONA",
                        "Se ha asignado a " + usuario.getPersona().getNombreCompleto() +
                                " al equipo " + equipo.getNombre()
                ));
            }

            // Agregar relación solo si no existe (evitar duplicados)
            if (!usuario.getEquipos().contains(equipo)) {
                usuario.getEquipos().add(equipo);
            }
        }

        // Guardar nuevas asociaciones
        usuarioRepo.saveAll(nuevosUsuarios);

        return equipoMapper.toDTO(equipo);
    }

    public PersonaEquipoDTO actualizarEquiposDeUsuario(Long idUsuario, List<Long> nuevosEquiposIds) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // CAPTURAR EQUIPOS ANTERIORES
        List<Equipo> equiposAnteriores = new ArrayList<>(usuario.getEquipos());
        // Limpiar equipos anteriores
        usuario.getEquipos().clear();
        // Obtener nuevos equipos
        List<Equipo> nuevosEquipos = equipoRepository.findAllById(nuevosEquiposIds);
        // 🔥 REGISTRAR DESVINCULACIONES
        for (Equipo equipoAnterior : equiposAnteriores) {
            boolean sigueAsignado = nuevosEquipos.stream()
                    .anyMatch(eq -> eq.getIdEquipo().equals(equipoAnterior.getIdEquipo()));

            if (!sigueAsignado) {
                cambiosEquipoService.registrarCambioPersonaEquipo(
                        usuario.getPersona(),
                        equipoAnterior, // equipoAnterior
                        null, // equipoNuevo = null porque se desvincula
                        "DESVINCULACION"
                );
            }
        }

        // REGISTRAR NUEVAS ASIGNACIONES
        for (Equipo equipoNuevo : nuevosEquipos) {
            boolean yaEstabaAsignado = equiposAnteriores.stream()
                    .anyMatch(eq -> eq.getIdEquipo().equals(equipoNuevo.getIdEquipo()));

            if (!yaEstabaAsignado) {
                cambiosEquipoService.registrarCambioPersonaEquipo(
                        usuario.getPersona(),
                        null, // equipoAnterior = null porque es nueva asignación
                        equipoNuevo, // equipoNuevo
                        "ASIGNACION"
                );
            }
        }
        // Asociar nuevos títulos
        usuario.getEquipos().addAll(nuevosEquipos);
        // Guardar cambios
        Usuario usuarioActualizado = usuarioRepo.save(usuario);
        // Mapear con el mapper personalizado
        return usuariosEquipoMapper.toDTO(usuarioActualizado);
    }

    public void eliminarEquipoDeUsuario(Long idPersona, Long idEquipo) {
        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // REGISTRAR DESVINCULACIÓN
        if (usuario.getEquipos().contains(equipo)) {
            cambiosEquipoService.registrarCambioPersonaEquipo(
                    usuario.getPersona(),
                    equipo, // equipoAnterior
                    null, // equipoNuevo = null
                    "DESVINCULACION"
            );
        }

        usuario.getEquipos().remove(equipo);
        usuarioRepo.save(usuario);
    }

    public Usuario asignarTituloAUsuario(Long idUsuario, Long idTitulo) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró USUARIO con ID: " + idUsuario));

        TitulosFormacionAcademica titulo = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró TITULO con ID: " + idTitulo));

        // Inicializar lista si está null
        if (usuario.getTitulosFormacionAcademica() == null) {
            usuario.setTitulosFormacionAcademica(new ArrayList<>());
        }

        // Agregar el título si no existe ya
        if (!usuario.getTitulosFormacionAcademica().contains(titulo)) {
            usuario.getTitulosFormacionAcademica().add(titulo);
        }

        return usuarioRepo.save(usuario);
    }


    public TitulosFormacionAcademicaDTO agregarUsuarioATitulo(Long idTitulo, Long idPersona) {
        TitulosFormacionAcademica titulosFormacionAcademica = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("Titulo no encontrado"));

        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.getTitulosFormacionAcademica().add(titulosFormacionAcademica);
        usuarioRepo.save(usuario);

        return tituloFormacionAcademicaMapper.toDTO(titulosFormacionAcademica);
    }

    public List<TitulosFormacionAcademicaDTO> obtenerTituloDeUsuario(Long idPersona) {
        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return tituloFormacionAcademicaMapper.toDTOList(usuario.getTitulosFormacionAcademica());
    }

    public List<PersonaTituloDTO> obtenerUsuariosPorTitulo(Long idTitulo) {
        List<Usuario> usuarios = usuarioRepo.findUsuariosByTitulosFormacionAcademica_IdTitulo(idTitulo);
        return usuarios.stream()
                .map(usuariosTituloMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TitulosFormacionAcademicaDTO actualizarUsuariosDeTitulo(Long idTitulo, List<Long> nuevosUsuariosIds) {
        TitulosFormacionAcademica titulosFormacionAcademica = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("titulo no encontrado"));

        // Usuarios actuales
        List<Usuario> todos = usuarioRepo.findAll();
        for (Usuario usuario : todos) {
            usuario.getTitulosFormacionAcademica().remove(titulosFormacionAcademica);
        }

        usuarioRepo.saveAll(todos); // guardar la limpieza primero

        // Nuevos usuarios a asociar
        List<Usuario> nuevosUsuarios = usuarioRepo.findAllById(nuevosUsuariosIds);
        for (Usuario usuario : nuevosUsuarios) {
            usuario.getTitulosFormacionAcademica().add(titulosFormacionAcademica);
        }

        usuarioRepo.saveAll(nuevosUsuarios); // guardar las nuevas asociaciones

        return tituloFormacionAcademicaMapper.toDTO(titulosFormacionAcademica);
    }

    public PersonaTituloDTO actualizarTitulosDeUsuario(Long idUsuario, List<Long> nuevosTitulosIds) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Limpiar títulos anteriores
        usuario.getTitulosFormacionAcademica().clear();

        // Obtener nuevos títulos
        List<TitulosFormacionAcademica> nuevosTitulos = titulosFormacionAcademicaRepository.findAllById(nuevosTitulosIds);

        // Asociar nuevos títulos
        usuario.getTitulosFormacionAcademica().addAll(nuevosTitulos);

        // Guardar cambios
        Usuario usuarioActualizado = usuarioRepo.save(usuario);

        // Mapear con el mapper personalizado
        return usuariosTituloMapper.toDTO(usuarioActualizado);
    }

    public void eliminarTituloDeUsuario(Long idPersona, Long idTitulo) {
        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        TitulosFormacionAcademica titulosFormacionAcademica = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("Titulo no encontrado"));

        usuario.getTitulosFormacionAcademica().remove(titulosFormacionAcademica);
        usuarioRepo.save(usuario);
    }



    public PersonaRolDTO agregarRolAUsuario(Long idPersona, Long idRol) {
        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Roles roles = rolesRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));

        usuario.getRoles().add(roles);
        Usuario usuarioActualizado = usuarioRepo.save(usuario);

        return usuarioRolMapper.toDTO(usuarioActualizado);
    }

    public RolesDTO agregarUsuarioARol(Long id, Long idPersona) {
        Roles roles = rolesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));

        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.getRoles().add(roles);
        usuarioRepo.save(usuario);

        return rolMapper.toDTO(roles);
    }

    public List<RolesDTO> obtenerRolDeUsuario(Long idPersona) {
        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return rolMapper.toDTOList(usuario.getRoles());
    }

    public List<PersonaRolDTO> obtenerUsuariosPorRol(Long idRol) {
        List<Usuario> usuarios = usuarioRepo.findUsuariosByRoles_IdRol(idRol);
        return usuarios.stream()
                .map(usuarioRolMapper::toDTO)
                .collect(Collectors.toList());
    }

    public RolesDTO actualizarUsuariosDeRol(Long idRol, List<Long> nuevosUsuariosIds) {
        Roles roles = rolesRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));
        // Usuarios actuales
        List<Usuario> todos = usuarioRepo.findAll();
        for (Usuario usuario : todos) {
            usuario.getRoles().remove(roles);
        }

        usuarioRepo.saveAll(todos); // guardar la limpieza primero

        // Nuevos usuarios a asociar
        List<Usuario> nuevosUsuarios = usuarioRepo.findAllById(nuevosUsuariosIds);
        for (Usuario usuario : nuevosUsuarios) {
            usuario.getRoles().add(roles);
        }

        usuarioRepo.saveAll(nuevosUsuarios); // guardar las nuevas asociaciones

        return rolMapper.toDTO(roles);
    }

    public PersonaRolDTO actualizarRolesDeUsuario(Long idUsuario, List<Long> nuevosRolesIds) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Limpiar roles anteriores
        usuario.getRoles().clear();
        // Obtener nuevos roles
        List<Roles> nuevosRoles = rolesRepository.findAllById(nuevosRolesIds);
        // Asociar nuevos roles
        usuario.getRoles().addAll(nuevosRoles);
        // Guardar cambios
        Usuario usuarioActualizado = usuarioRepo.save(usuario);
        // Mapear con el mapper personalizado
        return usuarioRolMapper.toDTO(usuarioActualizado);
    }

    public void eliminarRolDeUsuario(Long idPersona, Long id) {
        Usuario usuario = usuarioRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Roles roles = rolesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));

        usuario.getRoles().remove(roles);
        usuarioRepo.save(usuario);
    }

    public List<Usuario> findAllUsuarios() {
        return usuarioRepo.findAll();
    }

    public List<Usuario> findAllRoles() {
        return usuarioRepo.findAll();
    }

    public List<Usuario> findAllEquipos(){
        return usuarioRepo.findAll();
    }
}
