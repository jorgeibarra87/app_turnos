package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.ProcesosAtencionDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.Procesos;
import com.turnos.enfermeria.model.entity.ProcesosAtencion;
import com.turnos.enfermeria.repository.ProcesosAtencionRepository;
import com.turnos.enfermeria.repository.ProcesosRepository;
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
public class ProcesosAtencionService {

    private  final ProcesosAtencionRepository procesosAtencionRepo;
    private final ProcesosRepository procesosRepository;
    private final ModelMapper modelMapper;

    public ProcesosAtencionDTO create(ProcesosAtencionDTO procesosAtencionDTO) {

        Procesos procesos = procesosRepository.findById(procesosAtencionDTO.getIdProceso())
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        ProcesosAtencion procesosAtencion = modelMapper.map(procesosAtencionDTO, ProcesosAtencion.class);
        procesosAtencion.setIdProcesoAtencion(procesosAtencionDTO.getIdProcesoAtencion());
        procesosAtencion.setDetalle(procesosAtencionDTO.getDetalle());
        procesosAtencion.setProcesos(procesos);

        ProcesosAtencion procesosAtencionGuardado = procesosAtencionRepo.save(procesosAtencion);

        return modelMapper.map(procesosAtencionGuardado, ProcesosAtencionDTO.class);

    }

    public ProcesosAtencionDTO update(ProcesosAtencionDTO detalleProcesosAtencionDTO, Long id) {

        Procesos procesos = procesosRepository.findById(detalleProcesosAtencionDTO.getIdProceso())
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        ProcesosAtencion procesosAtencionoExistente = procesosAtencionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Proceso Atencion no encontrado"));

        ProcesosAtencionDTO procesosAtencionDTO = modelMapper.map(procesosAtencionoExistente, ProcesosAtencionDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleProcesosAtencionDTO.getIdProcesoAtencion()!= null) {
            procesosAtencionoExistente.setIdProcesoAtencion(detalleProcesosAtencionDTO.getIdProcesoAtencion());
        }
        if (detalleProcesosAtencionDTO.getDetalle() != null) {
            procesosAtencionoExistente.setDetalle(detalleProcesosAtencionDTO.getDetalle());
        }
        if (detalleProcesosAtencionDTO.getDetalle() != null) {
            procesosAtencionoExistente.setDetalle(detalleProcesosAtencionDTO.getDetalle());
        }

        // Guardar en la base de datos
        ProcesosAtencion procesosAtencionActualizado = procesosAtencionRepo.save(procesosAtencionoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(procesosAtencionActualizado, ProcesosAtencionDTO.class);
    }

    public Optional<ProcesosAtencionDTO> findById(Long idProcesoAtencion) {
        return procesosAtencionRepo.findById(idProcesoAtencion)
                .map(procesosAtencion -> modelMapper.map(procesosAtencion, ProcesosAtencionDTO.class)); // Convertir a DTO
    }

    public List<ProcesosAtencionDTO> findAll() {
        return procesosAtencionRepo.findAll()
                .stream()
                .map(procesosAtencion -> modelMapper.map(procesosAtencion, ProcesosAtencionDTO.class))
                .collect(Collectors.toList());
    }

    public List<ProcesosAtencionDTO> findByCuadro(Long idCuadro) {
        return procesosAtencionRepo.findByCuadroTurnoId(idCuadro)
                .stream()
                .map(procesosAtencion -> modelMapper.map(procesosAtencion, ProcesosAtencionDTO.class))
                .collect(Collectors.toList());
    }

}
