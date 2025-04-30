package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.MacroprocesosDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.Macroprocesos;
import com.turnos.enfermeria.service.BloqueServicioService;
import com.turnos.enfermeria.service.MacroprocesosService;
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
@RequestMapping("/macroprocesos")
//@Tag(name = "Macroprocesos", description = "CRUD de macroprocesos de gestión o ***REMOVED***istrativos")
public class MacroprocesosController {

    @Autowired
    private MacroprocesosService macroprocesosService;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo macroproceso",
            description = "Registra un nuevo macroproceso con su información básica.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<MacroprocesosDTO> create(@RequestBody MacroprocesosDTO macroprocesosDTO){
        MacroprocesosDTO nuevoMacroprocesosDTO = macroprocesosService.create(macroprocesosDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMacroprocesosDTO);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los macroprocesos",
            description = "Devuelve todos los macroprocesos existentes registrados en el sistema.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<MacroprocesosDTO>> findAll(){
        List<MacroprocesosDTO> macroprocesosDTO = macroprocesosService.findAll();
        return macroprocesosDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(macroprocesosDTO);
    }

    @GetMapping("/{idMacroproceso}")
    @Operation(
            summary = "Buscar macroproceso por ID",
            description = "Devuelve un macroproceso específico a partir de su identificador único.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<MacroprocesosDTO> findById(@PathVariable Long idMacroproceso){
        return macroprocesosService.findById(idMacroproceso)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idMacroproceso}")
    @Operation(
            summary = "Actualizar un macroproceso existente",
            description = "Actualiza los datos del macroproceso identificado por su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<MacroprocesosDTO> ***REMOVED***(@RequestBody MacroprocesosDTO macroprocesosDTO, @PathVariable Long idMacroproceso){
        return macroprocesosService.findById(idMacroproceso)
                .map(macroprocesoExistente -> ResponseEntity.ok(macroprocesosService.***REMOVED***(macroprocesosDTO, idMacroproceso)))
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{idMacroproceso}")
    @Operation(
            summary = "Eliminar macroproceso",
            description = "Elimina de forma lógica o definitiva un macroproceso del sistema.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idMacroproceso){
        return macroprocesosService.findById(idMacroproceso)
                .map(macroprocesosDTO-> {
                    macroprocesosService.delete(idMacroproceso);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
