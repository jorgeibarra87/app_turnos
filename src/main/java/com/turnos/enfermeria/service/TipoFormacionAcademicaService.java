package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.TipoFormacionAcademicaDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.TipoFormacionAcademica;
import com.turnos.enfermeria.repository.TipoFormacionAcademicaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TipoFormacionAcademicaService {

    private final TipoFormacionAcademicaRepository tipoFormacionAcademicaRepo;
    private final ModelMapper modelMapper;

    public TipoFormacionAcademicaDTO create(TipoFormacionAcademicaDTO tipoFormacionAcademicaDTO) {
        TipoFormacionAcademica tipoFormacionAcademica = modelMapper.map(tipoFormacionAcademicaDTO, TipoFormacionAcademica.class);
        tipoFormacionAcademica.setTipo(tipoFormacionAcademicaDTO.getTipo());

        TipoFormacionAcademica tipoFormacionAcademicaGuardado = tipoFormacionAcademicaRepo.save(tipoFormacionAcademica);

        return modelMapper.map(tipoFormacionAcademicaGuardado, TipoFormacionAcademicaDTO.class);

    }


    public TipoFormacionAcademicaDTO update(TipoFormacionAcademicaDTO detalleTipoFormacionAcademicaDTO, Long id) {
        TipoFormacionAcademica tipoFormacionAcademicaExistente = tipoFormacionAcademicaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("BloqueServicio no encontrado"));

        TipoFormacionAcademicaDTO tipoFormacionAcademicaDTO = modelMapper.map(tipoFormacionAcademicaExistente, TipoFormacionAcademicaDTO.class);

        if (detalleTipoFormacionAcademicaDTO.getTipo() != null) {
            tipoFormacionAcademicaExistente.setTipo(detalleTipoFormacionAcademicaDTO.getTipo());
        }

        // Guardar en la base de datos
        TipoFormacionAcademica tipoFormacionAcademicaActualizado = tipoFormacionAcademicaRepo.save(tipoFormacionAcademicaExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(tipoFormacionAcademicaActualizado, TipoFormacionAcademicaDTO.class);
    }

    public Optional<TipoFormacionAcademicaDTO> findById(Long idTipoFormacionAcademica) {
        return tipoFormacionAcademicaRepo.findById(idTipoFormacionAcademica)
                .map(tipoFormacionAcademica -> modelMapper.map(tipoFormacionAcademica, TipoFormacionAcademicaDTO.class)); // Convertir a DTO
    }

    public List<TipoFormacionAcademicaDTO> findAll() {
        return tipoFormacionAcademicaRepo.findAll()
                .stream()
                .map(tipoFormacionAcademica -> modelMapper.map(tipoFormacionAcademica, TipoFormacionAcademicaDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idTipoFormacionAcademica) {
        // Buscar el bloque en la base de datos
        TipoFormacionAcademica tipoFormacionAcademicaEliminar = tipoFormacionAcademicaRepo.findById(idTipoFormacionAcademica)
                .orElseThrow(() -> new EntityNotFoundException("tipo formacion no encontrada"));

        // Convertir la entidad a DTO
        TipoFormacionAcademicaDTO tipoFormacionAcademicaDTO = modelMapper.map(tipoFormacionAcademicaEliminar, TipoFormacionAcademicaDTO.class);

        tipoFormacionAcademicaRepo.deleteById(idTipoFormacionAcademica);
    }
}
