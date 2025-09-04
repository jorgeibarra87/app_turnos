package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.ActualizacionEstadoDTO;
import com.turnos.enfermeria.model.dto.NotificacionDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class NotificacionService {

    private final NotificacionesRepository notificacionesRepository;
    private final ConfiguracionCorreosRepository configuracionCorreosRepository;
    private final HistorialNotificacionesRepository historialRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    // ====== MÉTODOS CRUD BÁSICOS ======

    public NotificacionDTO create(NotificacionDTO notificacionDTO) {
        Notificaciones notificaciones = new Notificaciones();

        // Establecer valores básicos
        notificaciones.setCorreo(notificacionDTO.getCorreo());
        notificaciones.setMensaje(notificacionDTO.getMensaje());
        notificaciones.setEstado(notificacionDTO.getEstado() != null ? notificacionDTO.getEstado() : true);
        notificaciones.setPermanente(notificacionDTO.getPermanente() != null ? notificacionDTO.getPermanente() : false);
        notificaciones.setEstadoNotificacion(notificacionDTO.getEstadoNotificacion() != null ?
                notificacionDTO.getEstadoNotificacion() : "activo");
        notificaciones.setFechaEnvio(notificacionDTO.getFechaEnvio() != null ?
                notificacionDTO.getFechaEnvio() : new Date());
        notificaciones.setAutomatico(notificacionDTO.getAutomatico() != null ?
                notificacionDTO.getAutomatico() : false);

        Notificaciones notificacionesGuardado = notificacionesRepository.save(notificaciones);
        return modelMapper.map(notificacionesGuardado, NotificacionDTO.class);
    }

    public NotificacionDTO update(NotificacionDTO detalleNotificacionDTO, Long id) {
        Notificaciones notificacionExistente = notificacionesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        if (detalleNotificacionDTO.getCorreo() != null) {
            notificacionExistente.setCorreo(detalleNotificacionDTO.getCorreo());
        }
        if (detalleNotificacionDTO.getMensaje() != null) {
            notificacionExistente.setMensaje(detalleNotificacionDTO.getMensaje());
        }
        if (detalleNotificacionDTO.getEstado() != null) {
            notificacionExistente.setEstado(detalleNotificacionDTO.getEstado());
        }
        if (detalleNotificacionDTO.getPermanente() != null) {
            notificacionExistente.setPermanente(detalleNotificacionDTO.getPermanente());
        }
        if (detalleNotificacionDTO.getEstadoNotificacion() != null) {
            notificacionExistente.setEstadoNotificacion(detalleNotificacionDTO.getEstadoNotificacion());
        }

        Notificaciones notificacionActualizada = notificacionesRepository.save(notificacionExistente);
        return modelMapper.map(notificacionActualizada, NotificacionDTO.class);
    }

    public Optional<NotificacionDTO> findById(Long idNotificacion) {
        return notificacionesRepository.findById(idNotificacion)
                .map(notificaciones -> modelMapper.map(notificaciones, NotificacionDTO.class));
    }

    public List<NotificacionDTO> findAll() {
        return notificacionesRepository.findAll()
                .stream()
                .map(notificaciones -> modelMapper.map(notificaciones, NotificacionDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idNotificacion) {
        Notificaciones notificacionEliminar = notificacionesRepository.findById(idNotificacion)
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));
        notificacionesRepository.deleteById(idNotificacion);
    }

    // ====== MÉTODOS PARA CONFIGURACIÓN DE CORREOS ======

    public List<NotificacionDTO> getCorreosPredeterminadosActivos() {
        List<ConfiguracionCorreos> correos = configuracionCorreosRepository
                .findByTipoCorreoAndActivoTrue(TipoCorreo.PERMANENTE);

        return correos.stream()
                .map(correo -> {
                    NotificacionDTO dto = new NotificacionDTO();
                    dto.setIdNotificacion(correo.getIdConfiguracion());
                    dto.setCorreo(correo.getCorreo());
                    dto.setPermanente(true);
                    dto.setEstado(correo.getActivo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<NotificacionDTO> getCorreosSeleccionablesActivos() {
        List<ConfiguracionCorreos> correos = configuracionCorreosRepository
                .findByTipoCorreoAndActivoTrue(TipoCorreo.SELECCIONABLE);

        return correos.stream()
                .map(correo -> {
                    NotificacionDTO dto = new NotificacionDTO();
                    dto.setIdNotificacion(correo.getIdConfiguracion());
                    dto.setCorreo(correo.getCorreo());
                    dto.setPermanente(false);
                    dto.setEstado(correo.getActivo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<NotificacionDTO> getTodosCorreosActivos() {
        List<ConfiguracionCorreos> correos = configuracionCorreosRepository.findByActivoTrue();

        if (correos.isEmpty()) {
            crearCorreosConfiguracionPrueba();
            correos = configuracionCorreosRepository.findByActivoTrue();
        }

        return correos.stream()
                .map(correo -> {
                    NotificacionDTO dto = new NotificacionDTO();
                    dto.setIdNotificacion(correo.getIdConfiguracion());
                    dto.setCorreo(correo.getCorreo());
                    dto.setPermanente(correo.getTipoCorreo() == TipoCorreo.PERMANENTE);
                    dto.setEstado(correo.getActivo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ====== MÉTODO PRINCIPAL PARA ENVÍO DE CORREOS ======

    @Transactional
    public List<NotificacionDTO> enviarNotificacionesAutomaticas(List<NotificacionDTO> notificaciones) {
        List<NotificacionDTO> resultados = new ArrayList<>();

        for (NotificacionDTO notificacion : notificaciones) {
            // Crear registro en historial ANTES del envío
            HistorialNotificaciones historial = new HistorialNotificaciones();
            historial.setCorreo(notificacion.getCorreo());
            historial.setAsunto(notificacion.getAsunto());
            historial.setMensaje(notificacion.getMensaje());
            historial.setTipoEnvio(TipoEnvio.AUTOMATICO);
            historial.setEstadoEnvio(EstadoEnvio.PENDIENTE);
            //historial.setIdCuadroTurno(notificacion.getIdCuadroTurno());
            historial.setIntentos(1);

            try {
                log.info("Enviando correo automático a: {}", notificacion.getCorreo());

                // ENVIAR EL CORREO REALMENTE
                emailService.enviarCorreoHTML(
                        notificacion.getCorreo(),
                        notificacion.getAsunto(),
                        notificacion.getMensaje()
                );

                // Actualizar historial: ENVIADO
                historial.setEstadoEnvio(EstadoEnvio.ENVIADO);
                notificacion.setEstado(true);

                log.info("✅ Correo enviado exitosamente a: {}", notificacion.getCorreo());

            } catch (Exception emailException) {
                // Actualizar historial: FALLIDO
                historial.setEstadoEnvio(EstadoEnvio.FALLIDO);
                historial.setErrorMensaje(emailException.getMessage());
                notificacion.setEstado(false);

                log.error("❌ Error enviando correo a {}: {}",
                        notificacion.getCorreo(), emailException.getMessage());
            } finally {
                // SIEMPRE guardar en historial
                try {
                    HistorialNotificaciones guardado = historialRepository.save(historial);
                    log.info("📝 Historial guardado con ID: {}", guardado.getIdHistorial());
                } catch (Exception dbException) {
                    log.error("❌ Error guardando historial: {}", dbException.getMessage());
                }
            }

            resultados.add(notificacion);
        }

        log.info("🔄 Envío automático completado. Total procesados: {}", resultados.size());
        return resultados;
    }

    @Transactional
    public List<NotificacionDTO> enviarNotificaciones(List<NotificacionDTO> notificaciones) {
        List<NotificacionDTO> resultados = new ArrayList<>();

        for (NotificacionDTO notificacion : notificaciones) {
            // Crear registro en historial ANTES del envío
            HistorialNotificaciones historial = new HistorialNotificaciones();
            historial.setCorreo(notificacion.getCorreo());
            historial.setAsunto(notificacion.getAsunto());
            historial.setMensaje(notificacion.getMensaje());
            historial.setTipoEnvio(TipoEnvio.MANUAL);
            historial.setEstadoEnvio(EstadoEnvio.PENDIENTE);
            historial.setIntentos(1);

            try {
                log.info("Enviando correo manual a: {}", notificacion.getCorreo());

                // ENVIAR EL CORREO REALMENTE
                emailService.enviarCorreoHTML(
                        notificacion.getCorreo(),
                        notificacion.getAsunto(),
                        notificacion.getMensaje()
                );

                // Actualizar historial: ENVIADO
                historial.setEstadoEnvio(EstadoEnvio.ENVIADO);
                notificacion.setEstado(true);

                log.info("✅ Correo enviado exitosamente a: {}", notificacion.getCorreo());

            } catch (Exception emailException) {
                // Actualizar historial: FALLIDO
                historial.setEstadoEnvio(EstadoEnvio.FALLIDO);
                historial.setErrorMensaje(emailException.getMessage());
                notificacion.setEstado(false);

                log.error("❌ Error enviando correo a {}: {}",
                        notificacion.getCorreo(), emailException.getMessage());
            } finally {
                // SIEMPRE guardar en historial
                try {
                    HistorialNotificaciones guardado = historialRepository.save(historial);
                    log.info("📝 Historial guardado con ID: {}", guardado.getIdHistorial());
                } catch (Exception dbException) {
                    log.error("❌ Error guardando historial: {}", dbException.getMessage());
                }
            }

            resultados.add(notificacion);
        }

        log.info("🔄 Envío manual completado. Total procesados: {}", resultados.size());
        return resultados;
    }

    // ====== MÉTODOS PARA GESTIONAR CONFIGURACIÓN DE CORREOS ======

    public NotificacionDTO agregarCorreoConfiguracion(NotificacionDTO notificacionDTO) {
        // Verificar si ya existe
        if (configuracionCorreosRepository.existsByCorreo(notificacionDTO.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        ConfiguracionCorreos configuracion = new ConfiguracionCorreos();
        configuracion.setCorreo(notificacionDTO.getCorreo());
        configuracion.setTipoCorreo(notificacionDTO.getPermanente() ?
                TipoCorreo.PERMANENTE : TipoCorreo.SELECCIONABLE);
        configuracion.setActivo(true);

        ConfiguracionCorreos guardado = configuracionCorreosRepository.save(configuracion);

        NotificacionDTO resultado = new NotificacionDTO();
        resultado.setIdNotificacion(guardado.getIdConfiguracion());
        resultado.setCorreo(guardado.getCorreo());
        resultado.setPermanente(guardado.getTipoCorreo() == TipoCorreo.PERMANENTE);
        resultado.setEstado(guardado.getActivo());

        return resultado;
    }

    public List<NotificacionDTO> actualizarEstadosCorreos(List<ActualizacionEstadoDTO> actualizaciones) {
        List<NotificacionDTO> resultados = new ArrayList<>();

        for (ActualizacionEstadoDTO actualizacion : actualizaciones) {
            try {
                ConfiguracionCorreos configuracion = configuracionCorreosRepository
                        .findById(actualizacion.getIdNotificacion())
                        .orElseThrow(() -> new EntityNotFoundException("Configuración no encontrada"));

                if (actualizacion.getEstado() != null) {
                    configuracion.setActivo(actualizacion.getEstado());
                }

                ConfiguracionCorreos actualizada = configuracionCorreosRepository.save(configuracion);

                NotificacionDTO dto = new NotificacionDTO();
                dto.setIdNotificacion(actualizada.getIdConfiguracion());
                dto.setCorreo(actualizada.getCorreo());
                dto.setPermanente(actualizada.getTipoCorreo() == TipoCorreo.PERMANENTE);
                dto.setEstado(actualizada.getActivo());

                resultados.add(dto);

            } catch (EntityNotFoundException e) {
                log.error("Error actualizando configuración ID {}: {}",
                        actualizacion.getIdNotificacion(), e.getMessage());
            }
        }

        return resultados;
    }

    /**
     * Obtener correos por tipo (permanente/seleccionable)
     */
    public List<NotificacionDTO> getCorreosPorTipo(Boolean permanente) {
        TipoCorreo tipoCorreo = permanente ? TipoCorreo.PERMANENTE : TipoCorreo.SELECCIONABLE;
        List<ConfiguracionCorreos> correos = configuracionCorreosRepository
                .findByTipoCorreoAndActivoTrue(tipoCorreo);

        return correos.stream()
                .map(correo -> {
                    NotificacionDTO dto = new NotificacionDTO();
                    dto.setIdNotificacion(correo.getIdConfiguracion());
                    dto.setCorreo(correo.getCorreo());
                    dto.setPermanente(correo.getTipoCorreo() == TipoCorreo.PERMANENTE);
                    dto.setEstado(correo.getActivo());
                    // Para compatibilidad con el frontend
                    dto.setEstadoNotificacion(correo.getActivo() ? "activo" : "inactivo");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtener correos únicos (todos los correos activos sin duplicados)
     */
    public List<NotificacionDTO> getCorreosUnicos() {
        return getTodosCorreosActivos();
    }

    /**
     * Crear o actualizar correo (método de compatibilidad)
     */
    public NotificacionDTO createOrUpdate(NotificacionDTO notificacionDTO) {
        try {
            // Intentar agregar como configuración de correo
            return agregarCorreoConfiguracion(notificacionDTO);
        } catch (IllegalArgumentException e) {
            // Si ya existe, buscar y actualizar
            Optional<ConfiguracionCorreos> existente = configuracionCorreosRepository
                    .findByCorreo(notificacionDTO.getCorreo());

            if (existente.isPresent()) {
                ConfiguracionCorreos configuracion = existente.get();

                if (notificacionDTO.getEstado() != null) {
                    configuracion.setActivo(notificacionDTO.getEstado());
                }

                ConfiguracionCorreos actualizada = configuracionCorreosRepository.save(configuracion);

                NotificacionDTO resultado = new NotificacionDTO();
                resultado.setIdNotificacion(actualizada.getIdConfiguracion());
                resultado.setCorreo(actualizada.getCorreo());
                resultado.setPermanente(actualizada.getTipoCorreo() == TipoCorreo.PERMANENTE);
                resultado.setEstado(actualizada.getActivo());

                return resultado;
            } else {
                throw new EntityNotFoundException("Correo no encontrado: " + notificacionDTO.getCorreo());
            }
        }
    }

    // ====== MÉTODO PARA CREAR DATOS DE PRUEBA EN CONFIGURACIÓN ======
    private void crearCorreosConfiguracionPrueba() {
        try {
            log.info("🔧 Creando configuración de correos de prueba...");

            if (configuracionCorreosRepository.count() == 0) {
                // Correo predeterminado
                ConfiguracionCorreos predeterminado = new ConfiguracionCorreos();
                predeterminado.setCorreo("admin@hospital.com");
                predeterminado.setTipoCorreo(TipoCorreo.PERMANENTE);
                predeterminado.setActivo(true);
                configuracionCorreosRepository.save(predeterminado);

                // Correo seleccionable
                ConfiguracionCorreos seleccionable = new ConfiguracionCorreos();
                seleccionable.setCorreo("jorgeibarraing@gmail.com");
                seleccionable.setTipoCorreo(TipoCorreo.SELECCIONABLE);
                seleccionable.setActivo(true);
                configuracionCorreosRepository.save(seleccionable);

                log.info("✅ Configuración de correos de prueba creada");
            }
        } catch (Exception e) {
            log.error("❌ Error creando configuración de prueba: {}", e.getMessage());
        }
    }


    // ====== MÉTODO AUXILIAR PARA CREAR DATOS DE PRUEBA ======

    private void crearCorreosPrueba() {
        try {
            log.info("🔧 Creando correos de prueba...");

            // Verificar si ya existen
            if (notificacionesRepository.count() == 0) {
                // Crear correo predeterminado
                Notificaciones correoPredeterminado = new Notificaciones();
                correoPredeterminado.setCorreo("admin@hospital.com");
                correoPredeterminado.setMensaje("Correo predeterminado del sistema");
                correoPredeterminado.setEstado(true);
                correoPredeterminado.setPermanente(true);
                correoPredeterminado.setEstadoNotificacion("activo");
                correoPredeterminado.setFechaEnvio(new Date());
                correoPredeterminado.setAutomatico(false);
                notificacionesRepository.save(correoPredeterminado);

                // Crear correo seleccionable
                Notificaciones correoSeleccionable = new Notificaciones();
                correoSeleccionable.setCorreo("jorgeibarraing@gmail.com");
                correoSeleccionable.setMensaje("Correo seleccionable de prueba");
                correoSeleccionable.setEstado(true);
                correoSeleccionable.setPermanente(false);
                correoSeleccionable.setEstadoNotificacion("activo");
                correoSeleccionable.setFechaEnvio(new Date());
                correoSeleccionable.setAutomatico(false);
                notificacionesRepository.save(correoSeleccionable);

                log.info("✅ Correos de prueba creados exitosamente");
            }
        } catch (Exception e) {
            log.error("❌ Error creando correos de prueba: {}", e.getMessage());
        }
    }
}
