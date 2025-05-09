package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.ProcesosContratoDTO;
import com.turnos.enfermeria.service.ProcesosContratoService;
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
@RequestMapping("/procesosContrato")
//@Tag(name = "Procesos de Contrato", description = "Gestión de los procesos relacionados con contratos")
public class ProcesosContratoController {

    @Autowired
    private ProcesosContratoService procesosContratoService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo proceso de contrato",
            description = "Registra un nuevo proceso de contrato en el sistema.",
            tags={"Contratos"}
    )
    public ResponseEntity<ProcesosContratoDTO> create(@RequestBody ProcesosContratoDTO procesosContratoDTO){
        try {
            ProcesosContratoDTO nuevoProcesosContratoDTO = procesosContratoService.create(procesosContratoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProcesosContratoDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.PROCESO_CONTRATO_NO_ENCONTRADO,
                    procesosContratoDTO.getIdContrato(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.PROCESO_CONTRATO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.PROCESO_CONTRATO_ESTADO_INVALIDO,
                    "No se pudo crear proceso de contrato: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el proceso de contrato: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los procesos de contrato",
            description = "Devuelve una lista de todos los procesos de contrato registrados.",
            tags={"Contratos"}
    )
    public ResponseEntity<List<ProcesosContratoDTO>> findAll(){
        List<ProcesosContratoDTO> procesosContratoDTO = procesosContratoService.findAll();
        return procesosContratoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(procesosContratoDTO);
    }

    @GetMapping("/{idProcesoContrato}")
    @Operation(
            summary = "Obtener un proceso de contrato por ID",
            description = "Recupera la información de un proceso de contrato específico usando su ID.",
            tags={"Contratos"}
    )
    public ResponseEntity<ProcesosContratoDTO> findById(@PathVariable Long idProcesoContrato){
        return procesosContratoService.findById(idProcesoContrato)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PROCESO_CONTRATO_NO_ENCONTRADO,
                        idProcesoContrato,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idProcesoContrato}")
    @Operation(
            summary = "Actualizar un proceso de contrato",
            description = "Modifica la información de un proceso de contrato existente, identificado por su ID.",
            tags={"Contratos"}
    )
    public ResponseEntity<ProcesosContratoDTO> ***REMOVED***(@RequestBody ProcesosContratoDTO procesosContratoDTO, @PathVariable Long idProcesoContrato){
        return procesosContratoService.findById(idProcesoContrato)
                .map(procesosContratoExistente -> ResponseEntity.ok(procesosContratoService.***REMOVED***(procesosContratoDTO, idProcesoContrato)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PROCESO_CONTRATO_NO_ENCONTRADO,
                        idProcesoContrato,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @DeleteMapping("/{idProcesoContrato}")
    @Operation(
            summary = "Eliminar un proceso de contrato",
            description = "Elimina del sistema un proceso de contrato específico utilizando su ID.",
            tags={"Contratos"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idProcesoContrato){
        return procesosContratoService.findById(idProcesoContrato)
                .map(procesosContratoDTO-> {
                    procesosContratoService.delete(idProcesoContrato);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PROCESO_CONTRATO_NO_ENCONTRADO,
                        idProcesoContrato,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }
}