package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.mapper.UsuariosEquipoMapper;
import com.turnos.enfermeria.mapper.UsuariosRolMapper;
import com.turnos.enfermeria.mapper.UsuariosTituloMapper;
import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import com.turnos.enfermeria.model.entity.Usuario;
import com.turnos.enfermeria.repository.TitulosFormacionAcademicaRepository;
import com.turnos.enfermeria.repository.UsuarioRepository;
import com.turnos.enfermeria.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/usuario")
@Tag(name = "Usuarios", description = "API para gestionar Usuarios,Persona y Roles, sus equipos y sus títulos académicos.")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TitulosFormacionAcademicaRepository tituloRepository;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UsuariosTituloMapper usuariosTituloMapper;
    @Autowired
    private UsuariosRolMapper usuariosRolMapper;
    @Autowired
    private UsuariosEquipoMapper usuariosEquipoMapper;

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario y lo guarda en la base de datos.",
            tags={"Usuarios"})
    public ResponseEntity<UsuarioDTO> create(@RequestBody UsuarioDTO usuarioDTO){
        try {
            UsuarioDTO nuevoUsuarioDTO = usuarioService.create(usuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuarioDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.USUARIO_NO_ENCONTRADO,
                    usuarioDTO.getIdPersona(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.USUARIO_ESTADO_INVALIDO,
                    "No se pudo crear usuario: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el usuario: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve una lista de todos los usuarios registrados.",
            tags={"Usuarios"})
    public ResponseEntity<List<UsuarioDTO>> findAll(){
        List<UsuarioDTO> usuarioDTO = usuarioService.findAll();
        return usuarioDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping("/{idPersona}")
    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve los datos de un usuario a partir de su ID.",
            tags={"Usuarios"})
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Long idPersona){
        return usuarioService.findById(idPersona)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.USUARIO_NO_ENCONTRADO,
                        idPersona,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idPersona}")
    @Operation(summary = "Actualizar un usuario", description = "Actualiza los datos de un usuario específico por su ID.",
            tags={"Usuarios"})
    public ResponseEntity<UsuarioDTO> update(@RequestBody UsuarioDTO usuarioDTO, @PathVariable Long idPersona){
        return usuarioService.findById(idPersona)
                .map(usuarioExistente -> ResponseEntity.ok(usuarioService.update(usuarioDTO, idPersona)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.USUARIO_NO_ENCONTRADO,
                        idPersona,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    // GET: Obtener equipos de un usuario
    @GetMapping("/{id}/equipo")
    @Operation(summary = "Obtener equipos de un usuario", description = "Lista todos los equipos asociados a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<List<EquipoDTO>> obtenerEquiposDeUsuario(@PathVariable Long id) {
        try {
            List<EquipoDTO> equipos = usuarioService.obtenerEquiposDeUsuario(id);
            return ResponseEntity.ok(equipos);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.EQUIPO_NO_ENCONTRADO,
                    id,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.EQUIPO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.EQUIPO_ESTADO_INVALIDO,
                    "No se pudo acceder equipo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error en equipo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping("/equipo/{idEquipo}/usuarios")
    @Operation(summary = "Obtener usuarios de un equipo", description = "Lista todos los usuarios asociados a un equipo.",
            tags={"Usuarios"})
    public ResponseEntity<List<PersonaEquipoDTO>> obtenerUsuariosPorEquipo(@PathVariable Long idEquipo) {
        List<PersonaEquipoDTO> usuarios = usuarioService.obtenerUsuariosPorEquipo(idEquipo);
        return ResponseEntity.ok(usuarios);
    }
//    public ResponseEntity<List<PersonaEquipoDTO>> obtenerUsuariosPorEquipo(@PathVariable Long idEquipo) {
//        try {
//            List<PersonaEquipoDTO> usuarios = usuarioService.obtenerUsuariosPorEquipo(idEquipo);
//            return ResponseEntity.ok(usuarios);
//        }catch (EntityNotFoundException e) {
//            throw new GenericNotFoundException(
//                    CodigoError.USUARIO_NO_ENCONTRADO,
//                    idEquipo,
//                    request.getMethod(),
//                    request.getRequestURI()
//            );
//        } catch (IllegalArgumentException e) {
//            throw new GenericBadRequestException(
//                    CodigoError.USUARIO_DATOS_INVALIDOS,
//                    e.getMessage(),
//                    request.getMethod(),
//                    request.getRequestURI()
//            );
//
//        } catch (IllegalStateException e) {
//            throw new GenericConflictException(
//                    CodigoError.USUARIO_ESTADO_INVALIDO,
//                    "No se pudo crear usuario: " + e.getMessage(),
//                    request.getMethod(),
//                    request.getRequestURI()
//            );
//
//        } catch (Exception e) {
//            throw new GenericBadRequestException(
//                    CodigoError.ERROR_PROCESAMIENTO,
//                    "Error al crear el usuario: " + e.getMessage(),
//                    request.getMethod(),
//                    request.getRequestURI()
//            );
//        }
//    }

    // POST: Agregar un equipo a un usuario
    @PostMapping("/{id}/equipo/{idEquipo}")
    @Operation(summary = "Asignar equipo a usuario", description = "Agrega un equipo existente a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<PersonaEquipoDTO> agregarEquipoAUsuario(@PathVariable Long id, @PathVariable Long idEquipo) {
        try {
            PersonaEquipoDTO usuarioDTO = usuarioService.agregarEquipoAUsuario(id, idEquipo);
            return ResponseEntity.ok(usuarioDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.EQUIPO_NO_ENCONTRADO,
                    id,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.EQUIPO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.EQUIPO_ESTADO_INVALIDO,
                    "No se pudo acceder equipo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error en equipo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    // POST: Agregar un usuario a un equipo
    @PostMapping("/equipo/{idEquipo}/usuario/{idPersona}")
    @Operation(summary = "Asignar usuario a equipo", description = "Agrega un usuario existente a un equipo.",
            tags={"Usuarios"})
    public ResponseEntity<EquipoDTO> agregarUsuarioAEquipo(@PathVariable Long idEquipo, @PathVariable Long idPersona) {
        try {
            EquipoDTO equipoDTO = usuarioService.agregarUsuarioAEquipo(idEquipo, idPersona);
            return ResponseEntity.ok(equipoDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.USUARIO_NO_ENCONTRADO,
                    idEquipo,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.USUARIO_ESTADO_INVALIDO,
                    "No se pudo crear usuario: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el usuario: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @PutMapping("equipo/{idEquipo}")
    @Operation(summary = "Actualizar usuarios de un equipo", description = "Reemplaza la lista de usuarios asociados a un equipo.",
            tags={"Usuarios"})
    public ResponseEntity<EquipoDTO> actualizarUsuariosDeEquipo(
            @PathVariable Long idEquipo,
            @RequestBody List<Long> nuevosUsuariosIds) {
        try {
            EquipoDTO equipoDTO = usuarioService.actualizarUsuariosDeEquipo(idEquipo, nuevosUsuariosIds);
            return ResponseEntity.ok(equipoDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.USUARIO_NO_ENCONTRADO,
                    idEquipo,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.USUARIO_ESTADO_INVALIDO,
                    "No se pudo crear usuario: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el usuario: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @PutMapping("{idPersona}/equipo")
    @Operation(summary = "Actualizar equipos de un usuario", description = "Reemplaza la lista de equipos de un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<PersonaEquipoDTO> actualizarEquiposDeUsuario(
            @PathVariable Long idPersona,
            @RequestBody List<Long> nuevosEquiposIds) {
        try {
            PersonaEquipoDTO personaDTO = usuarioService.actualizarEquiposDeUsuario(idPersona, nuevosEquiposIds);
            return ResponseEntity.ok(personaDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.EQUIPO_NO_ENCONTRADO,
                    idPersona,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.EQUIPO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.EQUIPO_ESTADO_INVALIDO,
                    "No se pudo acceder equipo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error en equipo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }





    // GET: Obtener tirulos de un usuario
    @GetMapping("/{id}/titulos")
    @Operation(summary = "Obtener títulos de un usuario", description = "Lista todos los títulos académicos asociados a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<List<TitulosFormacionAcademicaDTO>> obtenerTituloDeUsuario(@PathVariable Long id) {
        try {
            List<TitulosFormacionAcademicaDTO> equipos = usuarioService.obtenerTituloDeUsuario(id);
            return ResponseEntity.ok(equipos);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TITULO_USUARIO_NO_ENCONTRADO,
                    id,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TITULO_USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TITULO_USUARIO_ESTADO_INVALIDO,
                    "No se pudo acceder a titulo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error en titulo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping("/titulo/{idTitulo}/usuarios")
    @Operation(summary = "Obtener usuarios por título", description = "Devuelve los usuarios que tienen un título académico específico.",
            tags={"Usuarios"})
    public ResponseEntity<List<PersonaTituloDTO>> obtenerUsuariosPorTitulo(@PathVariable Long idTitulo) {
        try {
            List<PersonaTituloDTO> usuarios = usuarioService.obtenerUsuariosPorTitulo(idTitulo);
            return ResponseEntity.ok(usuarios);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.USUARIO_NO_ENCONTRADO,
                    idTitulo,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.USUARIO_ESTADO_INVALIDO,
                    "No se pudo crear usuario: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el usuario: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    // POST: Agregar un titulo a un usuario
    @PostMapping("/{idUsuario}/titulo/{idTitulo}")
    public ResponseEntity<?> asignarTitulo(
            @PathVariable Long idUsuario,
            @PathVariable Long idTitulo) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + idUsuario));

        TitulosFormacionAcademica titulo = tituloRepository.findById(idTitulo)
                .orElseThrow(() -> new EntityNotFoundException("Título no encontrado con id: " + idTitulo));

        // Inicializamos la lista si está en null
        if (usuario.getTitulosFormacionAcademica() == null) {
            usuario.setTitulosFormacionAcademica(new ArrayList<>());
        }

        // Evitar duplicados
        if (!usuario.getTitulosFormacionAcademica().contains(titulo)) {
            usuario.getTitulosFormacionAcademica().add(titulo);
            usuarioRepository.save(usuario);
        }

        return ResponseEntity.ok("Título asignado correctamente al usuario");
    }
//    @PostMapping("/{id}/titulo/{idTitulo}")
//    @Operation(summary = "Asignar título a usuario", description = "Asocia un título académico existente a un usuario.",
//            tags={"Usuarios"})
//    public ResponseEntity<PersonaTituloDTO> agregarTituloAUsuario(@PathVariable Long id, @PathVariable Long idTitulo) {
//        try{
//        PersonaTituloDTO usuarioDTO = usuarioService.agregarTituloAUsuario(id, idTitulo);
//        return ResponseEntity.ok(usuarioDTO);
//        }catch (EntityNotFoundException e) {
//            throw new GenericNotFoundException(
//                    CodigoError.TITULO_USUARIO_NO_ENCONTRADO,
//                    idTitulo,
//                    request.getMethod(),
//                    request.getRequestURI()
//            );
//        } catch (IllegalArgumentException e) {
//            throw new GenericBadRequestException(
//                    CodigoError.TITULO_USUARIO_DATOS_INVALIDOS,
//                    e.getMessage(),
//                    request.getMethod(),
//                    request.getRequestURI()
//            );
//
//        } catch (IllegalStateException e) {
//            throw new GenericConflictException(
//                    CodigoError.TITULO_USUARIO_ESTADO_INVALIDO,
//                    "No se pudo acceder a titulo: " + e.getMessage(),
//                    request.getMethod(),
//                    request.getRequestURI()
//            );
//
//        } catch (Exception e) {
//            throw new GenericBadRequestException(
//                    CodigoError.ERROR_PROCESAMIENTO,
//                    "Error en titulo: " + e.getMessage(),
//                    request.getMethod(),
//                    request.getRequestURI()
//            );
//        }
//    }

    // POST: Agregar un usuario a un titulo
    @PostMapping("/titulo/{idTitulo}/usuario/{idPersona}")
    @Operation(summary = "Asignar usuario a título", description = "Agrega un usuario a la lista de personas que poseen un título académico.",
            tags={"Usuarios"})
    public ResponseEntity<TitulosFormacionAcademicaDTO> agregarUsuarioATitulo(@PathVariable Long idTitulo, @PathVariable Long idPersona) {
        try {
            TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO = usuarioService.agregarUsuarioATitulo(idTitulo, idPersona);
            return ResponseEntity.ok(titulosFormacionAcademicaDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.USUARIO_NO_ENCONTRADO,
                    idTitulo,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.USUARIO_ESTADO_INVALIDO,
                    "No se pudo crear usuario: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el usuario: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @PutMapping("titulo/{idTitulo}")
    @Operation(summary = "Actualizar usuarios de un título", description = "Reemplaza la lista de usuarios que tienen un título específico.",
            tags={"Usuarios"})
    public ResponseEntity<TitulosFormacionAcademicaDTO> actualizarUsuariosDeTitulo(
            @PathVariable Long idTitulo,
            @RequestBody List<Long> nuevosUsuariosIds) {
        try {
            TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO = usuarioService.actualizarUsuariosDeTitulo(idTitulo, nuevosUsuariosIds);
            return ResponseEntity.ok(titulosFormacionAcademicaDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TITULO_USUARIO_NO_ENCONTRADO,
                    idTitulo,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TITULO_USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TITULO_USUARIO_ESTADO_INVALIDO,
                    "No se pudo acceder a titulo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error en titulo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @PutMapping("{idPersona}/titulo")
    @Operation(summary = "Actualizar títulos de un usuario", description = "Reemplaza la lista de títulos académicos de un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<PersonaTituloDTO> actualizarTitulosDeUsuario(
            @PathVariable Long idPersona,
            @RequestBody List<Long> nuevosTitulosIds) {
        try {
            PersonaTituloDTO personaDTO = usuarioService.actualizarTitulosDeUsuario(idPersona, nuevosTitulosIds);
            return ResponseEntity.ok(personaDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TITULO_USUARIO_NO_ENCONTRADO,
                    idPersona,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TITULO_USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TITULO_USUARIO_ESTADO_INVALIDO,
                    "No se pudo acceder a titulo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error en titulo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    // GET: Obtener roles de un usuario
    @GetMapping("/{id}/roles")
    @Operation(summary = "Obtener roles de un usuario", description = "Lista todos los roles asociados a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<List<RolesDTO>> obtenerRolDeUsuario(@PathVariable Long id) {
        try {
            List<RolesDTO> roles = usuarioService.obtenerRolDeUsuario(id);
            return ResponseEntity.ok(roles);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.ROL_USUARIO_NO_ENCONTRADO,
                    id,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.ROL_USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.ROL_USUARIO_ESTADO_INVALIDO,
                    "No se pudo acceder a rol: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error en rol: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping("/rol/{id}/usuarios")
    @Operation(summary = "Obtener usuarios de un rol", description = "Lista todos los usuarios asociados a un rol.",
            tags={"Usuarios"})
    public ResponseEntity<List<PersonaRolDTO>> obtenerUsuariosPorRol(@PathVariable Long id) {
        try {
            List<PersonaRolDTO> usuarios = usuarioService.obtenerUsuariosPorRol(id);
            return ResponseEntity.ok(usuarios);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.ROL_USUARIO_NO_ENCONTRADO,
                    id,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.ROL_USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.ROL_USUARIO_ESTADO_INVALIDO,
                    "No se pudo acceder a rol: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error en rol: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    // POST: Agregar un rol a un usuario
    @PostMapping("/{id}/rol/{idRol}")
    @Operation(summary = "Asignar rol a usuario", description = "Agrega un rol existente a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<PersonaRolDTO> agregarEquipoARol(@PathVariable Long id, @PathVariable Long idRol) {
        try {
            PersonaRolDTO usuarioDTO = usuarioService.agregarRolAUsuario(id, idRol);
            return ResponseEntity.ok(usuarioDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.ROL_USUARIO_NO_ENCONTRADO,
                    id,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.ROL_USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.ROL_USUARIO_ESTADO_INVALIDO,
                    "No se pudo acceder a rol: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error en rol: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    // POST: Agregar un usuario a un rol
    @PostMapping("/rol{idRol}/usuario/{idPersona}")
    @Operation(summary = "Asignar usuario a rol", description = "Agrega un usuario existente a un rol.",
            tags={"Usuarios"})
    public ResponseEntity<RolesDTO> agregarUsuarioARol(@PathVariable Long idRol, @PathVariable Long idPersona) {
        try {
            RolesDTO rolDTO = usuarioService.agregarUsuarioARol(idRol, idPersona);
            return ResponseEntity.ok(rolDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.ROL_USUARIO_NO_ENCONTRADO,
                    idRol,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.ROL_USUARIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.ROL_USUARIO_ESTADO_INVALIDO,
                    "No se pudo acceder a rol: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error en rol: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @PutMapping("rol/{idRol}")
    @Operation(summary = "Actualizar usuarios de un rol", description = "Reemplaza la lista de usuarios asociados a un rol.",
            tags={"Usuarios"})
    public ResponseEntity<RolesDTO> actualizarUsuariosDeRol(
            @PathVariable Long idRol,
            @RequestBody List<Long> nuevosUsuariosIds) {
    try {
        RolesDTO rolDTO = usuarioService.actualizarUsuariosDeRol(idRol, nuevosUsuariosIds);
        return ResponseEntity.ok(rolDTO);
    }catch (EntityNotFoundException e) {
        throw new GenericNotFoundException(
                CodigoError.ROL_USUARIO_NO_ENCONTRADO,
                idRol,
                request.getMethod(),
                request.getRequestURI()
        );
    } catch (IllegalArgumentException e) {
        throw new GenericBadRequestException(
                CodigoError.ROL_USUARIO_DATOS_INVALIDOS,
                e.getMessage(),
                request.getMethod(),
                request.getRequestURI()
        );

    } catch (IllegalStateException e) {
        throw new GenericConflictException(
                CodigoError.ROL_USUARIO_ESTADO_INVALIDO,
                "No se pudo acceder a rol: " + e.getMessage(),
                request.getMethod(),
                request.getRequestURI()
        );

    } catch (Exception e) {
        throw new GenericBadRequestException(
                CodigoError.ERROR_PROCESAMIENTO,
                "Error en rol: " + e.getMessage(),
                request.getMethod(),
                request.getRequestURI()
        );
    }
    }

    @GetMapping("/titulos")
    public List<PersonaTituloDTO> getUsuariosConTitulos() {
        List<Usuario> usuarios = usuarioService.findAllUsuarios();
        return usuariosTituloMapper.toDTOList(usuarios);
    }

    @GetMapping("/roles")
    public List<UsuariosRolDTO> getUsuariosConRoles() {
        List<Usuario> usuarios = usuarioService.findAllRoles();
        return usuariosRolMapper.toDTOList(usuarios);
    }

    @GetMapping("/equipos")
    public List<PersonaEquipoDTO> getUsuariosConEquipos() {
        List<Usuario> usuarios = usuarioService.findAllEquipos();
        return usuariosEquipoMapper.toDTOList(usuarios);
    }
}
