package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.CambiosTurnoDTO;
import com.turnos.enfermeria.model.dto.TurnoDTO;
import com.turnos.enfermeria.model.entity.CambiosTurno;
import com.turnos.enfermeria.model.entity.Turnos;
import com.turnos.enfermeria.service.TurnosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/turnos")
@AllArgsConstructor
@Tag(name = "Turnos", description = "API para la gestión de turnos y su historial de cambios")
public class TurnosController {

    private final TurnosService turnosService;

    private final HttpServletRequest request;

    @GetMapping
    @Operation(summary = "Obtener todos los turnos", description = "Devuelve una lista de todos los turnos registrados.",
            tags={"Turnos"})
    public ResponseEntity<List<TurnoDTO>> findAll() {
        List<TurnoDTO> turnos = turnosService.findAll();
        return turnos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(turnos);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo turno", description = "Crea un nuevo turno con la información proporcionada.",
            tags={"Turnos"})
    public ResponseEntity<TurnoDTO> create(@RequestBody TurnoDTO turno) {
        try {
            TurnoDTO nuevoTurno = turnosService.create(turno);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTurno);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TURNO_NO_ENCONTRADO,
                    turno.getIdTurno(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TURNOS_ESTADO_INVALIDO,
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

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un turno", description = "Actualiza los datos de un turno existente identificado por su ID.",
            tags={"Turnos"})
    public ResponseEntity<TurnoDTO> actualizarTurno(@PathVariable Long id, @Valid @RequestBody TurnoDTO nuevoTurno) {
        return turnosService.findById(id)
                .map(turnoExistente -> ResponseEntity.ok(turnosService.actualizarTurno(id, nuevoTurno)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TURNO_NO_ENCONTRADO,
                        id,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un turno por ID", description = "Busca un turno por su identificador único.",
            tags={"Turnos"})
    public ResponseEntity<TurnoDTO> findById(@PathVariable Long id) {
        return turnosService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                CodigoError.TURNO_NO_ENCONTRADO,
                id,
                request.getMethod(),
                request.getRequestURI()
        ));
    }


//    /** 📌 Eliminar un turno */
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Eliminar un turno", description = "Elimina un turno específico a partir de su ID.",
//            tags={"Turnos"})
//    public ResponseEntity<Object> delete(@PathVariable Long id) {
//        return turnosService.findById(id)
//                .map(turno -> {
//                    turnosService.eliminarTurno(id);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.TURNO_NO_ENCONTRADO,
//                        id,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }

    @GetMapping("/cambios/{idTurno}")
    @Operation(summary = "Obtener cambios de un turno", description = "Devuelve los cambios registrados asociados a un turno específico.",
            tags={"Turnos"})
    public ResponseEntity<List<CambiosTurnoDTO>> obtenerCambiosDeTurno(@PathVariable Long idTurno) {
        List<CambiosTurnoDTO> cambios = turnosService.obtenerCambiosPorTurno(idTurno);
        if (cambios.isEmpty()) {
            throw new GenericNotFoundException(
                    CodigoError.TURNO_NO_ENCONTRADO,
                    idTurno,
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
        return ResponseEntity.ok(cambios);
        //return cambios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(cambios);
    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "Obtener historial de un turno", description = "Muestra el historial completo de versiones y cambios de un turno.",
            tags={"Turnos"})
    public ResponseEntity<List<CambiosTurnoDTO>> obtenerHistorial(@PathVariable Long id) {
        List<CambiosTurnoDTO> historial = turnosService.obtenerHistorialTurno(id);
        return historial.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(historial);
    }

    @PostMapping("/restaurar/{idCambio}")
    @Operation(summary = "Restaurar un turno a una versión anterior", description = "Restaura un turno utilizando el ID del cambio de versión.",
            tags={"Turnos"})
    public ResponseEntity<TurnoDTO> restaurarTurno(@PathVariable Long idCambio) {
        try{
        return ResponseEntity.ok(turnosService.restaurarTurno(idCambio));
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.RESTAURAR_TURNO_NO_ENCONTRADO,
                    idCambio,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.RESTAURAR_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.RESTAURAR_TURNOS_ESTADO_INVALIDO,
                    "No se pudo restaurar: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al restaurar el cuadro: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping("/restaurar-turnos/{version}")
    @Operation(summary = "Restaurar turnos por versión", description = "Restaura todos los turnos que pertenezcan a una versión específica.",
            tags={"Turnos"})
    public ResponseEntity<List<TurnoDTO>> restaurarTurnos(@PathVariable String version) {
        List<TurnoDTO> turnosRestaurados = turnosService.restaurarTurnosPorVersion(version);
        try {
            return ResponseEntity.ok(turnosRestaurados);
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.RESTAURAR_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.RESTAURAR_TURNOS_ESTADO_INVALIDO,
                    "No se pudo restaurar: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al restaurar el cuadro: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @PutMapping("/cambiar-estado")
    @Operation(summary = "Cambiar estado de todos los turnos", description = "Modifica el estado de todos los turnos que coincidan con el estado actual proporcionado.",
            tags={"Turnos"})
    public ResponseEntity<String> cambiarEstadoTurnos(
            @RequestParam String estadoActual,
            @RequestParam String nuevoEstado) {
        try {
            turnosService.cambiarEstadoDeTodosLosTurnos(estadoActual, nuevoEstado);
            return ResponseEntity.ok("Todos los turnos con estado '" + estadoActual + "' fueron cambiados a '" + nuevoEstado + "'.");
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CAMBIAR_ESTADO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CAMBIAR_ESTADO_ESTADO_INVALIDO,
                    "No se pudo cambiar estado: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al cambiar el estado: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }
}

