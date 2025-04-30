package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.TipoFormacionAcademicaDTO;
import com.turnos.enfermeria.model.entity.TipoFormacionAcademica;
import com.turnos.enfermeria.service.TipoFormacionAcademicaService;
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
@RequestMapping("/tipoFormacionAcademica")
//@Tag(name = "Tipo Formación Académica", description = "Operaciones CRUD sobre los tipos de formación académica del personal")
public class TipoFormacionAcademicaController {

    @Autowired
    private TipoFormacionAcademicaService tipoFormacionAcademicaService;

    @PostMapping
    @Operation(summary = "Crear tipo de formación académica", description = "Crea un nuevo tipo de formación académica",
            tags={"Títulos de Formación Académica"})
    public ResponseEntity<TipoFormacionAcademicaDTO> create(@RequestBody TipoFormacionAcademicaDTO tipoFormacionAcademicaDTO){
        TipoFormacionAcademicaDTO nuevoTipoFormacionAcademicaDTO = tipoFormacionAcademicaService.create(tipoFormacionAcademicaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipoFormacionAcademicaDTO);
    }

    @GetMapping
    @Operation(summary = "Listar tipos de formación académica", description = "Obtiene todos los tipos de formación académica registrados",
            tags={"Títulos de Formación Académica"})
    public ResponseEntity<List<TipoFormacionAcademicaDTO>> findAll(){
        List<TipoFormacionAcademicaDTO> tipoFormacionAcademicaDTO = tipoFormacionAcademicaService.findAll();
        return tipoFormacionAcademicaDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tipoFormacionAcademicaDTO);
    }

    @GetMapping("/{idTipoFormacionAcademica}")
    @Operation(summary = "Buscar tipo de formación académica por ID", description = "Devuelve un tipo de formación si existe el ID",
            tags={"Títulos de Formación Académica"})
    public ResponseEntity<TipoFormacionAcademicaDTO> findById(@PathVariable Long idTipoFormacionAcademica){
        return tipoFormacionAcademicaService.findById(idTipoFormacionAcademica)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idTipoFormacionAcademica}")
    @Operation(summary = "Actualizar tipo de formación académica", description = "Actualiza los datos de un tipo de formación existente",
            tags={"Títulos de Formación Académica"})
    public ResponseEntity<TipoFormacionAcademicaDTO> update(@RequestBody TipoFormacionAcademicaDTO tipoFormacionAcademicaDTO, @PathVariable Long idTipoFormacionAcademica){
        return tipoFormacionAcademicaService.findById(idTipoFormacionAcademica)
                .map(tipoFormacionAcademicaExistente -> ResponseEntity.ok(tipoFormacionAcademicaService.update(tipoFormacionAcademicaDTO, idTipoFormacionAcademica)))
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{idTipoFormacionAcademica}")
    @Operation(summary = "Eliminar tipo de formación académica", description = "Elimina un tipo de formación existente por su ID",
            tags={"Títulos de Formación Académica"})
    public ResponseEntity<Object> delete(@PathVariable Long idTipoFormacionAcademica){
        return tipoFormacionAcademicaService.findById(idTipoFormacionAcademica)
                .map(tipoFormacionAcademicaDTO-> {
                    tipoFormacionAcademicaService.delete(idTipoFormacionAcademica);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
