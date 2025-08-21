package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.ServicioDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.Procesos;
import com.turnos.enfermeria.model.entity.Servicio;
import com.turnos.enfermeria.repository.BloqueServicioRepository;
import com.turnos.enfermeria.repository.ProcesosRepository;
import com.turnos.enfermeria.repository.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ServicioService {

    private final ServicioRepository servicioRepo;
    private final BloqueServicioRepository bloqueServicioRepository;
    private final ProcesosRepository procesosRepository;
    private final ModelMapper modelMapper;

    public ServicioDTO create(ServicioDTO servicioDTO) {

        BloqueServicio bloqueServicio = bloqueServicioRepository.findById(servicioDTO.getIdBloqueServicio())
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        Procesos procesos = procesosRepository.findById(servicioDTO.getIdProceso())
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        Servicio servicio = modelMapper.map(servicioDTO, Servicio.class);
        servicio.setIdServicio(servicioDTO.getIdServicio());
        servicio.setNombre(servicioDTO.getNombre());
        servicio.setTipo(servicioDTO.getTipo());
        servicio.setBloqueServicios(bloqueServicio);
        servicio.setProcesos(procesos);

        Servicio servicioGuardado = servicioRepo.save(servicio);

        return modelMapper.map(servicioGuardado, ServicioDTO.class);
    }

    public ServicioDTO update(ServicioDTO detalleServicioDTO, Long id) {

        BloqueServicio bloqueServicio = bloqueServicioRepository.findById(detalleServicioDTO.getIdBloqueServicio())
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        Procesos procesos = procesosRepository.findById(detalleServicioDTO.getIdProceso())
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        Servicio servicioExistente = servicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        ServicioDTO servicioDTO = modelMapper.map(servicioExistente, ServicioDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleServicioDTO.getIdServicio()!= null) {
            servicioExistente.setIdServicio(detalleServicioDTO.getIdServicio());
        }
        if (detalleServicioDTO.getNombre() != null) {
            servicioExistente.setNombre(detalleServicioDTO.getNombre());
        }
        if (detalleServicioDTO.getTipo() != null) {
            servicioExistente.setTipo(detalleServicioDTO.getTipo());
        }
        if (detalleServicioDTO.getIdBloqueServicio() != null) {
            servicioExistente.setBloqueServicios(bloqueServicio);
        }
        if (detalleServicioDTO.getIdProceso() != null) {
            servicioExistente.setProcesos(procesos);
        }

        // Guardar en la base de datos
        Servicio servicioActualizado = servicioRepo.save(servicioExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(servicioActualizado, ServicioDTO.class);
    }

    public Optional<ServicioDTO> findById(Long idServicio) {
        return servicioRepo.findById(idServicio)
                .map(servicio -> modelMapper.map(servicio, ServicioDTO.class)); // Convertir a DTO
    }

    public List<ServicioDTO> findAll() {
        return servicioRepo.findAll()
                .stream()
                .map(servicio -> modelMapper.map(servicio, ServicioDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long idServicio) {
        // Buscar el bloque en la base de datos
        Servicio servicioEliminar = servicioRepo.findById(idServicio)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        // Convertir la entidad a DTO
        ServicioDTO servicioDTO = modelMapper.map(servicioEliminar, ServicioDTO.class);

        servicioRepo.deleteById(idServicio);
    }
}
