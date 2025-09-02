package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.ApiResponse;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.CuadroTurno;
import com.turnos.enfermeria.service.CuadroTurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cuadro-turnos")
@CrossOrigin("http://localhost:5173/")
@Tag(name = "Cuadro de Turnos", description = "Endpoints para gestionar cuadros de turnos y su historial")
public class CuadroTurnoController {

    @Autowired
    private CuadroTurnoService cuadroTurnoService;
    @Autowired
    private HttpServletRequest requestHttp;

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
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );
        } catch (Exception e) {
            throw new GenericConflictException(
                    CodigoError.CUADRO_TURNO_CONFLICTO,
                    "Error al crear el Cuadro de turno: " + e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
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
                        requestHttp.getMethod(),
                        requestHttp.getRequestURI()
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
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );
        }
        return historial.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(historial);
    }

    @GetMapping("/{id}/historialturnos")
    @Operation(summary = "Obtener historial de turnos por cuadro de turno", description = "Devuelve los cambios realizados sobre los turnos de un cuadro.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<List<CambiosTurnoDTO>> obtenerHistorialTurno(@PathVariable Long id) {
        List<CambiosTurnoDTO> historial = cuadroTurnoService.obtenerHistorialTurnos(id);
        if (historial == null) { // Si tu servicio devuelve null cuando no existe
            throw new GenericNotFoundException(
                    CodigoError.HISTORIAL_CUADRO_TURNO_NO_ENCONTRADO,
                    id,
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
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
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CUADRO_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CUADRO_TURNOS_ESTADO_INVALIDO,
                    "No se puede actualizar: " + e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al actualizar el cuadro: " + e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
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
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CUADRO_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CUADRO_TURNOS_ESTADO_INVALIDO,
                    "No se pudo restaurar: " + e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al restaurar el cuadro: " + e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );
        }
    }

    @PutMapping("/cambiar-estado")
    @Operation(summary = "Cambiar estado de cuadros y turnos", description = "Cambia el estado de todos los cuadros y turnos que coincidan con un estado actual.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<CambiosEstadoDTO> cambiarEstadoCuadrosYTurnos(
            @RequestBody CambiarEstadoRequestDTO request) {
        try {
            CambiosEstadoDTO cambios = cuadroTurnoService.cambiarEstadoDeCuadrosYTurnos(request.getEstadoActual(),
                    request.getNuevoEstado(),
                    request.getIdsCuadros());
            return ResponseEntity.ok(cambios);
        }catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CUADRO_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CUADRO_TURNOS_ESTADO_INVALIDO,
                    "No se pudo cambiar el estado: " + e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al restaurar el cuadro: " + e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
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
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CUADRO_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CUADRO_TURNOS_ESTADO_INVALIDO,
                    "No se pudo restaurar: " + e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al restaurar el cuadro: " + e.getMessage(),
                    requestHttp.getMethod(),
                    requestHttp.getRequestURI()
            );
        }
    }

    /**
     * Crear un nuevo cuadro de turno con nombre generado automáticamente
     * Soporta múltiples procesos de atención
     */
    @PostMapping("/crear-total")
    public ResponseEntity<ApiResponse> crearCuadroTurnoTotal(@RequestBody CuadroTurnoRequest request) {
        try {
            CuadroTurno cuadroTurno = cuadroTurnoService.crearCuadroTurnoTotal(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Cuadro de turno creado exitosamente", cuadroTurno));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Datos inválidos: " + e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error interno del servidor"));
        }
    }


    /**
     * Edita un cuadro de turno existente
     * @param id ID del cuadro a editar
     * @param request Datos de actualización
     * @return Cuadro de turno actualizado
     */
    @PutMapping("/{id}/editar-total")
    public ResponseEntity<?> editarCuadroTurnoTotal(
            @PathVariable Long id,
            @Valid @RequestBody CuadroTurnoRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }

        try {
            CuadroTurno cuadroActualizado = cuadroTurnoService.editarCuadroTurnoTotal(id, request);
            return ResponseEntity.ok(cuadroActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/procesos")
    public ResponseEntity<List<ProcesosDTO>> getProcesosFromCuadro(@PathVariable Long id) {
        List<ProcesosDTO> procesos = cuadroTurnoService.obtenerProcesosDesdeCuadroMultiproceso(id);
        return ResponseEntity.ok(procesos);
    }
}

