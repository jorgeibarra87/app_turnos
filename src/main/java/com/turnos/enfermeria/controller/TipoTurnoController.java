package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.TipoTurnoDTO;
import com.turnos.enfermeria.service.TipoTurnoService;
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
@RequestMapping("/tipoturno")
//@Tag(name = "Tipo de Turno", description = "Operaciones CRUD sobre los tipos de turno configurables en el sistema de turnos")
public class TipoTurnoController {

    @Autowired
    private TipoTurnoService tipoTurnoService;

    @PostMapping
    @Operation(summary = "Crear tipo de turno", description = "Crea un nuevo tipo de turno con los datos proporcionados",
            tags={"Contratos"})
    public ResponseEntity<TipoTurnoDTO> create(@RequestBody TipoTurnoDTO tipoTurnoDTO){
        TipoTurnoDTO nuevoTipoTurnoDTO = tipoTurnoService.create(tipoTurnoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipoTurnoDTO);
    }

    @GetMapping
    @Operation(summary = "Listar tipos de turno", description = "Devuelve todos los tipos de turno registrados",
            tags={"Contratos"})
    public ResponseEntity<List<TipoTurnoDTO>> findAll(){
        List<TipoTurnoDTO> tipoTurnoDTO = tipoTurnoService.findAll();
        return tipoTurnoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tipoTurnoDTO);
    }

    @GetMapping("/{idTipoTurno}")
    @Operation(summary = "Buscar tipo de turno por ID", description = "Busca un tipo de turno por su identificador",
            tags={"Contratos"})
    public ResponseEntity<TipoTurnoDTO> findById(@PathVariable Long idTipoTurno){
        return tipoTurnoService.findById(idTipoTurno)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idTipoTurno}")
    @Operation(summary = "Actualizar tipo de turno", description = "Actualiza los datos de un tipo de turno existente",
            tags={"Contratos"})
    public ResponseEntity<TipoTurnoDTO> ***REMOVED***(@RequestBody TipoTurnoDTO tipoTurnoDTO, @PathVariable Long idTipoTurno){
        return tipoTurnoService.findById(idTipoTurno)
                .map(tipoTurnoExistente -> ResponseEntity.ok(tipoTurnoService.***REMOVED***(tipoTurnoDTO, idTipoTurno)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idTipoTurno}")
    @Operation(summary = "Eliminar tipo de turno", description = "Elimina un tipo de turno existente por su ID",
            tags={"Contratos"})
    public ResponseEntity<Object> delete(@PathVariable Long idTipoTurno){
        return tipoTurnoService.findById(idTipoTurno)
                .map(tipoTurnoDTO-> {
                    tipoTurnoService.delete(idTipoTurno);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
