package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.CambiosCuadroTurnoDTO;
import com.turnos.enfermeria.model.dto.CuadroTurnoDTO;
import com.turnos.enfermeria.service.CuadroTurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cuadro-turnos")
@Tag(name = "Cuadro de Turnos", description = "Endpoints para gestionar cuadros de turnos y su historial")
public class CuadroTurnoController {

    @Autowired
    private CuadroTurnoService cuadroTurnoService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(summary = "Crear un cuadro de turnos", description = "Crea un nuevo cuadro de turnos con su información básica.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<CuadroTurnoDTO> crearCuadroTurno(@RequestBody CuadroTurnoDTO cuadroTurnoDTO) {
        try {
            CuadroTurnoDTO nuevoCuadro = cuadroTurnoService.crearCuadroTurno(cuadroTurnoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCuadro);
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CUADRO_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (Exception e) {
            throw new GenericConflictException(
                    CodigoError.CUADRO_TURNO_CONFLICTO,
                    "Error al crear el Cuadro de turno: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping
    @Operation(summary = "Listar cuadros de turnos", description = "Obtiene todos los cuadros de turnos registrados en el sistema.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<List<CuadroTurnoDTO>> obtenerCuadrosTurno() {
        List<CuadroTurnoDTO> cuadrosTurno = cuadroTurnoService.obtenerCuadrosTurno();
        return cuadrosTurno.isEmpty() ? ResponseEntity.noContent().build() :ResponseEntity.ok(cuadrosTurno);
    }

    @GetMapping("/{idCuadroTurno}")
    @Operation(summary = "Obtener un cuadro de turno por ID", description = "Devuelve un cuadro de turno específico mediante su ID.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<CuadroTurnoDTO> findById(@PathVariable("idCuadroTurno") Long idCuadroTurno){
        return cuadroTurnoService.findById(idCuadroTurno)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.CUADRO_TURNO_NO_ENCONTRADO,
                        idCuadroTurno,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "Obtener historial de un cuadro de turnos", description = "Devuelve los cambios realizados sobre un cuadro de turnos específico.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<List<CambiosCuadroTurnoDTO>> obtenerHistorial(@PathVariable Long id) {
        List<CambiosCuadroTurnoDTO> historial = cuadroTurnoService.obtenerHistorialCuadroTurno(id);
        if (historial == null) { // Si tu servicio devuelve null cuando no existe
            throw new GenericNotFoundException(
                    CodigoError.HISTORIAL_CUADRO_TURNO_NO_ENCONTRADO,
                    id,
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
        return historial.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(historial);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cuadro de turnos", description = "Modifica los datos de un cuadro de turnos existente.",
            tags={"Cuadro de Turnos"})

    public ResponseEntity<?> actualizarCuadroTurno(@PathVariable Long id, @RequestBody CuadroTurnoDTO cuadroTurnoDTO) {
        try {
            CuadroTurnoDTO actualizado = cuadroTurnoService.actualizarCuadroTurno(id, cuadroTurnoDTO);
            return ResponseEntity.ok(actualizado);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.CUADRO_TURNO_NO_ENCONTRADO,
                    id,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CUADRO_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CUADRO_TURNOS_ESTADO_INVALIDO,
                    "No se puede actualizar: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al actualizar el cuadro: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @PostMapping("/restaurar/{idCambio}")
    @Operation(summary = "Restaurar cuadro de turnos", description = "Restaura un cuadro de turnos a partir de una versión previa identificada por ID de cambio.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<CuadroTurnoDTO> restaurarCuadroTurno(@PathVariable Long idCambio) {
        try {
            CuadroTurnoDTO cuadroRestaurado = cuadroTurnoService.restaurarCuadroTurno(idCambio);
            return ResponseEntity.ok(cuadroRestaurado);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.CUADRO_TURNO_NO_ENCONTRADO,
                    idCambio,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CUADRO_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CUADRO_TURNOS_ESTADO_INVALIDO,
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
    @Operation(summary = "Cambiar estado de cuadros y turnos", description = "Cambia el estado de todos los cuadros y turnos que coincidan con un estado actual.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<Map<String, List<?>>> cambiarEstadoCuadrosYTurnos(
            @RequestParam String estadoActual,
            @RequestParam String nuevoEstado) {
        try {
            Map<String, List<?>> cambios = cuadroTurnoService.cambiarEstadoDeCuadrosYTurnos(estadoActual, nuevoEstado);
            return ResponseEntity.ok(cambios);
        }catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CUADRO_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CUADRO_TURNOS_ESTADO_INVALIDO,
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

    @PutMapping("/{id}/turno-excepcion")
    @Operation(summary = "Actualizar turno de excepción", description = "Actualiza el flag de excepción para un cuadro de turnos.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<CuadroTurnoDTO> actualizarTurnoExcepcion(@PathVariable Long id, @RequestBody Boolean nuevoValor) {
        try {
            CuadroTurnoDTO actualizado = cuadroTurnoService.actualizarTurnoExcepcion(id, nuevoValor, "ACTUALIZAR_TURNO_EXCEPCION");
            return ResponseEntity.ok(actualizado);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.CUADRO_TURNO_NO_ENCONTRADO,
                    id,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CUADRO_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CUADRO_TURNOS_ESTADO_INVALIDO,
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
}
