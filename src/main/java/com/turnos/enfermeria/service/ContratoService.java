package com.turnos.enfermeria.service;

import com.turnos.enfermeria.mapper.TitulosContratoMapper;
import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.ContratoRepository;
import com.turnos.enfermeria.repository.TitulosFormacionAcademicaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final ModelMapper modelMapper;
    private final TitulosFormacionAcademicaRepository titulosFormacionAcademicaRepository;
    private final TitulosContratoMapper titulosContratoMapper;

    /**
     * Guarda un contrato completo con todas sus relaciones
     */
    public Contrato guardarContratoCompleto(ContratoTotalDTO contratoDTO) {
        try {
            // 1. Crear y configurar la entidad Contrato principal
            Contrato contrato = new Contrato();
            contrato.setNumContrato(contratoDTO.getNumContrato());
            contrato.setSupervisor(contratoDTO.getSupervisor());
            contrato.setApoyoSupervision(contratoDTO.getApoyoSupervision());
            contrato.setObjeto(contratoDTO.getObjeto());
            contrato.setContratista(contratoDTO.getContratista());
            contrato.setFechaInicio(contratoDTO.getFechaInicio());
            contrato.setFechaTerminacion(contratoDTO.getFechaTerminacion());
            contrato.setAnio(contratoDTO.getAnio());
            contrato.setObservaciones(contratoDTO.getObservaciones());

            // 2. Establecer relaciones Many-to-Many

            // Títulos de Formación Académica
            if (contratoDTO.getTitulosIds() != null && !contratoDTO.getTitulosIds().isEmpty()) {
                List<TitulosFormacionAcademica> titulos = titulosFormacionAcademicaRepository.findAllById(contratoDTO.getTitulosIds());
                contrato.setTitulosFormacionAcademica(titulos);
            }

            // 3. Guardar el contrato (esto también guardará las relaciones Many-to-Many)
            Contrato contratoGuardado = contratoRepository.save(contrato);

            // 4. Guardar relaciones en tablas intermedias adicionales
            guardarTiposAtencion(contratoGuardado.getIdContrato(), contratoDTO.getTiposAtencionIds());
            guardarTiposTurno(contratoGuardado.getIdContrato(), contratoDTO.getTiposTurnoIds());
            guardarProcesos(contratoGuardado.getIdContrato(), contratoDTO.getProcesosIds());

            return contratoGuardado;

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el contrato: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un contrato existente
     */
    public Contrato actualizarContratoCompleto(Long contratoId, ContratoTotalDTO contratoDTO) {
        try {
            Contrato contratoExistente = contratoRepository.findById(contratoId)
                    .orElseThrow(() -> new RuntimeException("Contrato no found with id: " + contratoId));

            // Actualizar campos básicos
            contratoExistente.setNumContrato(contratoDTO.getNumContrato());
            contratoExistente.setSupervisor(contratoDTO.getSupervisor());
            contratoExistente.setApoyoSupervision(contratoDTO.getApoyoSupervision());
            contratoExistente.setObjeto(contratoDTO.getObjeto());
            contratoExistente.setContratista(contratoDTO.getContratista());
            contratoExistente.setFechaInicio(contratoDTO.getFechaInicio());
            contratoExistente.setFechaTerminacion(contratoDTO.getFechaTerminacion());
            contratoExistente.setAnio(contratoDTO.getAnio());
            contratoExistente.setObservaciones(contratoDTO.getObservaciones());

            // Actualizar títulos
            if (contratoDTO.getTitulosIds() != null) {
                List<TitulosFormacionAcademica> titulos = titulosFormacionAcademicaRepository.findAllById(contratoDTO.getTitulosIds());
                contratoExistente.setTitulosFormacionAcademica(titulos);
            }

            // Guardar cambios
            Contrato contratoActualizado = contratoRepository.save(contratoExistente);

            // Eliminar relaciones existentes y crear nuevas
            eliminarRelacionesExistentes(contratoId);
            guardarTiposAtencion(contratoId, contratoDTO.getTiposAtencionIds());
            guardarTiposTurno(contratoId, contratoDTO.getTiposTurnoIds());
            guardarProcesos(contratoId, contratoDTO.getProcesosIds());

            return contratoActualizado;

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el contrato: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina las relaciones existentes de un contrato
     */
    private void eliminarRelacionesExistentes(Long contratoId) {
        contratoRepository.deleteContratoTiposAtencion(contratoId);
        contratoRepository.deleteContratoTiposTurno(contratoId);
        contratoRepository.deleteContratoProcesos(contratoId);
    }

    /**
     * Obtiene un contrato completo con todas sus relaciones
     */
    public ContratoTotalDTO obtenerContratoCompleto(Long contratoId) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new RuntimeException("Contrato not found with id: " + contratoId));

        ContratoTotalDTO dto = new ContratoTotalDTO();
        dto.setNumContrato(contrato.getNumContrato());
        dto.setSupervisor(contrato.getSupervisor());
        dto.setApoyoSupervision(contrato.getApoyoSupervision());
        dto.setObjeto(contrato.getObjeto());
        dto.setContratista(contrato.getContratista());
        dto.setFechaInicio(contrato.getFechaInicio());
        dto.setFechaTerminacion(contrato.getFechaTerminacion());
        dto.setAnio(contrato.getAnio());
        dto.setObservaciones(contrato.getObservaciones());

        // Obtener IDs de relaciones
        if (contrato.getTitulosFormacionAcademica() != null) {
            dto.setTitulosIds(contrato.getTitulosFormacionAcademica().stream()
                    .map(TitulosFormacionAcademica::getIdTipoFormacionAcademica)
                    .collect(Collectors.toList()));
        }

        dto.setTiposAtencionIds(contratoRepository.findTiposAtencionByContratoId(contratoId));
        dto.setTiposTurnoIds(contratoRepository.findTiposTurnoByContratoId(contratoId));
        dto.setProcesosIds(contratoRepository.findProcesosByContratoId(contratoId));

        return dto;
    }

    /**
     * Obtiene todos los contratos
     */
    public List<Contrato> obtenerTodosLosContratos() {
        return contratoRepository.findAll();
    }

    /**
     * Elimina un contrato y todas sus relaciones
     */
    public void eliminarContrato(Long contratoId) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new RuntimeException("Contrato not found with id: " + contratoId));

        // Eliminar relaciones primero
        eliminarRelacionesExistentes(contratoId);

        // Eliminar el contrato
        contratoRepository.delete(contrato);
    }

    /**
     * Verifica si existe un número de contrato
     */
    public boolean existeNumeroContrato(String numContrato) {
        return contratoRepository.existsByNumContrato(numContrato);
    }

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

    public ContratoDTO ***REMOVED***(ContratoDTO detalleContratoDTO, Long id) {
        Contrato contratoExistente = contratoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));


        ContratoDTO contratoDTO = modelMapper.map(contratoExistente, ContratoDTO.class);

        // Actualizar los campos si no son nulos
        validarNulos(detalleContratoDTO, contratoExistente);

        // Guardar en la base de datos
        Contrato contratoActualizado = contratoRepository.save(contratoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(contratoActualizado, ContratoDTO.class);
    }

    private static void validarNulos(ContratoDTO detalleContratoDTO, Contrato contratoExistente) {
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
        List<TitulosFormacionAcademica> titulosFormacionAcademica = contratoRepository.findTitulosByIdContrato(idContrato).orElse(new ArrayList<>());
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



    /**
     * Guarda la relación entre contrato y tipos de atención
     */
    private void guardarTiposAtencion(Long contratoId, List<Long> tiposAtencionIds) {
        if (tiposAtencionIds != null && !tiposAtencionIds.isEmpty()) {
            for (Long tipoAtencionId : tiposAtencionIds) {
                // Usar query nativa para insertar en la tabla intermedia
                contratoRepository.insertContratoTipoAtencion(contratoId, tipoAtencionId);
            }
        }
    }

    /**
     * Guarda la relación entre contrato y tipos de turno
     */
    private void guardarTiposTurno(Long contratoId, List<Long> tiposTurnoIds) {
        if (tiposTurnoIds != null && !tiposTurnoIds.isEmpty()) {
            for (Long tipoTurnoId : tiposTurnoIds) {
                // Usar query nativa para insertar en la tabla intermedia
                contratoRepository.insertContratoTipoTurno(contratoId, tipoTurnoId);
            }
        }
    }

    /**
     * Guarda la relación entre contrato y procesos
     */
    private void guardarProcesos(Long contratoId, List<Long> procesosIds) {
        if (procesosIds != null && !procesosIds.isEmpty()) {
            for (Long procesoId : procesosIds) {
                // Usar query nativa para insertar en la tabla intermedia
                contratoRepository.insertContratoProceso(contratoId, procesoId);
            }
        }
    }
}
