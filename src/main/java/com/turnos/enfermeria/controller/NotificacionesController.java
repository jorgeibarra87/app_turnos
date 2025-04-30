package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.NotificacionDTO;
import com.turnos.enfermeria.service.NotificacionesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping
    @Operation(
            summary = "Crear una nueva notificación",
            description = "Crea una nueva notificación que puede estar asociada a eventos del sistema como cambios de turno o cuadros de turnos."
    )
    public ResponseEntity<NotificacionDTO> create(@RequestBody NotificacionDTO notificacionDTO){
        NotificacionDTO nuevaNotificacionDTO = notificacionesService.create(notificacionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaNotificacionDTO);
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
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idNotificacion}")
    @Operation(
            summary = "Actualizar una notificación existente",
            description = "Modifica el contenido, destinatarios u otros datos de una notificación previamente registrada."
    )
    public ResponseEntity<NotificacionDTO> ***REMOVED***(@RequestBody NotificacionDTO notificacionDTO, @PathVariable("idNotificacion") Long idNotificacion){
        return notificacionesService.findById(idNotificacion)
                .map(notificacionExistente -> ResponseEntity.ok(notificacionesService.***REMOVED***(notificacionDTO, idNotificacion)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idNotificacion}")
    @Operation(
            summary = "Eliminar notificación",
            description = "Elimina una notificación específica por su ID."
    )
    public ResponseEntity<Object> delete(@PathVariable("idNotificacion") Long idNotificacion){
        return notificacionesService.findById(idNotificacion)
                .map(notificacionDTO-> {
                    notificacionesService.delete(idNotificacion);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
