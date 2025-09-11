package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.TipoFormacionAcademicaDTO;
import com.turnos.enfermeria.service.TipoFormacionAcademicaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/tipoFormacionAcademica")
public class TipoFormacionAcademicaController {

    @Autowired
    private TipoFormacionAcademicaService tipoFormacionAcademicaService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(summary = "Crear tipo de formación académica", description = "Crea un nuevo tipo de formación académica",
            tags={"Títulos de Formación Académica"})
    public ResponseEntity<TipoFormacionAcademicaDTO> create(@RequestBody TipoFormacionAcademicaDTO tipoFormacionAcademicaDTO){
        try {
            TipoFormacionAcademicaDTO nuevoTipoFormacionAcademicaDTO = tipoFormacionAcademicaService.create(tipoFormacionAcademicaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipoFormacionAcademicaDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TIPO_FORMACION_NO_ENCONTRADA,
                    tipoFormacionAcademicaDTO.getIdTipoFormacionAcademica(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TIPO_FORMACION_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TIPO_FORMACION_ESTADO_INVALIDO,
                    "No se pudo crear tipo formacion: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
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
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TIPO_FORMACION_NO_ENCONTRADA,
                        idTipoFormacionAcademica,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idTipoFormacionAcademica}")
    @Operation(summary = "Actualizar tipo de formación académica", description = "Actualiza los datos de un tipo de formación existente",
            tags={"Títulos de Formación Académica"})
    public ResponseEntity<TipoFormacionAcademicaDTO> update(@RequestBody TipoFormacionAcademicaDTO tipoFormacionAcademicaDTO, @PathVariable Long idTipoFormacionAcademica){
        return tipoFormacionAcademicaService.findById(idTipoFormacionAcademica)
                .map(tipoFormacionAcademicaExistente -> ResponseEntity.ok(tipoFormacionAcademicaService.update(tipoFormacionAcademicaDTO, idTipoFormacionAcademica)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TIPO_FORMACION_NO_ENCONTRADA,
                        idTipoFormacionAcademica,
                        request.getMethod(),
                        request.getRequestURI()
                ));
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
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TIPO_FORMACION_NO_ENCONTRADA,
                        idTipoFormacionAcademica,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }
}
