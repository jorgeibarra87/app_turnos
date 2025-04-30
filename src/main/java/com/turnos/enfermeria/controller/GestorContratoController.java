package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.GestorContratoDTO;
import com.turnos.enfermeria.service.GestorContratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/gestorContrato")
//@Tag(name = "Gestor de Contrato", description = "Operaciones relacionadas con los gestores responsables de los contratos")
public class GestorContratoController {
    @Autowired
    private GestorContratoService gestorContratoService;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo gestor de contrato",
            description = "Registra un nuevo gestor de contrato con sus datos personales y vinculaciones.",
            tags={"Contratos"}
    )
    public ResponseEntity<GestorContratoDTO> create(@RequestBody GestorContratoDTO gestorContratoDTO){
        GestorContratoDTO nuevoGestorContratoDTO = gestorContratoService.create(gestorContratoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoGestorContratoDTO);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los gestores de contrato",
            description = "Devuelve una lista con todos los gestores de contrato registrados en el sistema.",
            tags={"Contratos"}
    )
    public ResponseEntity<List<GestorContratoDTO>> findAll(){
        List<GestorContratoDTO> gestorContratoDTO = gestorContratoService.findAll();
        return gestorContratoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(gestorContratoDTO);
    }

    @GetMapping("/{idGestorContrato}")
    @Operation(
            summary = "Buscar gestor de contrato por ID",
            description = "Devuelve la información de un gestor de contrato a partir de su ID.",
            tags={"Contratos"}
    )
    public ResponseEntity<GestorContratoDTO> findById(@PathVariable Long idGestorContrato){
        return gestorContratoService.findById(idGestorContrato)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idGestorContrato}")
    @Operation(summary = "Actualizar gestor de contrato",description = "Actualiza los datos de un gestor de contrato existente.",
            tags={"Contratos"})
    public ResponseEntity<GestorContratoDTO> update(@RequestBody GestorContratoDTO gestorContratoDTO, @PathVariable Long idGestorContrato){
        return gestorContratoService.findById(idGestorContrato)
                .map(gestorContratoExistente -> ResponseEntity.ok(gestorContratoService.update(gestorContratoDTO, idGestorContrato)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idGestorContrato}")
    @Operation(
            summary = "Eliminar gestor de contrato",
            description = "Elimina de forma lógica o definitiva un gestor de contrato del sistema.",
            tags={"Contratos"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idGestorContrato){
        return gestorContratoService.findById(idGestorContrato)
                .map(gestorContratoDTO-> {
                    gestorContratoService.delete(idGestorContrato);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
