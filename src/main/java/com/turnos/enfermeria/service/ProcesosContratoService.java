package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.ProcesosAtencionDTO;
import com.turnos.enfermeria.model.dto.ProcesosContratoDTO;
import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.Procesos;
import com.turnos.enfermeria.model.entity.ProcesosContrato;
import com.turnos.enfermeria.repository.ContratoRepository;
import com.turnos.enfermeria.repository.ProcesosContratoRepository;
import com.turnos.enfermeria.repository.ProcesosRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class ProcesosContratoService {

    private final ProcesosContratoRepository procesosContratoRepository;
    private final ProcesosRepository procesosRepository;
    private final ContratoRepository contratoRepository;
    private final ModelMapper modelMapper;

    public ProcesosContratoDTO create(ProcesosContratoDTO procesosContratoDTO) {
        Procesos proceso = procesosRepository.findById(procesosContratoDTO.getIdProceso())
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        Contrato contrato = contratoRepository.findById(procesosContratoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado."));

        ProcesosContrato procesosContrato = modelMapper.map(procesosContratoDTO, ProcesosContrato.class);
        procesosContrato.setDetalle(procesosContratoDTO.getDetalle());
        procesosContrato.setProcesos(proceso);
        procesosContrato.setContrato(contrato);

        ProcesosContrato procesosContratoGuardado = procesosContratoRepository.save(procesosContrato);

        return modelMapper.map(procesosContratoGuardado, ProcesosContratoDTO.class);

    }


    public ProcesosContratoDTO update(ProcesosContratoDTO detalleProcesosContratoDTO, Long id) {

        Procesos proceso = procesosRepository.findById(detalleProcesosContratoDTO.getIdProceso())
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        Contrato contrato = contratoRepository.findById(detalleProcesosContratoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado."));

        ProcesosContrato procesosContratoExistente = procesosContratoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proceso contrato no encontrado"));

        ProcesosContratoDTO procesosContratoDTO = modelMapper.map(procesosContratoExistente, ProcesosContratoDTO.class);

        if (detalleProcesosContratoDTO.getDetalle() != null) {
            procesosContratoExistente.setDetalle(detalleProcesosContratoDTO.getDetalle());
        }
        if (detalleProcesosContratoDTO.getIdProceso() != null) {
            procesosContratoExistente.setProcesos(proceso);
        }
        if (detalleProcesosContratoDTO.getIdContrato()!= null) {
            procesosContratoExistente.setContrato(contrato);
        }


        // Guardar en la base de datos
        ProcesosContrato procesosContratoActualizado = procesosContratoRepository.save(procesosContratoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(procesosContratoActualizado, ProcesosContratoDTO.class);
    }

    public Optional<ProcesosContratoDTO> findById(Long idProcesoContrato) {
        return procesosContratoRepository.findById(idProcesoContrato)
                .map(procesosContrato -> modelMapper.map(procesosContrato, ProcesosContratoDTO.class)); // Convertir a DTO
    }

    public List<ProcesosContratoDTO> findAll() {
        return procesosContratoRepository.findAll()
                .stream()
                .map(procesosContrato -> modelMapper.map(procesosContrato, ProcesosContratoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idProcesoContrato) {
        // Buscar el bloque en la base de datos
        ProcesosContrato procesosContratoEliminar = procesosContratoRepository.findById(idProcesoContrato)
                .orElseThrow(() -> new EntityNotFoundException("Proceso Contrato no encontrado"));

        // Convertir la entidad a DTO
        ProcesosContratoDTO procesosContratoDTO = modelMapper.map(procesosContratoEliminar, ProcesosContratoDTO.class);

        procesosContratoRepository.deleteById(idProcesoContrato);
    }

    public List<ProcesosContratoDTO> findByContrato(Long idContrato) {
        return procesosContratoRepository.findByContratoIdContrato(idContrato)
                .stream()
                .map(procesosContrato -> modelMapper.map(procesosContrato, ProcesosContratoDTO.class))
                .collect(Collectors.toList());
    }
}
