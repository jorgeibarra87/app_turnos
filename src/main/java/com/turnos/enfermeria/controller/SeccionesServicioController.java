package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.SeccionesServicioDTO;
import com.turnos.enfermeria.service.SeccionesServicioService;
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
@RequestMapping("/seccionesServicio")
//@Tag(name = "Secciones de Servicio", description = "Gestión de secciones dentro de un servicio")
public class SeccionesServicioController {

    @Autowired
    private SeccionesServicioService seccionesServicioService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(summary = "Crear sección de servicio", description = "Registra una nueva sección dentro de un servicio",
            tags={"Servicios"})
    public ResponseEntity<SeccionesServicioDTO> create(@RequestBody SeccionesServicioDTO seccionesServicioDTO){
        try {
            SeccionesServicioDTO nuevoSeccionesServicioDTO = seccionesServicioService.create(seccionesServicioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoSeccionesServicioDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.SECCION_SERVICIO_NO_ENCONTRADA,
                    seccionesServicioDTO.getIdSeccionServicio(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.SECCION_SERVICIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.SECCION_SERVICIO_ESTADO_INVALIDO,
                    "No se pudo crear la seccion: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @GetMapping
    @Operation(summary = "Listar todas las secciones", description = "Devuelve una lista de todas las secciones de servicio registradas",
            tags={"Servicios"})
    public ResponseEntity<List<SeccionesServicioDTO>> findAll(){
        List<SeccionesServicioDTO> seccionesServicioDTO = seccionesServicioService.findAll();
        return seccionesServicioDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(seccionesServicioDTO);
    }

    @GetMapping("/{idSeccionServicio}")
    @Operation(summary = "Obtener sección por ID", description = "Devuelve una sección de servicio por su ID",
            tags={"Servicios"})
    public ResponseEntity<SeccionesServicioDTO> findById(@PathVariable Long idSeccionServicio){
        return seccionesServicioService.findById(idSeccionServicio)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.SECCION_SERVICIO_NO_ENCONTRADA,
                        idSeccionServicio,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idSeccionServicio}")
    @Operation(summary = "Actualizar sección", description = "Actualiza los datos de una sección de servicio existente",
            tags={"Servicios"})
    public ResponseEntity<SeccionesServicioDTO> update(@RequestBody SeccionesServicioDTO seccionesServicioDTO, @PathVariable Long idSeccionServicio){
        return seccionesServicioService.findById(idSeccionServicio)
                .map(seccionesServicioExistente -> ResponseEntity.ok(seccionesServicioService.update(seccionesServicioDTO, idSeccionServicio)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.SECCION_SERVICIO_NO_ENCONTRADA,
                        idSeccionServicio,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }
//    @DeleteMapping("/{idSeccionServicio}")
//    @Operation(summary = "Eliminar sección", description = "Elimina una sección de servicio por su ID",
//            tags={"Servicios"})
//    public ResponseEntity<Object> delete(@PathVariable Long idSeccionServicio){
//        return seccionesServicioService.findById(idSeccionServicio)
//                .map(seccionesServicioDTO-> {
//                    seccionesServicioService.delete(idSeccionServicio);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.SECCION_SERVICIO_NO_ENCONTRADA,
//                        idSeccionServicio,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }
}
