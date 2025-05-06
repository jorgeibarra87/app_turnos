package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.service.BloqueServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Validated
@RestController
@RequestMapping("/bloqueServicio")
//@Tag(name = "Bloque de Servicio", description = "API para gestionar los bloques de servicio en el sistema de turnos")
public class BloqueServicioController {

    @Autowired
    private BloqueServicioService bloqueServicioService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(summary = "Crear un nuevo bloque de servicio", description = "Crea un nuevo bloque de servicio y lo guarda en la base de datos.",
            tags={"Servicios"})
    public ResponseEntity<BloqueServicioDTO> create(@RequestBody BloqueServicioDTO bloqueServicioDTO){
        BloqueServicioDTO nuevoBloqueServicioDTO = bloqueServicioService.create(bloqueServicioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBloqueServicioDTO);
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
//    public ResponseEntity<BloqueServicioDTO> findById(@PathVariable("idBloqueServicio") Long idBloqueServicio){
//        return bloqueServicioService.findById(idBloqueServicio)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    @PutMapping("/{idBloqueServicio}")
    @Operation(summary = "Actualizar un bloque de servicio", description = "Actualiza los datos de un bloque de servicio existente según su ID.",
            tags={"Servicios"})
    public ResponseEntity<BloqueServicioDTO> ***REMOVED***(@RequestBody BloqueServicioDTO bloqueServicioDTO, @PathVariable("idBloqueServicio") Long idBloqueServicio){
        return bloqueServicioService.findById(idBloqueServicio)
                .map(bloqueServicioExistente -> ResponseEntity.ok(bloqueServicioService.***REMOVED***(bloqueServicioDTO, idBloqueServicio)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idBloqueServicio}")
    @Operation(summary = "Eliminar un bloque de servicio", description = "Elimina un bloque de servicio del sistema por su ID.",
            tags={"Servicios"})
    public ResponseEntity<Object> delete(@PathVariable("idBloqueServicio") Long idBloqueServicio){
        return bloqueServicioService.findById(idBloqueServicio)
                .map(bloqueServicioDTO-> {
                    bloqueServicioService.delete(idBloqueServicio);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
