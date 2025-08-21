package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.TipoTurnoDTO;
import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.TipoTurno;
import com.turnos.enfermeria.repository.ContratoRepository;
import com.turnos.enfermeria.repository.TipoTurnoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TipoTurnoService {
    private final TipoTurnoRepository tipoTurnoRepository;
    private final ContratoRepository contratoRepository;
    private final ModelMapper modelMapper;

    public TipoTurnoDTO create(TipoTurnoDTO tipoTurnoDTO) {
        Contrato contrato = contratoRepository.findById(tipoTurnoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado."));

        TipoTurno tipoTurno = modelMapper.map(tipoTurnoDTO, TipoTurno.class);
        tipoTurno.setEspecialidad(tipoTurnoDTO.getEspecialidad());
        tipoTurno.setPresencial(tipoTurnoDTO.isPresencial());
        tipoTurno.setDisponibilidad(tipoTurnoDTO.isDisponibilidad());
        tipoTurno.setContratos(contrato);

        TipoTurno tipoTurnoGuardado = tipoTurnoRepository.save(tipoTurno);

        return modelMapper.map(tipoTurnoGuardado, TipoTurnoDTO.class);

    }


    public TipoTurnoDTO update(TipoTurnoDTO detalleTipoTurnoDTO, Long id) {
        Contrato contrato = contratoRepository.findById(detalleTipoTurnoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado."));

        TipoTurno tipoTurnoExistente = tipoTurnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo turno no encontrado"));

        TipoTurnoDTO tipoTurnoDTO = modelMapper.map(tipoTurnoExistente, TipoTurnoDTO.class);

        if (detalleTipoTurnoDTO.getEspecialidad() != null) {
            tipoTurnoExistente.setEspecialidad(detalleTipoTurnoDTO.getEspecialidad());
        }
        if (detalleTipoTurnoDTO.isPresencial()) {
            tipoTurnoExistente.setPresencial(detalleTipoTurnoDTO.isPresencial());
        }
        if (detalleTipoTurnoDTO.isDisponibilidad()) {
            tipoTurnoExistente.setDisponibilidad(detalleTipoTurnoDTO.isDisponibilidad());
        }
        if (detalleTipoTurnoDTO.getIdContrato() != null) {
            tipoTurnoExistente.setContratos(contrato);
        }

        // Guardar en la base de datos
        TipoTurno tipoTurnoActualizado = tipoTurnoRepository.save(tipoTurnoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(tipoTurnoActualizado, TipoTurnoDTO.class);
    }

    public Optional<TipoTurnoDTO> findById(Long idTipoTurno) {
        return tipoTurnoRepository.findById(idTipoTurno)
                .map(tipoTurno -> modelMapper.map(tipoTurno, TipoTurnoDTO.class)); // Convertir a DTO
    }

    public List<TipoTurnoDTO> findAll() {
        return tipoTurnoRepository.findAll()
                .stream()
                .map(tipoTurno -> modelMapper.map(tipoTurno, TipoTurnoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idTipoTurno) {
        // Buscar el bloque en la base de datos
        TipoTurno tipoTurnoEliminar = tipoTurnoRepository.findById(idTipoTurno)
                .orElseThrow(() -> new EntityNotFoundException("tipo turno no encontrado"));

        // Convertir la entidad a DTO
        TipoTurnoDTO tipoTurnoDTO = modelMapper.map(tipoTurnoEliminar, TipoTurnoDTO.class);

        tipoTurnoRepository.deleteById(idTipoTurno);
    }
}
