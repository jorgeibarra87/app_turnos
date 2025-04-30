package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.PersonaDTO;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.service.PersonaService;
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
@RequestMapping("/persona")
//@Tag(name = "Personas", description = "Operaciones relacionadas con la gestión de personas en el sistema")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @PostMapping
    @Operation(
            summary = "Crear una nueva persona",
            description = "Registra una nueva persona en el sistema con sus datos personales.",
            tags={"Usuarios"}
    )
    public ResponseEntity<PersonaDTO> create(@RequestBody PersonaDTO personaDTO){
        PersonaDTO nuevaPersonaDTO = personaService.create(personaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersonaDTO);
    }

    @GetMapping
    @Operation(
            summary = "Listar todas las personas",
            description = "Devuelve una lista con todas las personas registradas en el sistema.",
            tags={"Usuarios"}
    )
    public ResponseEntity<List<PersonaDTO>> findAll(){
        List<PersonaDTO> personaDTO = personaService.findAll();
        return personaDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(personaDTO);
    }

    @GetMapping("/{idPersona}")
    @Operation(
            summary = "Buscar persona por ID",
            description = "Devuelve los datos de una persona específica utilizando su ID.",
            tags={"Usuarios"}
    )
    public ResponseEntity<PersonaDTO> findById(@PathVariable Long idPersona){
        return personaService.findById(idPersona)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idPersona}")
    @Operation(
            summary = "Actualizar datos de una persona",
            description = "Actualiza los datos personales de una persona existente según su ID.",
            tags={"Usuarios"}
    )
    public ResponseEntity<PersonaDTO> update(@RequestBody PersonaDTO personaDTO, @PathVariable Long idPersona){
        return personaService.findById(idPersona)
                .map(personaExistente -> ResponseEntity.ok(personaService.update(personaDTO, idPersona)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idPersona}")
    @Operation(
            summary = "Eliminar persona",
            description = "Elimina del sistema una persona registrada según su ID.",
            tags={"Usuarios"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idPersona){
        return personaService.findById(idPersona)
                .map(personaDTO-> {
                    personaService.delete(idPersona);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
