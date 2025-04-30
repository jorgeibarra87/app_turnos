package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.RolesDTO;
import com.turnos.enfermeria.model.entity.Roles;
import com.turnos.enfermeria.service.RolesService;
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
@RequestMapping("/roles")
//@Tag(name = "Roles", description = "Operaciones para la gestión de roles del sistema")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @PostMapping
    @Operation(summary = "Crear rol", description = "Crea un nuevo rol en el sistema",
            tags={"Usuarios"})
    public ResponseEntity<RolesDTO> create(@RequestBody RolesDTO rolesDTO){
        RolesDTO nuevoRolesDTO = rolesService.create(rolesDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRolesDTO);
    }

    @GetMapping
    @Operation(summary = "Listar roles", description = "Obtiene todos los roles registrados en el sistema",
            tags={"Usuarios"})
    public ResponseEntity<List<RolesDTO>> findAll(){
        List<RolesDTO> rolesDTO = rolesService.findAll();
        return rolesDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(rolesDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID", description = "Devuelve el rol correspondiente al ID especificado",
            tags={"Usuarios"})
    public ResponseEntity<RolesDTO> findById(@PathVariable Long id){
        return rolesService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    @Operation(summary = "Actualizar rol", description = "Actualiza la información de un rol existente por ID",
            tags={"Usuarios"})
    public ResponseEntity<RolesDTO> ***REMOVED***(@RequestBody RolesDTO rolesDTO, @PathVariable Long id){
        return rolesService.findById(id)
                .map(roloExistente -> ResponseEntity.ok(rolesService.***REMOVED***(rolesDTO, id)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rol", description = "Elimina un rol por su ID",
            tags={"Usuarios"})
    public ResponseEntity<Object> delete(@PathVariable Long id){
        return rolesService.findById(id)
                .map(rolesDTO-> {
                    rolesService.delete(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}