package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.CambiosTurnoDTO;
import com.turnos.enfermeria.model.entity.CambiosTurno;
import com.turnos.enfermeria.model.entity.Usuario;
import com.turnos.enfermeria.service.CambiosTurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.validation.annotation.Validated;


@Validated
@RestController
@RequestMapping("/cambiosTurno")
//@Tag(name = "Cambios de Turno", description = "Operaciones relacionadas con el historial de cambios en los turnos")
public class CambiosTurnoController {
    @Autowired
    private CambiosTurnoService cambiosTurnoService;

    @PostMapping
    @Operation(summary = "Crear un cambio de turno", description = "Registra un nuevo cambio asociado a un turno.",
            tags={"Turnos"})
    public ResponseEntity<CambiosTurnoDTO> create(@RequestBody CambiosTurnoDTO cambiosTurnoDTO){
        CambiosTurnoDTO nuevoCambiosTurnoDTO = cambiosTurnoService.create(cambiosTurnoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCambiosTurnoDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todos los cambios de turno", description = "Devuelve una lista con todos los cambios registrados.",
            tags={"Turnos"})
    public ResponseEntity<List<CambiosTurnoDTO>> findAll(){
        List<CambiosTurnoDTO> cambiosTurnoDTO = cambiosTurnoService.findAll();
        return cambiosTurnoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(cambiosTurnoDTO);
    }

    @GetMapping("/{idCambio}")
    @Operation(summary = "Buscar un cambio de turno por ID", description = "Devuelve los detalles de un cambio específico si existe.",
            tags={"Turnos"})
    public ResponseEntity<CambiosTurnoDTO> findById(@PathVariable Long idCambio){
        return cambiosTurnoService.findById(idCambio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idCambio}")
    @Operation(summary = "Actualizar un cambio de turno", description = "Modifica la información de un cambio existente.",
            tags={"Turnos"})
    public ResponseEntity<CambiosTurnoDTO> ***REMOVED***(@RequestBody CambiosTurnoDTO cambiosTurnoDTO, @PathVariable Long idCambio){
        return cambiosTurnoService.findById(idCambio)
                .map(cambiosTurnoExistente -> ResponseEntity.ok(cambiosTurnoService.***REMOVED***(cambiosTurnoDTO, idCambio)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idCambio}")
    @Operation(summary = "Eliminar un cambio de turno", description = "Elimina un cambio del historial mediante su ID.",
            tags={"Turnos"})
    public ResponseEntity<Object> delete(@PathVariable Long idCambio){
        return cambiosTurnoService.findById(idCambio)
                .map(cambiosTurnoDTO-> {
                    cambiosTurnoService.delete(idCambio);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
