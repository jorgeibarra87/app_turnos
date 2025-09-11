package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.MacroprocesosDTO;
import com.turnos.enfermeria.service.MacroprocesosService;
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
@RequestMapping("/macroprocesos")
//@Tag(name = "Macroprocesos", description = "CRUD de macroprocesos de gestión o administrativos")
public class MacroprocesosController {

    @Autowired
    private MacroprocesosService macroprocesosService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo macroproceso",
            description = "Registra un nuevo macroproceso con su información básica.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<MacroprocesosDTO> create(@RequestBody MacroprocesosDTO macroprocesosDTO){
        try {
            MacroprocesosDTO nuevoMacroprocesosDTO = macroprocesosService.create(macroprocesosDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMacroprocesosDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.MACROPROCESO_NO_ENCONTRADO,
                    macroprocesosDTO.getIdMacroproceso(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.MACROPROCESO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.MACROPROCESO_ESTADO_INVALIDO,
                    "No se pudo crear macroproceso: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los macroprocesos",
            description = "Devuelve todos los macroprocesos existentes registrados en el sistema.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<MacroprocesosDTO>> findAll(){
        List<MacroprocesosDTO> macroprocesosDTO = macroprocesosService.findAll();
        return macroprocesosDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(macroprocesosDTO);
    }

    @GetMapping("/{idMacroproceso}")
    @Operation(
            summary = "Buscar macroproceso por ID",
            description = "Devuelve un macroproceso específico a partir de su identificador único.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<MacroprocesosDTO> findById(@PathVariable Long idMacroproceso){
        return macroprocesosService.findById(idMacroproceso)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.MACROPROCESO_NO_ENCONTRADO,
                        idMacroproceso,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idMacroproceso}")
    @Operation(
            summary = "Actualizar un macroproceso existente",
            description = "Actualiza los datos del macroproceso identificado por su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<MacroprocesosDTO> update(@RequestBody MacroprocesosDTO macroprocesosDTO, @PathVariable Long idMacroproceso){
        return macroprocesosService.findById(idMacroproceso)
                .map(macroprocesoExistente -> ResponseEntity.ok(macroprocesosService.update(macroprocesosDTO, idMacroproceso)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.MACROPROCESO_NO_ENCONTRADO,
                        idMacroproceso,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }



    @DeleteMapping("/{idMacroproceso}")
    @Operation(
            summary = "Eliminar macroproceso",
            description = "Elimina de forma lógica o definitiva un macroproceso del sistema.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<Object> delete(@PathVariable Long idMacroproceso){
        return macroprocesosService.findById(idMacroproceso)
                .map(macroprocesosDTO-> {
                    macroprocesosService.delete(idMacroproceso);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.MACROPROCESO_NO_ENCONTRADO,
                        idMacroproceso,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }
}
