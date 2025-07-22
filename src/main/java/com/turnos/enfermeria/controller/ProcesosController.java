package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.ProcesosDTO;
import com.turnos.enfermeria.service.ProcesosService;
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
@RequestMapping("/procesos")
//@Tag(name = "Procesos", description = "Operaciones CRUD para la gestión de procesos")
public class ProcesosController {
    @Autowired
    private ProcesosService procesosService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear un proceso",
            description = "Registra un nuevo proceso en el sistema a partir de los datos proporcionados.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<ProcesosDTO> create(@RequestBody ProcesosDTO procesosDTO){
        try {
            ProcesosDTO nuevoProcesosDTO = procesosService.create(procesosDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProcesosDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.PROCESO_NO_ENCONTRADO,
                    procesosDTO.getIdProceso(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.PROCESO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.PROCESO_ESTADO_INVALIDO,
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
            summary = "Listar procesos",
            description = "Devuelve una lista de todos los procesos registrados en el sistema.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<ProcesosDTO>> findAll(){
        List<ProcesosDTO> procesosDTO = procesosService.findAll();
        return procesosDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(procesosDTO);
    }

    @GetMapping("/{idProceso}")
    @Operation(
            summary = "Obtener proceso por ID",
            description = "Recupera la información de un proceso específico usando su identificador único.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<ProcesosDTO> findById(@PathVariable("idProceso") Long idProceso){
        return procesosService.findById(idProceso)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PROCESO_NO_ENCONTRADO,
                        idProceso,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idProceso}")
    @Operation(
            summary = "Actualizar proceso",
            description = "Modifica los datos de un proceso existente identificado por su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<ProcesosDTO> update(@RequestBody ProcesosDTO procesosDTO, @PathVariable Long idProceso){
        return procesosService.findById(idProceso)
                .map(procesoExistente -> ResponseEntity.ok(procesosService.update(procesosDTO, idProceso)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PROCESO_NO_ENCONTRADO,
                        idProceso,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

//    @DeleteMapping("/{idProceso}")
//    @Operation(
//            summary = "Eliminar proceso",
//            description = "Elimina un proceso del sistema utilizando su ID.",
//            tags={"Cuadro de Turnos"}
//    )
//    public ResponseEntity<Object> delete(@PathVariable Long idProceso){
//        return procesosService.findById(idProceso)
//                .map(bloqueServicioDTO-> {
//                    procesosService.delete(idProceso);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.PROCESO_NO_ENCONTRADO,
//                        idProceso,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }
}
