package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.TipoAtencionDTO;
import com.turnos.enfermeria.service.TipoAtencionService;
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
@RequestMapping("/tipoatencion")
//@Tag(name = "Tipo de Atención", description = "Operaciones CRUD sobre los distintos tipos de atención")
public class TipoAtencionController {

    @Autowired
    private TipoAtencionService tipoAtencionService;

    @PostMapping
    @Operation(summary = "Crear tipo de atención", description = "Crea un nuevo tipo de atención médica",
            tags={"Contratos"})
    public ResponseEntity<TipoAtencionDTO> create(@RequestBody TipoAtencionDTO tipoAtencionDTO){
        TipoAtencionDTO nuevoTipoAtencionDTO = tipoAtencionService.create(tipoAtencionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipoAtencionDTO);
    }

    @GetMapping
    @Operation(summary = "Listar tipos de atención", description = "Obtiene una lista de todos los tipos de atención registrados",
            tags={"Contratos"})
    public ResponseEntity<List<TipoAtencionDTO>> findAll(){
        List<TipoAtencionDTO> tipoAtencionDTO = tipoAtencionService.findAll();
        return tipoAtencionDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tipoAtencionDTO);
    }

    @GetMapping("/{idTipoAtencion}")
    @Operation(summary = "Obtener tipo de atención por ID", description = "Busca un tipo de atención específico por su ID",
            tags={"Contratos"})
    public ResponseEntity<TipoAtencionDTO> findById(@PathVariable Long idTipoAtencion){
        return tipoAtencionService.findById(idTipoAtencion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idTipoAtencion}")
    @Operation(summary = "Actualizar tipo de atención", description = "Actualiza la información de un tipo de atención existente",
            tags={"Contratos"})
    public ResponseEntity<TipoAtencionDTO> update(@RequestBody TipoAtencionDTO tipoAtencionDTO, @PathVariable("idTipoAtencion") Long idTipoAtencion){
        return tipoAtencionService.findById(idTipoAtencion)
                .map(tipoAtencionoExistente -> ResponseEntity.ok(tipoAtencionService.update(tipoAtencionDTO, idTipoAtencion)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idTipoAtencion}")
    @Operation(summary = "Eliminar tipo de atención", description = "Elimina un tipo de atención por su ID",
            tags={"Contratos"})
    public ResponseEntity<Object> delete(@PathVariable Long idTipoAtencion){
        return tipoAtencionService.findById(idTipoAtencion)
                .map(tipoAtencionDTO-> {
                    tipoAtencionService.delete(idTipoAtencion);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
