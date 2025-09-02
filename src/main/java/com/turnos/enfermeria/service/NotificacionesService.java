package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.ActualizacionEstadoDTO;
import com.turnos.enfermeria.model.dto.NotificacionDTO;
import com.turnos.enfermeria.model.entity.Notificaciones;
import com.turnos.enfermeria.repository.NotificacionesRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class NotificacionesService {

    private final NotificacionesRepository notificacionesRepository;
    private final ModelMapper modelMapper;

    public NotificacionDTO create(NotificacionDTO notificacionDTO) {
        Notificaciones notificaciones = modelMapper.map(notificacionDTO, Notificaciones.class);

        notificaciones.setCorreo(notificacionDTO.getCorreo());
        notificaciones.setMensaje(notificacionDTO.getMensaje());
        notificaciones.setEstado(notificacionDTO.getEstado());
        notificaciones.setPermanente(notificacionDTO.getPermanente());
        notificaciones.setEstadoNotificacion(notificacionDTO.getEstadoNotificacion());
        notificaciones.setFechaEnvio(notificacionDTO.getFechaEnvio() != null ?
                notificacionDTO.getFechaEnvio() : new Date());
        notificaciones.setAutomatico(notificacionDTO.getAutomatico() != null ?
                notificacionDTO.getAutomatico() : false);

        Notificaciones notificacionesGuardado = notificacionesRepository.save(notificaciones);

        return modelMapper.map(notificacionesGuardado, NotificacionDTO.class);
    }

    public NotificacionDTO update(NotificacionDTO detalleNotificacionDTO, Long id) {
        Notificaciones notificacionExistente = notificacionesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("notificacion no encontrada"));

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
        if (detalleNotificacionDTO.getFechaEnvio() != null) {
            notificacionExistente.setFechaEnvio(detalleNotificacionDTO.getFechaEnvio());
        }
        if (detalleNotificacionDTO.getAutomatico() != null) {
            notificacionExistente.setAutomatico(detalleNotificacionDTO.getAutomatico());
        }

        // Guardar en la base de datos
        Notificaciones notificacionActualizada = notificacionesRepository.save(notificacionExistente);

        // Convertir a DTO antes de retornar
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
        // Buscar el notificacion en la base de datos
        Notificaciones notificacionEliminar = notificacionesRepository.findById(idNotificacion)
                .orElseThrow(() -> new EntityNotFoundException("Notificacion no encontrado"));

        notificacionesRepository.deleteById(idNotificacion);
    }

    /**
     * Obtener correos predeterminados activos (permanente = true AND estado = 'activo')
     */
    public List<NotificacionDTO> getCorreosPredeterminadosActivos() {
        return notificacionesRepository.findCorreosPredeterminadosActivos()
                .stream()
                .map(notificacion -> modelMapper.map(notificacion, NotificacionDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Obtener correos seleccionables activos (permanente = false AND estado_notificacion = 'activo')
     */
    public List<NotificacionDTO> getCorreosSeleccionablesActivos() {
        return notificacionesRepository.findCorreosSeleccionablesActivos()
                .stream()
                .map(notificacion -> modelMapper.map(notificacion, NotificacionDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Obtener todos los correos activos para envío automático
     */
    public List<NotificacionDTO> getTodosCorreosActivos() {
        return notificacionesRepository.findTodosCorreosActivos()
                .stream()
                .map(notificacion -> modelMapper.map(notificacion, NotificacionDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Obtener correos por tipo (permanente true/false)
     */
    public List<NotificacionDTO> getCorreosPorTipo(Boolean permanente) {
        return notificacionesRepository.findCorreosPorTipo(permanente)
                .stream()
                .map(notificacion -> modelMapper.map(notificacion, NotificacionDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Crear o actualizar notificación evitando duplicados por correo
     */
    public NotificacionDTO createOrUpdate(NotificacionDTO notificacionDTO) {
        // Buscar si ya existe el correo
        List<Notificaciones> existentes = notificacionesRepository.findByCorreo(notificacionDTO.getCorreo());

        if (!existentes.isEmpty()) {
            // Si existe, actualizar el más reciente
            Notificaciones existente = existentes.get(existentes.size() - 1);
            return update(notificacionDTO, existente.getIdNotificacion());
        } else {
            // Si no existe, crear nuevo
            return create(notificacionDTO);
        }
    }

    /**
     * Actualizar estados de múltiples correos
     */
    @Transactional
    public List<NotificacionDTO> actualizarEstadosCorreos(List<ActualizacionEstadoDTO> actualizaciones) {
        List<NotificacionDTO> resultados = new ArrayList<>();

        for (ActualizacionEstadoDTO actualizacion : actualizaciones) {
            try {
                Notificaciones notificacion = notificacionesRepository.findById(actualizacion.getIdNotificacion())
                        .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));

                if (actualizacion.getEstado() != null) {
                    notificacion.setEstado(actualizacion.getEstado());
                }
                if (actualizacion.getEstadoNotificacion() != null) {
                    notificacion.setEstadoNotificacion(actualizacion.getEstadoNotificacion());
                }

                Notificaciones actualizada = notificacionesRepository.save(notificacion);
                resultados.add(modelMapper.map(actualizada, NotificacionDTO.class));

            } catch (EntityNotFoundException e) {
                // Log error pero continúa con las siguientes
                log.error("Error actualizando notificación ID {}: {}",
                        actualizacion.getIdNotificacion(), e.getMessage());
            }
        }

        return resultados;
    }

    /**
     * Enviar notificaciones automáticas
     */
    @Transactional
    public List<NotificacionDTO> enviarNotificacionesAutomaticas(List<NotificacionDTO> notificaciones) {
        List<NotificacionDTO> enviadas = new ArrayList<>();

        for (NotificacionDTO notificacion : notificaciones) {
            try {
                // Marcar como automática y establecer fecha de envío
                notificacion.setFechaEnvio(new Date());
                notificacion.setAutomatico(true);

                NotificacionDTO enviada = create(notificacion);
                enviadas.add(enviada);

                log.info("Notificación automática enviada a: {}", notificacion.getCorreo());

            } catch (Exception e) {
                log.error("Error enviando notificación automática a {}: {}",
                        notificacion.getCorreo(), e.getMessage());
            }
        }

        return enviadas;
    }

    /**
     * MÉTODO: Enviar notificaciones manuales
     * Este método procesa notificaciones creadas manualmente por los usuarios
     */
    @Transactional
    public List<NotificacionDTO> enviarNotificaciones(List<NotificacionDTO> notificaciones) {
        List<NotificacionDTO> notificacionesEnviadas = new ArrayList<>();

        try {
            for (NotificacionDTO notificacionDTO : notificaciones) {
                // 1. Validar datos de la notificación
                validarNotificacion(notificacionDTO);

                // 2. Crear entidad de notificación
                Notificaciones notificacion = new Notificaciones();
                notificacion.setCorreo(notificacionDTO.getCorreo());
                notificacion.setMensaje(notificacionDTO.getMensaje());
                notificacion.setEstadoNotificacion(notificacionDTO.getEstadoNotificacion());
                notificacion.setEstado(notificacionDTO.getEstado());
                notificacion.setPermanente(notificacionDTO.getPermanente());
                notificacion.setFechaEnvio(notificacionDTO.getFechaEnvio() != null ?
                        notificacionDTO.getFechaEnvio() : new Date());
                notificacion.setAutomatico(false); // Es manual

                // 3. Guardar en base de datos
                Notificaciones notificacionGuardada = notificacionesRepository.save(notificacion);

                // 4. Enviar el correo electrónico (si tienes servicio de email)
                try {
                    enviarCorreoElectronico(notificacionGuardada);
                    notificacionGuardada.setEstado(true); // Marcamos como enviado exitosamente
                } catch (Exception emailException) {
                    log.error("Error al enviar correo a {}: {}",
                            notificacionGuardada.getCorreo(), emailException.getMessage());
                    notificacionGuardada.setEstado(false); // Marcamos como falló el envío
                }

                // 5. Actualizar estado en BD
                notificacionGuardada = notificacionesRepository.save(notificacionGuardada);

                // 6. Convertir a DTO y agregar a lista de enviadas
                NotificacionDTO notificacionEnviadaDTO = modelMapper.map(notificacionGuardada, NotificacionDTO.class);
                notificacionesEnviadas.add(notificacionEnviadaDTO);

                log.info("Notificación manual procesada para: {}", notificacion.getCorreo());
            }

            return notificacionesEnviadas;

        } catch (Exception e) {
            log.error("Error al procesar notificaciones manuales: {}", e.getMessage());
            throw new RuntimeException("Error al enviar notificaciones: " + e.getMessage());
        }
    }

    /**
     * Validar datos de notificación
     */
    private void validarNotificacion(NotificacionDTO notificacionDTO) {
        if (notificacionDTO.getCorreo() == null || notificacionDTO.getCorreo().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

        if (!isValidEmail(notificacionDTO.getCorreo())) {
            throw new IllegalArgumentException("El formato del correo no es válido: " + notificacionDTO.getCorreo());
        }

        if (notificacionDTO.getMensaje() == null || notificacionDTO.getMensaje().trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
    }

    /**
     * Validar formato de email
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    /**
     * Enviar correo electrónico (implementar según tu servicio de email)
     */
    private void enviarCorreoElectronico(Notificaciones notificacion) throws Exception {
        // Aquí implementarías el envío real del correo
        // Ejemplo usando JavaMail o el servicio que tengas configurado

        /*
        // Ejemplo básico:
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificacion.getCorreo());
        message.setSubject("Notificación del Sistema");
        message.setText(notificacion.getMensaje());
        message.setFrom("sistema@hospital.com");

        mailSender.send(message);
        */

        // Por ahora solo simulamos el envío
        log.info("Simulando envío de correo a: {}", notificacion.getCorreo());
    }

    /**
     * Obtener correos únicos (último registro por correo)
     */
    public List<NotificacionDTO> getCorreosUnicos() {
        return notificacionesRepository.findCorreosUnicos()
                .stream()
                .map(notificacion -> modelMapper.map(notificacion, NotificacionDTO.class))
                .collect(Collectors.toList());
    }
}
