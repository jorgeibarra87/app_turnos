package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.ProcesosAtencionDTO;
import com.turnos.enfermeria.model.entity.ProcesosAtencion;
import com.turnos.enfermeria.service.ProcesosAtencionService;
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
import java.util.Optional;


@Validated
@RestController
@RequestMapping("/procesosAtencion")
//@Tag(name = "Procesos de Atención", description = "Operaciones relacionadas con los procesos de atención")
public class ProcesosAtencionController {
    @Autowired
    private ProcesosAtencionService procesosAtencionService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo proceso de atención",
            description = "Crea y registra un nuevo proceso de atención en el sistema.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<ProcesosAtencionDTO> create(@RequestBody ProcesosAtencionDTO procesosAtencionDTO){
        try {
            ProcesosAtencionDTO nuevoProcesosAtencionDTO = procesosAtencionService.create(procesosAtencionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProcesosAtencionDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.PROCESO_ATENCION_NO_ENCONTRADO,
                    procesosAtencionDTO.getIdProcesoAtencion(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.PROCESO_ATENCION_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.PROCESO_ATENCION_ESTADO_INVALIDO,
                    "No se pudo crear proceso de atención: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el proceso atencion: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los procesos de atención",
            description = "Devuelve una lista con todos los procesos de atención registrados.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<ProcesosAtencionDTO>> findAll(){
        List<ProcesosAtencionDTO> procesosAtencionDTO = procesosAtencionService.findAll();
        return procesosAtencionDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(procesosAtencionDTO);
    }

    @GetMapping("/{idProcesoAtencion}")
    @Operation(
            summary = "Obtener un proceso de atención por ID",
            description = "Devuelve los datos de un proceso de atención específico según su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<ProcesosAtencionDTO> findById(@PathVariable Long idProcesoAtencion){
        return procesosAtencionService.findById(idProcesoAtencion)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PROCESO_ATENCION_NO_ENCONTRADO,
                        idProcesoAtencion,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idProcesoAtencion}")
    @Operation(
            summary = "Actualizar un proceso de atención",
            description = "Modifica los datos de un proceso de atención existente identificado por su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<ProcesosAtencionDTO> ***REMOVED***(@RequestBody ProcesosAtencionDTO procesosAtencionDTO, @PathVariable Long idProcesoAtencion){
        return procesosAtencionService.findById(idProcesoAtencion)
                .map(procesosAtencionExistente -> ResponseEntity.ok(procesosAtencionService.***REMOVED***(procesosAtencionDTO, idProcesoAtencion)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PROCESO_ATENCION_NO_ENCONTRADO,
                        idProcesoAtencion,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @DeleteMapping("/{idProcesoAtencion}")
    @Operation(
            summary = "Eliminar un proceso de atención",
            description = "Elimina un proceso de atención registrado en el sistema utilizando su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idProcesoAtencion){
        return procesosAtencionService.findById(idProcesoAtencion)
                .map(procesosAtencionDTO-> {
                    procesosAtencionService.delete(idProcesoAtencion);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PROCESO_ATENCION_NO_ENCONTRADO,
                        idProcesoAtencion,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }
}
