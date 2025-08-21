package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.TipoAtencionDTO;
import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.TipoAtencion;
import com.turnos.enfermeria.repository.ContratoRepository;
import com.turnos.enfermeria.repository.TipoAtencionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TipoAtencionService {

    private final TipoAtencionRepository tipoAtencionRepository;
    private final ContratoRepository contratoRepository;
    private final ModelMapper modelMapper;

    public TipoAtencionDTO create(TipoAtencionDTO tipoAtencionDTO) {

        Contrato contrato = contratoRepository.findById(tipoAtencionDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado."));

        TipoAtencion tipoAtencion = modelMapper.map(tipoAtencionDTO, TipoAtencion.class);
        tipoAtencion.setNombreTipoAtencion(tipoAtencionDTO.getNombreTipoAtencion());
        tipoAtencion.setDescripcionTipoAtencion(tipoAtencionDTO.getDescripcionTipoAtencion());
        tipoAtencion.setEstadoTipoAtencion(tipoAtencionDTO.getEstadoTipoAtencion());
        tipoAtencion.setContratos(contrato);

        TipoAtencion tipoAtencionGuardado = tipoAtencionRepository.save(tipoAtencion);

        return modelMapper.map(tipoAtencionGuardado, TipoAtencionDTO.class);

    }


    public TipoAtencionDTO update(TipoAtencionDTO detalleTipoAtencionDTO, Long id) {

        Contrato contrato = contratoRepository.findById(detalleTipoAtencionDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado."));

        TipoAtencion tipoAtencionExistente = tipoAtencionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo Atencion no encontrada"));

        TipoAtencionDTO tipoAtencionDTO = modelMapper.map(tipoAtencionExistente, TipoAtencionDTO.class);

        if (detalleTipoAtencionDTO.getNombreTipoAtencion() != null) {
            tipoAtencionExistente.setNombreTipoAtencion(detalleTipoAtencionDTO.getNombreTipoAtencion());
        }
        if (detalleTipoAtencionDTO.getDescripcionTipoAtencion() != null) {
            tipoAtencionExistente.setDescripcionTipoAtencion(detalleTipoAtencionDTO.getDescripcionTipoAtencion());
        }
        if (detalleTipoAtencionDTO.getEstadoTipoAtencion() != null) {
            tipoAtencionExistente.setEstadoTipoAtencion(detalleTipoAtencionDTO.getEstadoTipoAtencion());
        }
        if (detalleTipoAtencionDTO.getIdContrato() != null) {
            tipoAtencionExistente.setContratos(contrato);
        }

        // Guardar en la base de datos
        TipoAtencion tipoAtencionActualizado = tipoAtencionRepository.save(tipoAtencionExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(tipoAtencionActualizado, TipoAtencionDTO.class);
    }

    public Optional<TipoAtencionDTO> findById(Long idTipoAtencion) {
        return tipoAtencionRepository.findById(idTipoAtencion)
                .map(tipoAtencion -> modelMapper.map(tipoAtencion, TipoAtencionDTO.class)); // Convertir a DTO
    }

    public List<TipoAtencionDTO> findAll() {
        return tipoAtencionRepository.findAll()
                .stream()
                .map(tipoAtencion -> modelMapper.map(tipoAtencion, TipoAtencionDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idTipoAtencion) {
        // Buscar el bloque en la base de datos
        TipoAtencion tipoAtencionEliminar = tipoAtencionRepository.findById(idTipoAtencion)
                .orElseThrow(() -> new EntityNotFoundException("Tipo Atencion no encontrada"));

        // Convertir la entidad a DTO
        TipoAtencionDTO tipoAtencionDTO = modelMapper.map(tipoAtencionEliminar, TipoAtencionDTO.class);

        tipoAtencionRepository.deleteById(idTipoAtencion);
    }
}
