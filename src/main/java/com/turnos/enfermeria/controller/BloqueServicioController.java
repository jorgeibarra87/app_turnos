package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.service.BloqueServicioService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Validated
@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/bloqueServicio")
//@Tag(name = "Bloque de Servicio", description = "API para gestionar los bloques de servicio en el sistema de turnos")
public class BloqueServicioController {

    @Autowired
    private BloqueServicioService bloqueServicioService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(summary = "Crear un nuevo bloque de servicio",
            description = "Crea un nuevo bloque de servicio y lo guarda en la base de datos.",
            tags={"Servicios"})
    public ResponseEntity<BloqueServicioDTO> create(@Valid @RequestBody BloqueServicioDTO bloqueServicioDTO) {
        try {
            BloqueServicioDTO nuevoBloque = bloqueServicioService.create(bloqueServicioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBloque);
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.BLOQUE_SERVICIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todos los bloques de servicio", description = "Devuelve una lista de todos los bloques de servicio registrados.",
            tags={"Servicios"})
    public ResponseEntity<List<BloqueServicioDTO>> findAll(){
        List<BloqueServicioDTO> bloqueServicioDTO = bloqueServicioService.findAll();
        return bloqueServicioDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(bloqueServicioDTO);
    }

    @GetMapping("/{idBloqueServicio}")
    @Operation(summary = "Obtener un bloque de servicio por ID", description = "Devuelve un bloque de servicio específico mediante su ID.",
            tags={"Servicios"})
    public ResponseEntity<BloqueServicioDTO> findById(@PathVariable("idBloqueServicio") Long idBloqueServicio){
        return bloqueServicioService.findById(idBloqueServicio)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.BLOQUE_SERVICIO_NO_ENCONTRADO,
                        idBloqueServicio,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idBloqueServicio}")
    @Operation(summary = "Actualizar un bloque de servicio",
            description = "Actualiza los datos de un bloque de servicio existente según su ID.",
            tags={"Servicios"})
    public ResponseEntity<BloqueServicioDTO> update(
            @Valid @RequestBody BloqueServicioDTO bloqueServicioDTO,
            @PathVariable("idBloqueServicio") Long idBloqueServicio) {

        try {
            return bloqueServicioService.findById(idBloqueServicio)
                    .map(existente -> ResponseEntity.ok(bloqueServicioService.update(bloqueServicioDTO, idBloqueServicio)))
                    .orElseThrow(() -> new GenericNotFoundException(
                            CodigoError.BLOQUE_SERVICIO_NO_ENCONTRADO,
                            idBloqueServicio,
                            request.getMethod(),
                            request.getRequestURI()
                    ));
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.BLOQUE_SERVICIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (Exception e) {
            throw new GenericConflictException(
                    CodigoError.BLOQUE_SERVICIO_CONFLICTO,
                    "Error al actualizar el bloque: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

//    @DeleteMapping("/{idBloqueServicio}")
//    @Operation(summary = "Eliminar un bloque de servicio",
//            description = "Elimina un bloque de servicio del sistema por su ID.",
//            tags={"Servicios"})
//    public ResponseEntity<Void> delete(@PathVariable("idBloqueServicio") Long idBloqueServicio) {
//        try {
//            bloqueServicioService.findById(idBloqueServicio)
//                    .orElseThrow(() -> new GenericNotFoundException(
//                            CodigoError.BLOQUE_SERVICIO_NO_ENCONTRADO,
//                            idBloqueServicio,
//                            request.getMethod(),
//                            request.getRequestURI()
//                    ));
//
//            bloqueServicioService.delete(idBloqueServicio);
//            return ResponseEntity.noContent().build();
//
//        } catch (IllegalStateException e) {
//            throw new GenericConflictException(
//                    CodigoError.BLOQUE_SERVICIO_CONFLICTO,
//                    "No se puede eliminar: " + e.getMessage(),
//                    request.getMethod(),
//                    request.getRequestURI()
//            );
//        }
//    }
}
