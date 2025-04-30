package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.TitulosFormacionAcademicaDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.TipoFormacionAcademica;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import com.turnos.enfermeria.repository.TipoFormacionAcademicaRepository;
import com.turnos.enfermeria.repository.TitulosFormacionAcademicaRepository;
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
public class TitulosFormacionAcademicaService {

    private final TitulosFormacionAcademicaRepository titulosFormacionAcademicaRepo;
    private final TipoFormacionAcademicaRepository tipoFormacionAcademicaRepository;
    private final ModelMapper modelMapper;

    public TitulosFormacionAcademicaDTO create(TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO) {
        TipoFormacionAcademica tipoFormacionAcademica = tipoFormacionAcademicaRepository.findById(titulosFormacionAcademicaDTO.getIdTipoFormacionAcademica())
                .orElseThrow(() -> new RuntimeException("Tipo Formacion no encontrada."));

        TitulosFormacionAcademica titulosFormacionAcademica = modelMapper.map(titulosFormacionAcademicaDTO, TitulosFormacionAcademica.class);

        titulosFormacionAcademica.setTitulo(titulosFormacionAcademicaDTO.getTitulo());
        titulosFormacionAcademica.setTipoFormacionAcademica(tipoFormacionAcademica);

        TitulosFormacionAcademica titulosFormacionAcademicaGuardado = titulosFormacionAcademicaRepo.save(titulosFormacionAcademica);

        return modelMapper.map(titulosFormacionAcademicaGuardado, TitulosFormacionAcademicaDTO.class);

    }


    public TitulosFormacionAcademicaDTO update(TitulosFormacionAcademicaDTO detalleTitulosFormacionAcademicaDTO, Long id) {
        TipoFormacionAcademica tipoFormacionAcademica = tipoFormacionAcademicaRepository.findById(detalleTitulosFormacionAcademicaDTO.getIdTipoFormacionAcademica())
                .orElseThrow(() -> new RuntimeException("Tipo Formacion no encontrada."));
        TitulosFormacionAcademica titulosFormacionAcademicaExistente = titulosFormacionAcademicaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Titulo no encontrado"));

        TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO = modelMapper.map(titulosFormacionAcademicaExistente, TitulosFormacionAcademicaDTO.class);

        if (detalleTitulosFormacionAcademicaDTO.getTitulo() != null) {
            titulosFormacionAcademicaExistente.setTitulo(detalleTitulosFormacionAcademicaDTO.getTitulo());
        }
        if (detalleTitulosFormacionAcademicaDTO.getIdTipoFormacionAcademica() != null) {
            titulosFormacionAcademicaExistente.setTipoFormacionAcademica(tipoFormacionAcademica);
        }

        // Guardar en la base de datos
        TitulosFormacionAcademica titulosFormacionAcademicaActualizado = titulosFormacionAcademicaRepo.save(titulosFormacionAcademicaExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(titulosFormacionAcademicaActualizado, TitulosFormacionAcademicaDTO.class);
    }

    public Optional<TitulosFormacionAcademicaDTO> findById(Long idTitulo) {
        return titulosFormacionAcademicaRepo.findById(idTitulo)
                .map(titulosFormacionAcademica -> modelMapper.map(titulosFormacionAcademica, TitulosFormacionAcademicaDTO.class)); // Convertir a DTO
    }

    public List<TitulosFormacionAcademicaDTO> findAll() {
        return titulosFormacionAcademicaRepo.findAll()
                .stream()
                .map(titulosFormacionAcademica -> modelMapper.map(titulosFormacionAcademica, TitulosFormacionAcademicaDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idTitulo) {
        // Buscar el bloque en la base de datos
        TitulosFormacionAcademica titulosFormacionAcademicaEliminar = titulosFormacionAcademicaRepo.findById(idTitulo)
                .orElseThrow(() -> new EntityNotFoundException("titulo no encontrado"));

        // Convertir la entidad a DTO
        TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO = modelMapper.map(titulosFormacionAcademicaEliminar, TitulosFormacionAcademicaDTO.class);

        titulosFormacionAcademicaRepo.deleteById(idTitulo);
    }
}
