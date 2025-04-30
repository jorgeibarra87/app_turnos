package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.EquipoDTO;
import com.turnos.enfermeria.service.EquipoService;
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
@RequestMapping("/equipo")
//@Tag(name = "Equipo", description = "Gestión de equipos de trabajo en el sistema de turnos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo equipo",
            description = "Registra un nuevo equipo con su nombre, tipo y miembros asociados.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> create(@RequestBody EquipoDTO equipoDTO){
        EquipoDTO nuevoEquipoDTO = equipoService.create(equipoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEquipoDTO);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los equipos",
            description = "Devuelve una lista con todos los equipos registrados en el sistema.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<EquipoDTO>> findAll(){
        List<EquipoDTO> equipoDTO = equipoService.findAll();
        return equipoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(equipoDTO);
    }

    @GetMapping("/{idEquipo}")
    @Operation(
            summary = "Buscar equipo por ID",
            description = "Devuelve los datos de un equipo específico usando su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> findById(@PathVariable Long idEquipo){
        return equipoService.findById(idEquipo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idEquipo}")
    @Operation(
            summary = "Actualizar equipo",
            description = "Modifica los datos de un equipo existente, identificado por su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> ***REMOVED***(@RequestBody EquipoDTO equipoDTO, @PathVariable Long idEquipo){
        return equipoService.findById(idEquipo)
                .map(equipoExistente -> ResponseEntity.ok(equipoService.***REMOVED***(equipoDTO, idEquipo)))
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{idEquipo}")
    @Operation(
            summary = "Eliminar equipo",
            description = "Elimina un equipo existente del sistema según su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idEquipo){
        return equipoService.findById(idEquipo)
                .map(equipoDTO-> {
                    equipoService.delete(idEquipo);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
