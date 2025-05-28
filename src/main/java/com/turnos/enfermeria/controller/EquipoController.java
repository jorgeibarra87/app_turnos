package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.EquipoDTO;
import com.turnos.enfermeria.service.EquipoService;
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
@RequestMapping("/equipo")
//@Tag(name = "Equipo", description = "Gestión de equipos de trabajo en el sistema de turnos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo equipo",
            description = "Registra un nuevo equipo con su nombre, tipo y miembros asociados.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> create(@RequestBody EquipoDTO equipoDTO){
        try {
            EquipoDTO nuevoEquipoDTO = equipoService.create(equipoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEquipoDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.EQUIPO_NO_ENCONTRADO,
                    equipoDTO.getIdEquipo(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.EQUIPO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.EQUIPO_ESTADO_INVALIDO,
                    "No se pudo crear equipo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el equipo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los equipos",
            description = "Devuelve una lista con todos los equipos registrados en el sistema.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<EquipoDTO>> findAll(){
        List<EquipoDTO> equipoDTO = equipoService.findAll();
        return equipoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(equipoDTO);
    }

    @GetMapping("/{idEquipo}")
    @Operation(
            summary = "Buscar equipo por ID",
            description = "Devuelve los datos de un equipo específico usando su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> findById(@PathVariable Long idEquipo){
        return equipoService.findById(idEquipo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.EQUIPO_NO_ENCONTRADO,
                        idEquipo,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idEquipo}")
    @Operation(
            summary = "Actualizar equipo",
            description = "Modifica los datos de un equipo existente, identificado por su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> update(@RequestBody EquipoDTO equipoDTO, @PathVariable Long idEquipo){
        return equipoService.findById(idEquipo)
                .map(equipoExistente -> ResponseEntity.ok(equipoService.update(equipoDTO, idEquipo)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.EQUIPO_NO_ENCONTRADO,
                        idEquipo,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }



    @DeleteMapping("/{idEquipo}")
    @Operation(
            summary = "Eliminar equipo",
            description = "Elimina un equipo existente del sistema según su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idEquipo){
        return equipoService.findById(idEquipo)
                .map(equipoDTO-> {
                    equipoService.delete(idEquipo);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.EQUIPO_NO_ENCONTRADO,
                        idEquipo,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }



    /**
     * Crea un equipo basado en un perfil específico
     */
    @PostMapping("/crear-por-perfil")
    @Operation(
            summary = "crear por perfil",
            description = "Crea un equipo basado en un perfil específico.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> crearEquipoPorPerfil(
            @RequestParam Long idTitulo,
            @RequestParam Long idCuadroTurno) {
        try {
            EquipoDTO equipoCreado = equipoService.createEquipoByPerfil(idTitulo, idCuadroTurno);
            return ResponseEntity.ok(equipoCreado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene todos los equipos de un perfil específico
     */
    @GetMapping("/por-perfil/{idTitulo}")
    @Operation(
            summary = "equipos por perfil",
            description = "Obtiene todos los equipos de un perfil específico.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<EquipoDTO>> obtenerEquiposPorPerfil(@PathVariable Long idTitulo) {
        try {
            List<EquipoDTO> equipos = equipoService.findEquiposByPerfil(idTitulo);
            return ResponseEntity.ok(equipos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cuenta los equipos de un perfil específico
     */
    @GetMapping("/contar-por-perfil/{idTitulo}")
    @Operation(
            summary = "cuenta equipos por perfil",
            description = "Cuenta los equipos de un perfil específico.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<Long> contarEquiposPorPerfil(@PathVariable Long idTitulo) {
        try {
            long cantidad = equipoService.contarEquiposByPerfil(idTitulo);
            return ResponseEntity.ok(cantidad);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
