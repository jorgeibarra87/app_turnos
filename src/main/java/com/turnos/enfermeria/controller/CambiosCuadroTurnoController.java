package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.CambiosCuadroTurnoDTO;
import com.turnos.enfermeria.service.CambiosCuadroTurnoService;
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
@RequestMapping("/CambiosCuadroTurno")
//@Tag(name = "Cambios de Cuadro de Turno", description = "Endpoints para registrar y consultar los cambios realizados sobre los cuadros de turnos")
public class CambiosCuadroTurnoController {

    @Autowired
    private CambiosCuadroTurnoService cambiosCuadroTurnoService;

    @PostMapping
    @Operation(
            summary = "Registrar un cambio en un cuadro de turnos",
            description = "Guarda un nuevo cambio asociado a un cuadro de turnos, indicando el tipo de modificación realizada.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<CambiosCuadroTurnoDTO> create(@RequestBody CambiosCuadroTurnoDTO cambiosCuadroTurnoDTO){
        CambiosCuadroTurnoDTO nuevocambiosCuadroTurnoDTO = cambiosCuadroTurnoService.create(cambiosCuadroTurnoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevocambiosCuadroTurnoDTO);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los cambios de cuadros de turnos",
            description = "Devuelve una lista con todos los cambios registrados en cuadros de turnos.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<CambiosCuadroTurnoDTO>> findAll(){
        List<CambiosCuadroTurnoDTO> cambiosCuadroTurnoDTO = cambiosCuadroTurnoService.findAll();
        return cambiosCuadroTurnoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(cambiosCuadroTurnoDTO);
    }

    @GetMapping("/{idCambioCuadro}")
    @Operation(
            summary = "Obtener cambio de cuadro de turnos por ID",
            description = "Consulta un registro de cambio específico mediante su identificador único.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<CambiosCuadroTurnoDTO> findById(@PathVariable Long idCambioCuadro){
        return cambiosCuadroTurnoService.findById(idCambioCuadro)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idCambioCuadro}")
    @Operation(
            summary = "Actualizar un cambio de cuadro de turnos",
            description = "Permite modificar la información de un cambio previamente registrado, como su descripción o tipo de acción.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<CambiosCuadroTurnoDTO> ***REMOVED***(@RequestBody CambiosCuadroTurnoDTO cambiosCuadroTurnoDTO, @PathVariable Long idCambioCuadro){
        return cambiosCuadroTurnoService.findById(idCambioCuadro)
                .map(cambiosCuadroTurnoExistente -> ResponseEntity.ok(cambiosCuadroTurnoService.***REMOVED***(cambiosCuadroTurnoDTO, idCambioCuadro)))
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{idCambioCuadro}")
    @Operation(
            summary = "Eliminar un cambio de cuadro de turnos",
            description = "Elimina un registro de cambio asociado a un cuadro de turnos, si existe.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idCambioCuadro){
        return cambiosCuadroTurnoService.findById(idCambioCuadro)
                .map(cambiosCuadroTurnoDTO-> {
                    cambiosCuadroTurnoService.delete(idCambioCuadro);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
