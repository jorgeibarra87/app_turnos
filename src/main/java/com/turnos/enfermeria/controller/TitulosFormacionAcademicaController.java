package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.TitulosFormacionAcademicaDTO;
import com.turnos.enfermeria.service.TitulosFormacionAcademicaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/titulosFormacionAcademica")
@Tag(name = "Títulos de Formación Académica", description = "Operaciones CRUD para la gestión de títulos académicos del personal")
public class TitulosFormacionAcademicaController {

    @Autowired
    private TitulosFormacionAcademicaService titulosFormacionAcademicaService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear título académico",
            description = "Registra un nuevo título de formación académica en el sistema",
            tags={"Títulos de Formación Académica"}
    )
    public ResponseEntity<TitulosFormacionAcademicaDTO> create(@RequestBody TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO){
        try {
            TitulosFormacionAcademicaDTO nuevoTitulosFormacionAcademicaDTO = titulosFormacionAcademicaService.create(titulosFormacionAcademicaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTitulosFormacionAcademicaDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TITULOS_FORMACION_NO_ENCONTRADO,
                    titulosFormacionAcademicaDTO.getIdTitulo(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TITULOS_FORMACION_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TITULOS_FORMACION_ESTADO_INVALIDO,
                    "No se pudo crear turno: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el turno: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping
    @Operation(
            summary = "Listar títulos académicos",
            description = "Obtiene una lista con todos los títulos de formación académica registrados",
            tags={"Títulos de Formación Académica"}
    )
    public ResponseEntity<List<TitulosFormacionAcademicaDTO>> findAll(){
        List<TitulosFormacionAcademicaDTO> titulosFormacionAcademicaDTO = titulosFormacionAcademicaService.findAll();
        return titulosFormacionAcademicaDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(titulosFormacionAcademicaDTO);
    }

    @GetMapping("/{idTitulo}")
    @Operation(
            summary = "Buscar título académico por ID",
            description = "Devuelve el título de formación académica correspondiente al ID especificado",
            tags={"Títulos de Formación Académica"}
    )
    public ResponseEntity<TitulosFormacionAcademicaDTO> findById(@PathVariable Long idTitulo){
        return titulosFormacionAcademicaService.findById(idTitulo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TITULOS_FORMACION_NO_ENCONTRADO,
                        idTitulo,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idTitulo}")
    @Operation(summary = "Actualizar título académico",description = "Actualiza la información de un título existente mediante su ID",
            tags={"Títulos de Formación Académica"})
    public ResponseEntity<TitulosFormacionAcademicaDTO> update(@RequestBody TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO, @PathVariable Long idTitulo){
        return titulosFormacionAcademicaService.findById(idTitulo)
                .map(TitulosFormacionAcademicaExistente -> ResponseEntity.ok(titulosFormacionAcademicaService.update(titulosFormacionAcademicaDTO, idTitulo)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TITULOS_FORMACION_NO_ENCONTRADO,
                        idTitulo,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }
    @DeleteMapping("/{idTitulo}")
    @Operation(
            summary = "Eliminar título académico",
            description = "Elimina un título de formación académica por su ID",
            tags={"Títulos de Formación Académica"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idTitulo){
        return titulosFormacionAcademicaService.findById(idTitulo)
                .map(titulosFormacionAcademicaDTO-> {
                    titulosFormacionAcademicaService.delete(idTitulo);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TITULOS_FORMACION_NO_ENCONTRADO,
                        idTitulo,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }
}
