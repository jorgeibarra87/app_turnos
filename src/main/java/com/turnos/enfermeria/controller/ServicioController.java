package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.ServicioDTO;
import com.turnos.enfermeria.service.ServicioService;
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
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/servicio")
@Tag(name = "Servicios", description = "Operaciones relacionadas con los servicios del sistema")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;
    @Autowired
    private HttpServletRequest request;


    @PostMapping
    @Operation(summary = "Crear servicio", description = "Registra un nuevo servicio en el sistema",
            tags={"Servicios", "Cuadro de Turnos"})
    public ResponseEntity<ServicioDTO> create(@RequestBody ServicioDTO servicioDTO){
        try {
            ServicioDTO nuevoServicioDTO = servicioService.create(servicioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicioDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.SERVICIO_NO_ENCONTRADO,
                    servicioDTO.getIdServicio(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.SERVICIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.SERVICIO_ESTADO_INVALIDO,
                    "No se pudo crear el servicio: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }

    }

    @GetMapping
    @Operation(summary = "Listar todos los servicios", description = "Devuelve una lista con todos los servicios registrados",
            tags={"Servicios", "Cuadro de Turnos"})
    public ResponseEntity<List<ServicioDTO>> findAll(){
        List<ServicioDTO> servicioDTO = servicioService.findAll();
        return servicioDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(servicioDTO);
    }

    @GetMapping("/{idServicio}")
    @Operation(summary = "Obtener servicio por ID", description = "Devuelve un servicio específico según su identificador",
            tags={"Servicios", "Cuadro de Turnos"})
    public ResponseEntity<ServicioDTO> findById(@PathVariable Long idServicio){
        return servicioService.findById(idServicio)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.SERVICIO_NO_ENCONTRADO,
                        idServicio,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idServicio}")
    @Operation(summary = "Actualizar servicio", description = "Actualiza los datos de un servicio existente",
            tags={"Servicios", "Cuadro de Turnos"})
    public ResponseEntity<ServicioDTO> update(@RequestBody ServicioDTO servicioDTO, @PathVariable("idServicio") Long idServicio){
        return servicioService.findById(idServicio)
                .map(servicioExistente -> ResponseEntity.ok(servicioService.update(servicioDTO, idServicio)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.SERVICIO_NO_ENCONTRADO,
                        idServicio,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

//    @DeleteMapping("/{idServicio}")
//    @Operation(summary = "Eliminar servicio", description = "Elimina un servicio existente por su ID",
//            tags={"Servicios", "Cuadro de Turnos"})
//    public ResponseEntity<Object> delete(@PathVariable Long idServicio){
//        return servicioService.findById(idServicio)
//                .map(servicioDTO-> {
//                    servicioService.delete(idServicio);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.SERVICIO_NO_ENCONTRADO,
//                        idServicio,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }
}
