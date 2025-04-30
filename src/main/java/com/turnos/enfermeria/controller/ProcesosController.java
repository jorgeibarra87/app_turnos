package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.ProcesosDTO;
import com.turnos.enfermeria.model.entity.Procesos;
import com.turnos.enfermeria.service.ProcesosService;
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
@RequestMapping("/procesos")
//@Tag(name = "Procesos", description = "Operaciones CRUD para la gestión de procesos")
public class ProcesosController {
    @Autowired
    private ProcesosService procesosService;

    @PostMapping
    @Operation(
            summary = "Crear un proceso",
            description = "Registra un nuevo proceso en el sistema a partir de los datos proporcionados.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<ProcesosDTO> create(@RequestBody ProcesosDTO procesosDTO){
        ProcesosDTO nuevoProcesosDTO = procesosService.create(procesosDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProcesosDTO);
    }

    @GetMapping
    @Operation(
            summary = "Listar procesos",
            description = "Devuelve una lista de todos los procesos registrados en el sistema.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<ProcesosDTO>> findAll(){
        List<ProcesosDTO> procesosDTO = procesosService.findAll();
        return procesosDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(procesosDTO);
    }

    @GetMapping("/{idProceso}")
    @Operation(
            summary = "Obtener proceso por ID",
            description = "Recupera la información de un proceso específico usando su identificador único.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<ProcesosDTO> findById(@PathVariable("idProceso") Long idProceso){
        return procesosService.findById(idProceso)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idProceso}")
    @Operation(
            summary = "Actualizar proceso",
            description = "Modifica los datos de un proceso existente identificado por su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<ProcesosDTO> ***REMOVED***(@RequestBody ProcesosDTO procesosDTO, @PathVariable Long idProceso){
        return procesosService.findById(idProceso)
                .map(procesoExistente -> ResponseEntity.ok(procesosService.***REMOVED***(procesosDTO, idProceso)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idProceso}")
    @Operation(
            summary = "Eliminar proceso",
            description = "Elimina un proceso del sistema utilizando su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idProceso){
        return procesosService.findById(idProceso)
                .map(bloqueServicioDTO-> {
                    procesosService.delete(idProceso);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
