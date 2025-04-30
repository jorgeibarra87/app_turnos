package com.turnos.enfermeria.service;

import com.turnos.enfermeria.mapper.TitulosContratoMapper;
import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.ContratoRepository;
import com.turnos.enfermeria.repository.TitulosFormacionAcademicaRepository;
import com.turnos.enfermeria.repository.UsuarioRepository;
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
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final TitulosFormacionAcademicaRepository titulosFormacionAcademicaRepository;
    private final TitulosContratoMapper titulosContratoMapper;

    public ContratoDTO create(ContratoDTO contratoDTO) {


        Contrato contrato = modelMapper.map(contratoDTO, Contrato.class);
        contrato.setIdContrato(contratoDTO.getIdContrato());
        contrato.setNumContrato(contratoDTO.getNumContrato());
        contrato.setSupervisor(contratoDTO.getSupervisor());
        contrato.setApoyoSupervision(contratoDTO.getApoyoSupervision());
        contrato.setObjeto(contratoDTO.getObjeto());
        contrato.setContratista(contratoDTO.getContratista());
        contrato.setFechaInicio(contratoDTO.getFechaInicio());
        contrato.setFechaTerminacion(contratoDTO.getFechaTerminacion());
        contrato.setObservaciones(contratoDTO.getObservaciones());

        Contrato contratoGuardado = contratoRepository.save(contrato);

        return modelMapper.map(contratoGuardado, ContratoDTO.class);

    }

    public ContratoDTO update(ContratoDTO detalleContratoDTO, Long id) {
        Contrato contratoExistente = contratoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));


        ContratoDTO contratoDTO = modelMapper.map(contratoExistente, ContratoDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleContratoDTO.getIdContrato()!= null) {
            contratoExistente.setIdContrato(detalleContratoDTO.getIdContrato());
        }
        if (detalleContratoDTO.getNumContrato()!= null) {
            contratoExistente.setNumContrato(detalleContratoDTO.getNumContrato());
        }
        if (detalleContratoDTO.getSupervisor()!= null) {
            contratoExistente.setSupervisor(detalleContratoDTO.getSupervisor());
        }
        if (detalleContratoDTO.getApoyoSupervision()!= null) {
            contratoExistente.setApoyoSupervision(detalleContratoDTO.getApoyoSupervision());
        }
        if (detalleContratoDTO.getObjeto()!= null) {
            contratoExistente.setObjeto(detalleContratoDTO.getObjeto());
        }
        if (detalleContratoDTO.getContratista()!= null) {
            contratoExistente.setContratista(detalleContratoDTO.getContratista());
        }
        if (detalleContratoDTO.getFechaInicio()!= null) {
            contratoExistente.setFechaInicio(detalleContratoDTO.getFechaInicio());
        }
        if (detalleContratoDTO.getFechaTerminacion()!= null) {
            contratoExistente.setFechaTerminacion(detalleContratoDTO.getFechaTerminacion());
        }
        if (detalleContratoDTO.getAnio()!= null) {
            contratoExistente.setAnio(detalleContratoDTO.getAnio());
        }
        if (detalleContratoDTO.getObservaciones()!= null) {
            contratoExistente.setObservaciones(detalleContratoDTO.getObservaciones());
        }

        // Guardar en la base de datos
        Contrato contratoActualizado = contratoRepository.save(contratoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(contratoActualizado, ContratoDTO.class);
    }

    public Optional<ContratoDTO> findById(Long idContrato) {
        return contratoRepository.findById(idContrato)
                .map(contrato -> modelMapper.map(contrato, ContratoDTO.class)); // Convertir a DTO
    }

    public List<ContratoDTO> findAll() {
        return contratoRepository.findAll()
                .stream()
                .map(contrato -> modelMapper.map(contrato, ContratoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long idContrato) {
        // Buscar el bloque en la base de datos
        Contrato contratoEliminar = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));

        // Convertir la entidad a DTO
        ContratoDTO contratoDTO = modelMapper.map(contratoEliminar, ContratoDTO.class);

        contratoRepository.deleteById(idContrato);
    }

    public List<TitulosFormacionAcademicaDTO> agregarTituloAContrato(Long idContrato, Long idTitulo) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        TitulosFormacionAcademica titulo = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("Titulo no encontrado"));

        contrato.getTitulosFormacionAcademica().add(titulo);
        Contrato contratoActualizado = contratoRepository.save(contrato);

        return titulosContratoMapper.toDTOList(contratoActualizado.getTitulosFormacionAcademica());
    }

    public List<TitulosFormacionAcademicaDTO> obtenerTitulosPorContrato(Long idContrato) {
        List<TitulosFormacionAcademica> titulosFormacionAcademica = contratoRepository.findTitulosByIdContrato(idContrato);
        return titulosFormacionAcademica.stream()
                .map(titulosContratoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TitulosFormacionAcademicaDTO actualizarTitulosDeContrato(Long idTitulo, List<Long> nuevosTitulosIds) {
        TitulosFormacionAcademica titulosFormacionAcademica = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("Titulo no encontrado"));

        // Contratos actuales
        List<Contrato> todos = contratoRepository.findAll();
        for (Contrato contrato : todos) {
            contrato.getTitulosFormacionAcademica().remove(titulosFormacionAcademica);
        }

        contratoRepository.saveAll(todos); // guardar la limpieza primero

        // Nuevos contratos a asociar
        List<Contrato> nuevosContratos = contratoRepository.findAllById(nuevosTitulosIds);
        for (Contrato contrato : nuevosContratos) {
            contrato.getTitulosFormacionAcademica().add(titulosFormacionAcademica);
        }

        contratoRepository.saveAll(nuevosContratos); // guardar las nuevas asociaciones

        return titulosContratoMapper.toDTO(titulosFormacionAcademica);
    }

    public void eliminarTituloDeContrato(Long idContrato, Long idTitulo) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new RuntimeException("Contratono encontrado"));
        TitulosFormacionAcademica titulosFormacionAcademica = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("titulo no encontrado"));

        contrato.getTitulosFormacionAcademica().remove(titulosFormacionAcademica);
        contratoRepository.save(contrato);
    }
}
