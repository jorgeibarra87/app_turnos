package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.NotificacionDTO;
import com.turnos.enfermeria.service.NotificacionesService;
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
@RequestMapping("/notificaciones")
@Tag(name = "Notificaciones", description = "Operaciones relacionadas con el manejo de notificaciones automáticas o manuales del sistema")
public class NotificacionesController {

    @Autowired
    private NotificacionesService notificacionesService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear una nueva notificación",
            description = "Crea una nueva notificación que puede estar asociada a eventos del sistema como cambios de turno o cuadros de turnos."
    )
    public ResponseEntity<NotificacionDTO> create(@RequestBody NotificacionDTO notificacionDTO){
        try {
            NotificacionDTO nuevaNotificacionDTO = notificacionesService.create(notificacionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaNotificacionDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.NOTIFICACION_NO_ENCONTRADA,
                    notificacionDTO.getIdNotificacion(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.NOTIFICACION_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.NOTIFICACION_ESTADO_INVALIDO,
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

    @GetMapping
    @Operation(
            summary = "Obtener todas las notificaciones",
            description = "Devuelve una lista de todas las notificaciones registradas."
    )
    public ResponseEntity<List<NotificacionDTO>> findAll(){
        List<NotificacionDTO> notificacionDTO = notificacionesService.findAll();
        return notificacionDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notificacionDTO);
    }

    @GetMapping("/{idNotificacion}")
    @Operation(
            summary = "Buscar notificación por ID",
            description = "Obtiene una notificación específica según su ID."
    )
    public ResponseEntity<NotificacionDTO> findById(@PathVariable("idNotificacion") Long idNotificacion){
        return notificacionesService.findById(idNotificacion)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.NOTIFICACION_NO_ENCONTRADA,
                        idNotificacion,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idNotificacion}")
    @Operation(
            summary = "Actualizar una notificación existente",
            description = "Modifica el contenido, destinatarios u otros datos de una notificación previamente registrada."
    )
    public ResponseEntity<NotificacionDTO> update(@RequestBody NotificacionDTO notificacionDTO, @PathVariable("idNotificacion") Long idNotificacion){
        return notificacionesService.findById(idNotificacion)
                .map(notificacionExistente -> ResponseEntity.ok(notificacionesService.update(notificacionDTO, idNotificacion)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.NOTIFICACION_NO_ENCONTRADA,
                        idNotificacion,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

//    @DeleteMapping("/{idNotificacion}")
//    @Operation(
//            summary = "Eliminar notificación",
//            description = "Elimina una notificación específica por su ID."
//    )
//    public ResponseEntity<Object> delete(@PathVariable("idNotificacion") Long idNotificacion){
//        return notificacionesService.findById(idNotificacion)
//                .map(notificacionDTO-> {
//                    notificacionesService.delete(idNotificacion);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.NOTIFICACION_NO_ENCONTRADA,
//                        idNotificacion,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }
}
