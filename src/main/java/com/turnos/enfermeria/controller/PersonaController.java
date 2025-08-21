package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.PersonaDTO;
import com.turnos.enfermeria.service.PersonaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Validated
@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/persona")
//@Tag(name = "Personas", description = "Operaciones relacionadas con la gestión de personas en el sistema")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear una nueva persona",
            description = "Registra una nueva persona en el sistema con sus datos personales.",
            tags={"Usuarios"}
    )
    public ResponseEntity<PersonaDTO> create(@RequestBody PersonaDTO personaDTO){
        try {
            PersonaDTO nuevaPersonaDTO = personaService.create(personaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersonaDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.PERSONA_NO_ENCONTRADA,
                    personaDTO.getIdPersona(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.PERSONA_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.PERSONA_ESTADO_INVALIDO,
                    "No se pudo crear persona: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear la persona: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
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
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PERSONA_NO_ENCONTRADA,
                        idPersona,
                        request.getMethod(),
                        request.getRequestURI()
                ));
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
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PERSONA_NO_ENCONTRADA,
                        idPersona,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

//    @DeleteMapping("/{idPersona}")
//    @Operation(
//            summary = "Eliminar persona",
//            description = "Elimina del sistema una persona registrada según su ID.",
//            tags={"Usuarios"}
//    )
//    public ResponseEntity<Object> delete(@PathVariable Long idPersona){
//        return personaService.findById(idPersona)
//                .map(personaDTO-> {
//                    personaService.delete(idPersona);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.PERSONA_NO_ENCONTRADA,
//                        idPersona,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }
}
