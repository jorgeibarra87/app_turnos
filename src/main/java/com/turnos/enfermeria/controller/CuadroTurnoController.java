package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.CambiosCuadroTurnoDTO;
import com.turnos.enfermeria.model.dto.CuadroTurnoDTO;
import com.turnos.enfermeria.service.CuadroTurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
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

//    @PostMapping
//    public ResponseEntity<CuadroTurnoDTO> crearCuadroTurno(@RequestBody CuadroTurnoDTO cuadroTurno) {
//        CuadroTurnoDTO nuevoCuadro = cuadroTurnoService.crearCuadroTurno(cuadroTurno);
//        return ResponseEntity.ok(nuevoCuadro);
//    }

    @PostMapping
    @Operation(summary = "Crear un cuadro de turnos", description = "Crea un nuevo cuadro de turnos con su información básica.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<CuadroTurnoDTO> crearCuadroTurno(@RequestBody CuadroTurnoDTO cuadroTurnoDTO) {
        CuadroTurnoDTO nuevoCuadro = cuadroTurnoService.crearCuadroTurno(cuadroTurnoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCuadro);
    }

//    @GetMapping
//    public List<CuadroTurno> obtenerCuadrosTurno() {
//        return cuadroTurnoService.obtenerCuadrosTurno();
//    }

    @GetMapping
    @Operation(summary = "Listar cuadros de turnos", description = "Obtiene todos los cuadros de turnos registrados en el sistema.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<List<CuadroTurnoDTO>> obtenerCuadrosTurno() {
        List<CuadroTurnoDTO> cuadrosTurno = cuadroTurnoService.obtenerCuadrosTurno();
        return ResponseEntity.ok(cuadrosTurno);
    }

//    @GetMapping("/{id}/historial")
//    public List<CambiosCuadroTurno> obtenerHistorial(@PathVariable Long id) {
//        return cuadroTurnoService.obtenerHistorialCuadroTurno(id);
//    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "Obtener historial de un cuadro de turnos", description = "Devuelve los cambios realizados sobre un cuadro de turnos específico.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<List<CambiosCuadroTurnoDTO>> obtenerHistorial(@PathVariable Long id) {
        List<CambiosCuadroTurnoDTO> historial = cuadroTurnoService.obtenerHistorialCuadroTurno(id);
        return ResponseEntity.ok(historial);
    }

//    @PutMapping("/{id}")
//    public CuadroTurno actualizarCuadroTurno(@PathVariable Long id, @RequestBody CuadroTurno nuevoCuadro) {
//        return cuadroTurnoService.actualizarCuadroTurno(id, nuevoCuadro);
//    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> actualizarCuadroTurno(@PathVariable Long id, @RequestBody CuadroTurno cuadroTurno) {
//        try {
//            CuadroTurno actualizado = cuadroTurnoService.actualizarCuadroTurno(id, cuadroTurno);
//            return ResponseEntity.ok(actualizado);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
//        }
//    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cuadro de turnos", description = "Modifica los datos de un cuadro de turnos existente.",
            tags={"Cuadro de Turnos"})
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Cuadro de turnos actualizado correctamente"),
//            @ApiResponse(responseCode = "404", description = "Cuadro de turnos no encontrado"),
//            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
//    })
    public ResponseEntity<?> actualizarCuadroTurno(@PathVariable Long id, @RequestBody CuadroTurnoDTO cuadroTurnoDTO) {
        try {
            CuadroTurnoDTO actualizado = cuadroTurnoService.actualizarCuadroTurno(id, cuadroTurnoDTO);
            return ResponseEntity.ok(actualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }

//    @PostMapping("/restaurar/{idCambio}")
//    public CuadroTurno restaurarCuadroTurno(@PathVariable Long idCambio) {
//        return cuadroTurnoService.restaurarCuadroTurno(idCambio);
//    }

    @PostMapping("/restaurar/{idCambio}")
    @Operation(summary = "Restaurar cuadro de turnos", description = "Restaura un cuadro de turnos a partir de una versión previa identificada por ID de cambio.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<CuadroTurnoDTO> restaurarCuadroTurno(@PathVariable Long idCambio) {
        CuadroTurnoDTO cuadroRestaurado = cuadroTurnoService.restaurarCuadroTurno(idCambio);
        return ResponseEntity.ok(cuadroRestaurado);
    }

//    @PutMapping("/cambiar-estado")
//    public ResponseEntity<String> cambiarEstadoCuadrosYTurnos(
//            @RequestParam String estadoActual,
//            @RequestParam String nuevoEstado) {
//
//        cuadroTurnoService.cambiarEstadoDeCuadrosYTurnos(estadoActual, nuevoEstado);
//        return ResponseEntity.ok("Todos los cuadros de turno y sus turnos con estado '" + estadoActual +
//                "' fueron cambiados a '" + nuevoEstado + "'.");
//    }

    @PutMapping("/cambiar-estado")
    @Operation(summary = "Cambiar estado de cuadros y turnos", description = "Cambia el estado de todos los cuadros y turnos que coincidan con un estado actual.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<Map<String, List<?>>> cambiarEstadoCuadrosYTurnos(
            @RequestParam String estadoActual,
            @RequestParam String nuevoEstado) {

        Map<String, List<?>> cambios = cuadroTurnoService.cambiarEstadoDeCuadrosYTurnos(estadoActual, nuevoEstado);
        return ResponseEntity.ok(cambios);
    }

    @PutMapping("/{id}/turno-excepcion")
    @Operation(summary = "Actualizar turno de excepción", description = "Actualiza el flag de excepción para un cuadro de turnos.",
            tags={"Cuadro de Turnos"})
    public ResponseEntity<CuadroTurnoDTO> actualizarTurnoExcepcion(@PathVariable Long id, @RequestBody Boolean nuevoValor) {
        CuadroTurnoDTO actualizado = cuadroTurnoService.actualizarTurnoExcepcion(id, nuevoValor, "ACTUALIZAR_TURNO_EXCEPCION");
        return ResponseEntity.ok(actualizado);
    }
}
