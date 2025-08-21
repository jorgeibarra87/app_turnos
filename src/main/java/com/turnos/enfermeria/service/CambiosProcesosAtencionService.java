package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.CambiosProcesosAtencionDTO;
import com.turnos.enfermeria.model.entity.CambiosProcesosAtencion;
import com.turnos.enfermeria.repository.CambiosProcesosAtencionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CambiosProcesosAtencionService {

    private final CambiosProcesosAtencionRepository cambiosProcesosAtencionRepository;
    private final ModelMapper modelMapper;

    public CambiosProcesosAtencionDTO create(CambiosProcesosAtencionDTO cambiosProcesosAtencionDTO) {
        CambiosProcesosAtencion cambiosProcesosAtencion = modelMapper.map(cambiosProcesosAtencionDTO, CambiosProcesosAtencion.class);

        cambiosProcesosAtencion.setCambioCuadroTurno(cambiosProcesosAtencionDTO.getCambiosCuadroTurno());
        cambiosProcesosAtencion.setProcesos(cambiosProcesosAtencionDTO.getProcesos());
        cambiosProcesosAtencion.setCuadroTurno(cambiosProcesosAtencionDTO.getCuadroTurno());


        CambiosProcesosAtencion cambiosProcesosAtencionGuardado = cambiosProcesosAtencionRepository.save(cambiosProcesosAtencion);

        return modelMapper.map(cambiosProcesosAtencionGuardado, CambiosProcesosAtencionDTO.class);

    }


    public CambiosProcesosAtencionDTO update(CambiosProcesosAtencionDTO detalleCambiosProcesosAtencionDTO, Long id) {
        CambiosProcesosAtencion cambiosProcesosAtencionExistente = cambiosProcesosAtencionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CambiosProcesosAtencion no encontrado"));

        CambiosProcesosAtencionDTO cambiosProcesosAtencionDTO = modelMapper.map(cambiosProcesosAtencionExistente, CambiosProcesosAtencionDTO.class);

        // Actualizar los campos si no son nulos

        if (detalleCambiosProcesosAtencionDTO.getCambiosCuadroTurno() != null) {
            cambiosProcesosAtencionExistente.setCambioCuadroTurno(detalleCambiosProcesosAtencionDTO.getCambiosCuadroTurno());
        }
        if (detalleCambiosProcesosAtencionDTO.getDetalle() != null) {
            cambiosProcesosAtencionExistente.setDetalle(detalleCambiosProcesosAtencionDTO.getDetalle());
        }
        if (detalleCambiosProcesosAtencionDTO.getProcesos() != null) {
            cambiosProcesosAtencionExistente.setProcesos(detalleCambiosProcesosAtencionDTO.getProcesos());
        }
        if (detalleCambiosProcesosAtencionDTO.getCuadroTurno() != null) {
            cambiosProcesosAtencionExistente.setCuadroTurno(detalleCambiosProcesosAtencionDTO.getCuadroTurno());
        }

        // Guardar en la base de datos
        CambiosProcesosAtencion cambiosProcesosAtencionActualizado = cambiosProcesosAtencionRepository.save(cambiosProcesosAtencionExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(cambiosProcesosAtencionActualizado, CambiosProcesosAtencionDTO.class);
    }

    public Optional<CambiosProcesosAtencionDTO> findById(Long idCambioProcesoAtencion) {
        return cambiosProcesosAtencionRepository.findById(idCambioProcesoAtencion)
                .map(cambiosProcesosAtencion -> modelMapper.map(cambiosProcesosAtencion, CambiosProcesosAtencionDTO.class)); // Convertir a DTO
    }

    public List<CambiosProcesosAtencionDTO> findAll() {
        return cambiosProcesosAtencionRepository.findAll()
                .stream()
                .map(cambiosProcesosAtencion -> modelMapper.map(cambiosProcesosAtencion, CambiosProcesosAtencionDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idCambioProcesoAtencion) {
        CambiosProcesosAtencion cambiosProcesosAtencionEliminar = cambiosProcesosAtencionRepository.findById(idCambioProcesoAtencion)
                .orElseThrow(() -> new EntityNotFoundException("cambio proceso atencion no encontrado"));

        // Convertir la entidad a DTO
        CambiosProcesosAtencionDTO cambiosProcesosAtencionDTO = modelMapper.map(cambiosProcesosAtencionEliminar, CambiosProcesosAtencionDTO.class);

        cambiosProcesosAtencionRepository.deleteById(idCambioProcesoAtencion);
    }
}
