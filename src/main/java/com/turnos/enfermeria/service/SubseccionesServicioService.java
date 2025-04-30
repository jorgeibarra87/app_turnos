package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.SubseccionesServicioDTO;
import com.turnos.enfermeria.model.entity.SeccionesServicio;
import com.turnos.enfermeria.model.entity.SubseccionesServicio;
import com.turnos.enfermeria.repository.SeccionesServicioRepository;
import com.turnos.enfermeria.repository.SubseccionesServicioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SubseccionesServicioService {

    private final SubseccionesServicioRepository subseccionesServicioRepo;
    private final SeccionesServicioRepository seccionesServicioRepository;
    private final ModelMapper modelMapper;

    public SubseccionesServicioDTO create(SubseccionesServicioDTO subseccionesServicioDTO) {

        SeccionesServicio seccionesServicio = seccionesServicioRepository.findById(subseccionesServicioDTO.getIdSeccionServicio())
                .orElseThrow(() -> new RuntimeException("Seccion Servicio no encontrada."));
        SubseccionesServicio subseccionesServicio = modelMapper.map(subseccionesServicioDTO, SubseccionesServicio.class);
        subseccionesServicio.setNombre(subseccionesServicioDTO.getNombre());
        subseccionesServicio.setSeccionesServicios(seccionesServicio);
        SubseccionesServicio subseccionesServicioGuardado = subseccionesServicioRepo.save(subseccionesServicio);

        return modelMapper.map(subseccionesServicioGuardado, SubseccionesServicioDTO.class);

    }


    public SubseccionesServicioDTO update(SubseccionesServicioDTO detalleSubseccionesServicioDTO, Long id) {

        SeccionesServicio seccionesServicio = seccionesServicioRepository.findById(detalleSubseccionesServicioDTO.getIdSeccionServicio())
                .orElseThrow(() -> new RuntimeException("Seccion Servicio no encontrada."));

        SubseccionesServicio  subseccionesServicioExistente = subseccionesServicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subseccion no encontrada"));

        SubseccionesServicioDTO subseccionesServicioDTO = modelMapper.map(subseccionesServicioExistente, SubseccionesServicioDTO.class);

        if (detalleSubseccionesServicioDTO.getNombre() != null) {
            subseccionesServicioExistente.setNombre(detalleSubseccionesServicioDTO.getNombre());
        }
        if (detalleSubseccionesServicioDTO.getIdSeccionServicio() != null) {
            subseccionesServicioExistente.setSeccionesServicios(seccionesServicio);
        }

        // Guardar en la base de datos
        SubseccionesServicio subseccionesServicioActualizado = subseccionesServicioRepo.save(subseccionesServicioExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(subseccionesServicioActualizado, SubseccionesServicioDTO.class);
    }

    public Optional<SubseccionesServicioDTO> findById(Long idSubseccionServicio) {
        return subseccionesServicioRepo.findById(idSubseccionServicio)
                .map(subseccionesServicio -> modelMapper.map(subseccionesServicio, SubseccionesServicioDTO.class)); // Convertir a DTO
    }

    public List<SubseccionesServicioDTO> findAll() {
        return subseccionesServicioRepo.findAll()
                .stream()
                .map(subseccionesServicio -> modelMapper.map(subseccionesServicio, SubseccionesServicioDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idSubseccionServicio) {
        // Buscar el bloque en la base de datos
        SubseccionesServicio subseccionesServicioEliminar = subseccionesServicioRepo.findById(idSubseccionServicio)
                .orElseThrow(() -> new EntityNotFoundException("Bloque no encontrado"));

        // Convertir la entidad a DTO
        SubseccionesServicioDTO subseccionesServicioDTO = modelMapper.map(subseccionesServicioEliminar, SubseccionesServicioDTO.class);

        subseccionesServicioRepo.deleteById(idSubseccionServicio);
    }
}
