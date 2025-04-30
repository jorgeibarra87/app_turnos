package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.NotificacionDTO;
import com.turnos.enfermeria.model.entity.Notificaciones;
import com.turnos.enfermeria.repository.NotificacionesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class NotificacionesService {

    private final NotificacionesRepository notificacionesRepository;
    private final ModelMapper modelMapper;

    public NotificacionDTO create(NotificacionDTO notificacionDTO) {
        Notificaciones notificaciones = modelMapper.map(notificacionDTO, Notificaciones.class);

        notificaciones.setCorreo(notificacionDTO.getCorreo());
        notificaciones.setMensaje(notificacionDTO.getMensaje());
        notificaciones.setEstado(notificacionDTO.getEstado());
        notificaciones.setPermanente(notificacionDTO.getPermanente());

        Notificaciones notificacionesGuardado = notificacionesRepository.save(notificaciones);

        return modelMapper.map(notificacionesGuardado, NotificacionDTO.class);

    }


    public NotificacionDTO update(NotificacionDTO detalleNotificacionDTO, Long id) {
        Notificaciones notificacionExistente = notificacionesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("notificacion no encontrada"));

        NotificacionDTO notificacionDTO = modelMapper.map(notificacionExistente, NotificacionDTO.class);

        if (detalleNotificacionDTO.getCorreo()!= null) {
            notificacionExistente.setCorreo(detalleNotificacionDTO.getCorreo());
        }
        if (detalleNotificacionDTO.getMensaje()!= null) {
            notificacionExistente.setMensaje(detalleNotificacionDTO.getMensaje());
        }
        if (detalleNotificacionDTO.getEstado()!= null) {
            notificacionExistente.setEstado(detalleNotificacionDTO.getEstado());
        }
        if (detalleNotificacionDTO.getPermanente()!= null) {
            notificacionExistente.setPermanente(detalleNotificacionDTO.getPermanente());
        }

        // Guardar en la base de datos
        Notificaciones notificacionActualizada = notificacionesRepository.save(notificacionExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(notificacionActualizada, NotificacionDTO.class);
    }

    public Optional<NotificacionDTO> findById(Long idNotificacion) {
        return notificacionesRepository.findById(idNotificacion)
                .map(notificaciones -> modelMapper.map(notificaciones, NotificacionDTO.class)); // Convertir a DTO
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

        // Convertir la entidad a DTO
        NotificacionDTO notificacionDTO = modelMapper.map(notificacionEliminar, NotificacionDTO.class);

        notificacionesRepository.deleteById(idNotificacion);
    }
}
