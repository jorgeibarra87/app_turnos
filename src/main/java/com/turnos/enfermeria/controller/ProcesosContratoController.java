package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.ProcesosContratoDTO;
import com.turnos.enfermeria.service.ProcesosContratoService;
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
@RequestMapping("/procesosContrato")
//@Tag(name = "Procesos de Contrato", description = "Gestión de los procesos relacionados con contratos")
public class ProcesosContratoController {

    @Autowired
    private ProcesosContratoService procesosContratoService;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo proceso de contrato",
            description = "Registra un nuevo proceso de contrato en el sistema.",
            tags={"Contratos"}
    )
    public ResponseEntity<ProcesosContratoDTO> create(@RequestBody ProcesosContratoDTO procesosContratoDTO){
        ProcesosContratoDTO nuevoProcesosContratoDTO = procesosContratoService.create(procesosContratoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProcesosContratoDTO);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los procesos de contrato",
            description = "Devuelve una lista de todos los procesos de contrato registrados.",
            tags={"Contratos"}
    )
    public ResponseEntity<List<ProcesosContratoDTO>> findAll(){
        List<ProcesosContratoDTO> procesosContratoDTO = procesosContratoService.findAll();
        return procesosContratoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(procesosContratoDTO);
    }

    @GetMapping("/{idProcesoContrato}")
    @Operation(
            summary = "Obtener un proceso de contrato por ID",
            description = "Recupera la información de un proceso de contrato específico usando su ID.",
            tags={"Contratos"}
    )
    public ResponseEntity<ProcesosContratoDTO> findById(@PathVariable Long idProcesoContrato){
        return procesosContratoService.findById(idProcesoContrato)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idProcesoContrato}")
    @Operation(
            summary = "Actualizar un proceso de contrato",
            description = "Modifica la información de un proceso de contrato existente, identificado por su ID.",
            tags={"Contratos"}
    )
    public ResponseEntity<ProcesosContratoDTO> update(@RequestBody ProcesosContratoDTO procesosContratoDTO, @PathVariable Long idProcesoContrato){
        return procesosContratoService.findById(idProcesoContrato)
                .map(procesosContratoExistente -> ResponseEntity.ok(procesosContratoService.update(procesosContratoDTO, idProcesoContrato)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idProcesoContrato}")
    @Operation(
            summary = "Eliminar un proceso de contrato",
            description = "Elimina del sistema un proceso de contrato específico utilizando su ID.",
            tags={"Contratos"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idProcesoContrato){
        return procesosContratoService.findById(idProcesoContrato)
                .map(procesosContratoDTO-> {
                    procesosContratoService.delete(idProcesoContrato);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}