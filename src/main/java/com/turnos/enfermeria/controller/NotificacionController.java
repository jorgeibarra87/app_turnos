package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.ActualizacionEstadoDTO;
import com.turnos.enfermeria.model.dto.NotificacionDTO;
import com.turnos.enfermeria.service.EmailService;
import com.turnos.enfermeria.service.NotificacionService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Hidden
@Validated
@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/notificaciones")
@Tag(name = "Notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionesService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private EmailService emailService;

    // ====== RUTAS ESPECÍFICAS PRIMERO ======

    @GetMapping("/correos-predeterminados-activos")
    public ResponseEntity<List<NotificacionDTO>> getCorreosPredeterminadosActivos() {
        List<NotificacionDTO> correos = notificacionesService.getCorreosPredeterminadosActivos();
        return ResponseEntity.ok(correos);
    }

    @GetMapping("/correos-seleccionables-activos")
    public ResponseEntity<List<NotificacionDTO>> getCorreosSeleccionablesActivos() {
        List<NotificacionDTO> correos = notificacionesService.getCorreosSeleccionablesActivos();
        return ResponseEntity.ok(correos);
    }

    @GetMapping("/correos-activos")
    public ResponseEntity<List<NotificacionDTO>> getTodosCorreosActivos() {
        List<NotificacionDTO> correos = notificacionesService.getTodosCorreosActivos();
        return ResponseEntity.ok(correos);
    }

    @GetMapping("/correos-unicos")
    public ResponseEntity<List<NotificacionDTO>> getCorreosUnicos() {
        List<NotificacionDTO> correos = notificacionesService.getCorreosUnicos();
        return ResponseEntity.ok(correos);
    }

    @GetMapping("/correos")
    public ResponseEntity<List<NotificacionDTO>> getCorreosPorTipo(@RequestParam Boolean permanente) {
        List<NotificacionDTO> correos = notificacionesService.getCorreosPorTipo(permanente);
        return ResponseEntity.ok(correos);
    }

    @GetMapping("/validar-configuracion-correo")
    public ResponseEntity<Map<String, Object>> validarConfiguracion() {
        Map<String, Object> respuesta = new HashMap<>();
        boolean valida = emailService.isConfiguracionValida();

        respuesta.put("configuracionValida", valida);
        respuesta.put("mensaje", valida ? "Configuración válida" : "Error en configuración");

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/agregar-correo-configuracion")
    public ResponseEntity<NotificacionDTO> agregarCorreoConfiguracion(@RequestBody NotificacionDTO notificacionDTO) {
        try {
            NotificacionDTO resultado = notificacionesService.agregarCorreoConfiguracion(notificacionDTO);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.NOTIFICACION_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    // ====== ENDPOINTS CRUD BÁSICOS ======

    @PostMapping
    @Operation(summary = "Crear una nueva notificación")
    public ResponseEntity<NotificacionDTO> create(@RequestBody NotificacionDTO notificacionDTO){
        try {
            NotificacionDTO nuevaNotificacionDTO = notificacionesService.create(notificacionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaNotificacionDTO);
        } catch (EntityNotFoundException e) {
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
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todas las notificaciones")
    public ResponseEntity<List<NotificacionDTO>> findAll(){
        List<NotificacionDTO> notificacionDTO = notificacionesService.findAll();
        return notificacionDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notificacionDTO);
    }

    // ✅ MOVER ESTA RUTA AL FINAL (después de todas las rutas específicas)
    @GetMapping("/{idNotificacion}")
    @Operation(summary = "Buscar notificación por ID")
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
    @Operation(summary = "Actualizar una notificación existente")
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

    // ====== ENDPOINTS DE ENVÍO ======

    @PostMapping("/enviar-automaticas")
    public ResponseEntity<List<NotificacionDTO>> enviarNotificacionesAutomaticas(@RequestBody List<NotificacionDTO> notificaciones) {
        List<NotificacionDTO> enviadas = notificacionesService.enviarNotificacionesAutomaticas(notificaciones);
        return ResponseEntity.ok(enviadas);
    }

    @PostMapping("/enviar")
    @Operation(summary = "Enviar notificaciones manuales")
    public ResponseEntity<List<NotificacionDTO>> enviarNotificaciones(@RequestBody List<NotificacionDTO> notificaciones) {
        try {
            List<NotificacionDTO> enviadas = notificacionesService.enviarNotificaciones(notificaciones);
            return ResponseEntity.ok(enviadas);
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.NOTIFICACION_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @PostMapping("/create-or-update")
    public ResponseEntity<NotificacionDTO> createOrUpdate(@RequestBody NotificacionDTO notificacionDTO) {
        NotificacionDTO resultado = notificacionesService.createOrUpdate(notificacionDTO);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/actualizar-estados")
    public ResponseEntity<List<NotificacionDTO>> actualizarEstadosCorreos(@RequestBody List<ActualizacionEstadoDTO> actualizaciones) {
        List<NotificacionDTO> actualizados = notificacionesService.actualizarEstadosCorreos(actualizaciones);
        return ResponseEntity.ok(actualizados);
    }

    @PostMapping("/probar-correo")
    public ResponseEntity<Map<String, Object>> probarCorreo(@RequestParam String destinatario) {
        Map<String, Object> respuesta = new HashMap<>();

        try {
            boolean exito = emailService.enviarCorreoPrueba(destinatario);
            respuesta.put("exito", exito);
            respuesta.put("mensaje", exito ? "Correo enviado exitosamente" : "Error al enviar");
        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", "Error: " + e.getMessage());
        }

        return ResponseEntity.ok(respuesta);
    }
}

