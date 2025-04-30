package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.model.entity.Usuario;
import com.turnos.enfermeria.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuarios", description = "API para gestionar Usuarios,Persona y Roles, sus equipos y sus títulos académicos.")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario y lo guarda en la base de datos.",
            tags={"Usuarios"})
    public ResponseEntity<UsuarioDTO> create(@RequestBody UsuarioDTO usuarioDTO){
        UsuarioDTO nuevoUsuarioDTO = usuarioService.create(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuarioDTO);
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
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idPersona}")
    @Operation(summary = "Actualizar un usuario", description = "Actualiza los datos de un usuario específico por su ID.",
            tags={"Usuarios"})
    public ResponseEntity<UsuarioDTO> update(@RequestBody UsuarioDTO usuarioDTO, @PathVariable Long idPersona){
        return usuarioService.findById(idPersona)
                .map(usuarioExistente -> ResponseEntity.ok(usuarioService.update(usuarioDTO, idPersona)))
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario del sistema por su ID.",
            tags={"Usuarios"})
    public ResponseEntity<Object> delete(@PathVariable Long idPersona){
        return usuarioService.findById(idPersona)
                .map(usuarioDTO-> {
                    usuarioService.delete(idPersona);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // GET: Obtener equipos de un usuario
    @GetMapping("/{id}/equipo")
    @Operation(summary = "Obtener equipos de un usuario", description = "Lista todos los equipos asociados a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<List<EquipoDTO>> obtenerEquiposDeUsuario(@PathVariable Long id) {
        List<EquipoDTO> equipos = usuarioService.obtenerEquiposDeUsuario(id);
        return ResponseEntity.ok(equipos);
    }

    @GetMapping("/equipo/{idEquipo}/usuarios")
    @Operation(summary = "Obtener usuarios de un equipo", description = "Lista todos los usuarios asociados a un equipo.",
            tags={"Usuarios"})
    public ResponseEntity<List<PersonaEquipoDTO>> obtenerUsuariosPorEquipo(@PathVariable Long idEquipo) {
        List<PersonaEquipoDTO> usuarios = usuarioService.obtenerUsuariosPorEquipo(idEquipo);
        return ResponseEntity.ok(usuarios);
    }

    // POST: Agregar un equipo a un usuario
    @PostMapping("/{id}/equipo/{idEquipo}")
    @Operation(summary = "Asignar equipo a usuario", description = "Agrega un equipo existente a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<PersonaEquipoDTO> agregarEquipoAUsuario(@PathVariable Long id, @PathVariable Long idEquipo) {
        PersonaEquipoDTO usuarioDTO = usuarioService.agregarEquipoAUsuario(id, idEquipo);
        return ResponseEntity.ok(usuarioDTO);
    }

    // POST: Agregar un usuario a un equipo
    @PostMapping("/equipo/{idEquipo}/usuario/{idPersona}")
    @Operation(summary = "Asignar usuario a equipo", description = "Agrega un usuario existente a un equipo.",
            tags={"Usuarios"})
    public ResponseEntity<EquipoDTO> agregarUsuarioAEquipo(@PathVariable Long idEquipo, @PathVariable Long idPersona) {
        EquipoDTO equipoDTO = usuarioService.agregarUsuarioAEquipo(idEquipo, idPersona);
        return ResponseEntity.ok(equipoDTO);
    }

    @PutMapping("equipo/{idEquipo}")
    @Operation(summary = "Actualizar usuarios de un equipo", description = "Reemplaza la lista de usuarios asociados a un equipo.",
            tags={"Usuarios"})
    public ResponseEntity<EquipoDTO> actualizarUsuariosDeEquipo(
            @PathVariable Long idEquipo,
            @RequestBody List<Long> nuevosUsuariosIds) {

        EquipoDTO equipoDTO = usuarioService.actualizarUsuariosDeEquipo(idEquipo, nuevosUsuariosIds);
        return ResponseEntity.ok(equipoDTO);
    }

    @PutMapping("{idPersona}/equipo")
    @Operation(summary = "Actualizar equipos de un usuario", description = "Reemplaza la lista de equipos de un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<PersonaEquipoDTO> actualizarEquiposDeUsuario(
            @PathVariable Long idPersona,
            @RequestBody List<Long> nuevosEquiposIds) {

        PersonaEquipoDTO personaDTO = usuarioService.actualizarEquiposDeUsuario(idPersona, nuevosEquiposIds);
        return ResponseEntity.ok(personaDTO);
    }

    // DELETE: Eliminar un equipo de un usuario
    @DeleteMapping("/{id}/equipo/{idEquipo}")
    @Operation(summary = "Eliminar equipo de usuario", description = "Elimina la relación entre un usuario y un equipo.",
            tags={"Usuarios"})
    public ResponseEntity<String> eliminarEquipoDeUsuario(@PathVariable Long id, @PathVariable Long idEquipo) {
        usuarioService.eliminarEquipoDeUsuario(id, idEquipo);
        return ResponseEntity.ok("Equipo eliminado del usuario correctamente.");
    }



    // GET: Obtener tirulos de un usuario
    @GetMapping("/{id}/titulos")
    @Operation(summary = "Obtener títulos de un usuario", description = "Lista todos los títulos académicos asociados a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<List<TitulosFormacionAcademicaDTO>> obtenerTituloDeUsuario(@PathVariable Long id) {
        List<TitulosFormacionAcademicaDTO> equipos = usuarioService.obtenerTituloDeUsuario(id);
        return ResponseEntity.ok(equipos);
    }

    @GetMapping("/titulo/{idTitulo}/usuarios")
    @Operation(summary = "Obtener usuarios por título", description = "Devuelve los usuarios que tienen un título académico específico.",
            tags={"Usuarios"})
    public ResponseEntity<List<PersonaTituloDTO>> obtenerUsuariosPorTitulo(@PathVariable Long idTitulo) {
        List<PersonaTituloDTO> usuarios = usuarioService.obtenerUsuariosPorTitulo(idTitulo);
        return ResponseEntity.ok(usuarios);
    }

    // POST: Agregar un titulo a un usuario
    @PostMapping("/{id}/titulo/{idTitulo}")
    @Operation(summary = "Asignar título a usuario", description = "Asocia un título académico existente a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<PersonaTituloDTO> agregarTituloAUsuario(@PathVariable Long id, @PathVariable Long idTitulo) {
        PersonaTituloDTO usuarioDTO = usuarioService.agregarTituloAUsuario(id, idTitulo);
        return ResponseEntity.ok(usuarioDTO);
    }

    // POST: Agregar un usuario a un titulo
    @PostMapping("/titulo/{idTitulo}/usuario/{idPersona}")
    @Operation(summary = "Asignar usuario a título", description = "Agrega un usuario a la lista de personas que poseen un título académico.",
            tags={"Usuarios"})
    public ResponseEntity<TitulosFormacionAcademicaDTO> agregarUsuarioATitulo(@PathVariable Long idTitulo, @PathVariable Long idPersona) {
        TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO = usuarioService.agregarUsuarioATitulo(idTitulo, idPersona);
        return ResponseEntity.ok(titulosFormacionAcademicaDTO);
    }

    @PutMapping("titulo/{idTitulo}")
    @Operation(summary = "Actualizar usuarios de un título", description = "Reemplaza la lista de usuarios que tienen un título específico.",
            tags={"Usuarios"})
    public ResponseEntity<TitulosFormacionAcademicaDTO> actualizarUsuariosDeTitulo(
            @PathVariable Long idTitulo,
            @RequestBody List<Long> nuevosUsuariosIds) {

        TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO = usuarioService.actualizarUsuariosDeTitulo(idTitulo, nuevosUsuariosIds);
        return ResponseEntity.ok(titulosFormacionAcademicaDTO);
    }

    @PutMapping("{idPersona}/titulo")
    @Operation(summary = "Actualizar títulos de un usuario", description = "Reemplaza la lista de títulos académicos de un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<PersonaTituloDTO> actualizarTitulosDeUsuario(
            @PathVariable Long idPersona,
            @RequestBody List<Long> nuevosTitulosIds) {

        PersonaTituloDTO personaDTO = usuarioService.actualizarTitulosDeUsuario(idPersona, nuevosTitulosIds);
        return ResponseEntity.ok(personaDTO);
    }

    // DELETE: Eliminar un titulo de un usuario
    @DeleteMapping("/{id}/titulo/{idTitulo}")
    @Operation(summary = "Eliminar título de usuario", description = "Elimina la relación entre un usuario y un título académico.",
            tags={"Usuarios"})
    public ResponseEntity<String> eliminarTituloDeUsuario(@PathVariable Long id, @PathVariable Long idTitulo) {
        usuarioService.eliminarTituloDeUsuario(id, idTitulo);
        return ResponseEntity.ok("titulo eliminado del usuario correctamente.");
    }


    // GET: Obtener roles de un usuario
    @GetMapping("/{id}/roles")
    @Operation(summary = "Obtener roles de un usuario", description = "Lista todos los roles asociados a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<List<RolesDTO>> obtenerRolDeUsuario(@PathVariable Long id) {
        List<RolesDTO> roles = usuarioService.obtenerRolDeUsuario(id);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/rol/{id}/usuarios")
    @Operation(summary = "Obtener usuarios de un rol", description = "Lista todos los usuarios asociados a un rol.",
            tags={"Usuarios"})
    public ResponseEntity<List<PersonaRolDTO>> obtenerUsuariosPorRol(@PathVariable Long id) {
        List<PersonaRolDTO> usuarios = usuarioService.obtenerUsuariosPorRol(id);
        return ResponseEntity.ok(usuarios);
    }

    // POST: Agregar un rol a un usuario
    @PostMapping("/{id}/rol/{idRol}")
    @Operation(summary = "Asignar rol a usuario", description = "Agrega un rol existente a un usuario.",
            tags={"Usuarios"})
    public ResponseEntity<PersonaRolDTO> agregarEquipoARol(@PathVariable Long id, @PathVariable Long idRol) {
        PersonaRolDTO usuarioDTO = usuarioService.agregarRolAUsuario(id, idRol);
        return ResponseEntity.ok(usuarioDTO);
    }

    // POST: Agregar un usuario a un rol
    @PostMapping("/rol{idRol}/usuario/{idPersona}")
    @Operation(summary = "Asignar usuario a rol", description = "Agrega un usuario existente a un rol.",
            tags={"Usuarios"})
    public ResponseEntity<RolesDTO> agregarUsuarioARol(@PathVariable Long idRol, @PathVariable Long idPersona) {
        RolesDTO rolDTO = usuarioService.agregarUsuarioARol(idRol, idPersona);
        return ResponseEntity.ok(rolDTO);
    }

    @PutMapping("rol/{idRol}")
    @Operation(summary = "Actualizar usuarios de un rol", description = "Reemplaza la lista de usuarios asociados a un rol.",
            tags={"Usuarios"})
    public ResponseEntity<RolesDTO> actualizarUsuariosDeRol(
            @PathVariable Long idRol,
            @RequestBody List<Long> nuevosUsuariosIds) {

        RolesDTO rolDTO = usuarioService.actualizarUsuariosDeRol(idRol, nuevosUsuariosIds);
        return ResponseEntity.ok(rolDTO);
    }

    // DELETE: Eliminar un rol de un usuario
    @DeleteMapping("/{id}/rol/{idRol}")
    @Operation(summary = "Eliminar rol de usuario", description = "Elimina la relación entre un usuario y un rol.",
            tags={"Usuarios"})
    public ResponseEntity<String> eliminarRolDeUsuario(@PathVariable Long id, @PathVariable Long idRol) {
        usuarioService.eliminarRolDeUsuario(id, idRol);
        return ResponseEntity.ok("Rol eliminado del usuario correctamente.");
    }

}
