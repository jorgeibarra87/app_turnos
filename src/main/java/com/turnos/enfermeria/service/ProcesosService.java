package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.ProcesosDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.Macroprocesos;
import com.turnos.enfermeria.model.entity.Procesos;
import com.turnos.enfermeria.repository.MacroprocesosRepository;
import com.turnos.enfermeria.repository.ProcesosRepository;
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
public class ProcesosService {

    private final ProcesosRepository procesosRepo;
    private final MacroprocesosRepository macroprocesosRepository;
    private final ModelMapper modelMapper;

    public ProcesosDTO create(ProcesosDTO procesosDTO) {
        Macroprocesos macroprocesos = macroprocesosRepository.findById(procesosDTO.getIdMacroproceso())
                .orElseThrow(() -> new RuntimeException("macroproceso no encontrado."));
        Procesos procesos = modelMapper.map(procesosDTO, Procesos.class);
        procesos.setNombre(procesosDTO.getNombre());
        procesos.setMacroprocesos(macroprocesos);

        Procesos procesoGuardado = procesosRepo.save(procesos);

        return modelMapper.map(procesoGuardado, ProcesosDTO.class);

    }

    public ProcesosDTO update(ProcesosDTO detalleProcesosDTO, Long id) {
        Macroprocesos macroprocesos = macroprocesosRepository.findById(detalleProcesosDTO.getIdMacroproceso())
                .orElseThrow(() -> new RuntimeException("macroproceso no encontrado."));
        Procesos procesoExistente = procesosRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));

        ProcesosDTO procesosDTO = modelMapper.map(procesoExistente, ProcesosDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleProcesosDTO.getIdProceso()!= null) {
            procesoExistente.setIdProceso(detalleProcesosDTO.getIdProceso());
        }
        if (detalleProcesosDTO.getNombre() != null) {
            procesoExistente.setNombre(detalleProcesosDTO.getNombre());
        }
        if (detalleProcesosDTO.getIdMacroproceso() != null) {
            procesoExistente.setMacroprocesos(macroprocesos);
        }

        // Guardar en la base de datos
        Procesos procesoActualizado = procesosRepo.save(procesoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(procesoActualizado, ProcesosDTO.class);
    }

    public Optional<ProcesosDTO> findById(Long idProceso) {
        return procesosRepo.findById(idProceso)
                .map(procesos -> modelMapper.map(procesos, ProcesosDTO.class)); // Convertir a DTO
    }

    public List<ProcesosDTO> findAll() {
        return procesosRepo.findAll()
                .stream()
                .map(procesos -> modelMapper.map(procesos, ProcesosDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long idProceso) {
        // Buscar el proceso en la base de datos
        Procesos precesoEliminar = procesosRepo.findById(idProceso)
                .orElseThrow(() -> new EntityNotFoundException("proceso no encontrado"));

        // Convertir la entidad a DTO
        ProcesosDTO procesosDTO = modelMapper.map(precesoEliminar, ProcesosDTO.class);

        procesosRepo.deleteById(idProceso);
    }
}
