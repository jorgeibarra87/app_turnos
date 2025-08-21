package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.CambiosTurnoDTO;
import com.turnos.enfermeria.service.CambiosTurnoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.validation.annotation.Validated;


@Validated
@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/cambiosTurno")
//@Tag(name = "Cambios de Turno", description = "Operaciones relacionadas con el historial de cambios en los turnos")
public class CambiosTurnoController {
    @Autowired
    private CambiosTurnoService cambiosTurnoService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(summary = "Crear un cambio de turno", description = "Registra un nuevo cambio asociado a un turno.",
            tags={"Turnos"})
    public ResponseEntity<CambiosTurnoDTO> create(@RequestBody CambiosTurnoDTO cambiosTurnoDTO){
        try {
            CambiosTurnoDTO nuevoCambiosTurnoDTO = cambiosTurnoService.create(cambiosTurnoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCambiosTurnoDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.CAMBIOS_TURNO_NO_ENCONTRADO,
                    cambiosTurnoDTO.getIdCambio(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CAMBIOS_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CAMBIOS_TURNO_ESTADO_INVALIDO,
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
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.CAMBIOS_TURNO_NO_ENCONTRADO,
                        idCambio,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idCambio}")
    @Operation(summary = "Actualizar un cambio de turno", description = "Modifica la información de un cambio existente.",
            tags={"Turnos"})
    public ResponseEntity<CambiosTurnoDTO> update(@RequestBody CambiosTurnoDTO cambiosTurnoDTO, @PathVariable Long idCambio){
        return cambiosTurnoService.findById(idCambio)
                .map(cambiosTurnoExistente -> ResponseEntity.ok(cambiosTurnoService.update(cambiosTurnoDTO, idCambio)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.CAMBIOS_TURNO_NO_ENCONTRADO,
                        idCambio,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

//    @DeleteMapping("/{idCambio}")
//    @Operation(summary = "Eliminar un cambio de turno", description = "Elimina un cambio del historial mediante su ID.",
//            tags={"Turnos"})
//    public ResponseEntity<Object> delete(@PathVariable Long idCambio){
//        return cambiosTurnoService.findById(idCambio)
//                .map(cambiosTurnoDTO-> {
//                    cambiosTurnoService.delete(idCambio);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.CAMBIOS_TURNO_NO_ENCONTRADO,
//                        idCambio,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }
}
