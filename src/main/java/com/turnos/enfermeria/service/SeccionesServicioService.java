package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.SeccionesServicioDTO;
import com.turnos.enfermeria.model.entity.SeccionesServicio;
import com.turnos.enfermeria.model.entity.Servicio;
import com.turnos.enfermeria.repository.SeccionesServicioRepository;
import com.turnos.enfermeria.repository.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SeccionesServicioService {

    private final SeccionesServicioRepository seccionesServicioRepo;
    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;

    public SeccionesServicioDTO create(SeccionesServicioDTO seccionesServicioDTO) {

        Servicio servicio = servicioRepository.findById(seccionesServicioDTO.getIdServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));
        SeccionesServicio seccionesServicio = modelMapper.map(seccionesServicioDTO, SeccionesServicio.class);
        seccionesServicio.setIdSeccionServicio(seccionesServicioDTO.getIdSeccionServicio());
        seccionesServicio.setNombre(seccionesServicioDTO.getNombre());
        seccionesServicio.setServicio(servicio);

        SeccionesServicio seccionesServicioGuardado = seccionesServicioRepo.save(seccionesServicio);

        return modelMapper.map(seccionesServicioGuardado, SeccionesServicioDTO.class);

    }

    public SeccionesServicioDTO update(SeccionesServicioDTO detalleSeccionesServicioDTO, Long id) {

        Servicio servicio = servicioRepository.findById(detalleSeccionesServicioDTO.getIdServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));

        SeccionesServicio seccionesServicioExistente = seccionesServicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Seccion Servicio no encontrada"));

        SeccionesServicioDTO seccionesServicioDTO = modelMapper.map(seccionesServicioExistente, SeccionesServicioDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleSeccionesServicioDTO.getIdSeccionServicio()!= null) {
            seccionesServicioExistente.setIdSeccionServicio(detalleSeccionesServicioDTO.getIdSeccionServicio());
        }
        if (detalleSeccionesServicioDTO.getNombre() != null) {
            seccionesServicioExistente.setNombre(detalleSeccionesServicioDTO.getNombre());
        }
        if (detalleSeccionesServicioDTO.getIdServicio()!= null) {
            seccionesServicioExistente.setServicio(servicio);
        }

        // Guardar en la base de datos
        SeccionesServicio seccionesServicioActualizado = seccionesServicioRepo.save(seccionesServicioExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(seccionesServicioActualizado, SeccionesServicioDTO.class);
    }

    public Optional<SeccionesServicioDTO> findById(Long idSeccionServicio) {
        return seccionesServicioRepo.findById(idSeccionServicio)
                .map(seccionesServicio -> modelMapper.map(seccionesServicio, SeccionesServicioDTO.class)); // Convertir a DTO
    }

    public List<SeccionesServicioDTO> findAll() {
        return seccionesServicioRepo.findAll()
                .stream()
                .map(seccionesServicio -> modelMapper.map(seccionesServicio, SeccionesServicioDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long idSeccionServicio) {
        // Buscar el bloque en la base de datos
        SeccionesServicio seccionesServicioEliminar = seccionesServicioRepo.findById(idSeccionServicio)
                .orElseThrow(() -> new EntityNotFoundException("Bloque no encontrado"));

        // Convertir la entidad a DTO
        SeccionesServicioDTO seccionesServicioDTO = modelMapper.map(seccionesServicioEliminar, SeccionesServicioDTO.class);

        seccionesServicioRepo.deleteById(idSeccionServicio);
    }
}