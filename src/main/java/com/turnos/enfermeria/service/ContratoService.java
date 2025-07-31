package com.turnos.enfermeria.service;

import com.turnos.enfermeria.mapper.ProcesosContratoMapper;
import com.turnos.enfermeria.mapper.TitulosContratoMapper;
import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
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
    private final ProcesosContratoMapper procesosContratoMapper;
    private final TipoAtencionRepository tipoAtencionRepository;
    private final TipoTurnoRepository tipoTurnoRepository;
    //private final ProcesosContratoRepository procesoContratoRepository;
    private final TitulosFormacionAcademicaRepository titulosRepository;
    private final ProcesosRepository procesosRepository;

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

            // 2. Establecer relación Many-to-Many con títulos
            if (contratoDTO.getTitulosIds() != null && !contratoDTO.getTitulosIds().isEmpty()) {
                List<TitulosFormacionAcademica> titulos = titulosFormacionAcademicaRepository.findAllById(contratoDTO.getTitulosIds());
                contrato.setTitulosFormacionAcademica(titulos);
            }

            // 2. Establecer relación Many-to-Many con títulos
            if (contratoDTO.getProcesosIds() != null && !contratoDTO.getProcesosIds().isEmpty()) {
                List<Procesos> procesos = procesosRepository.findAllById(contratoDTO.getProcesosIds());
                contrato.setProcesos(procesos);
            }

            // 3. Guardar el contrato primero para obtener su ID
            Contrato contratoGuardado = contratoRepository.save(contrato);

            // 4. Establecer relaciones Many-to-One desde las entidades relacionadas
            establecerRelacionesTiposAtencion(contratoGuardado, contratoDTO.getTiposAtencionIds());
            establecerRelacionesTiposTurno(contratoGuardado, contratoDTO.getTiposTurnoIds());
            //establecerRelacionesProcesos(contratoGuardado, contratoDTO.getProcesosIds());

            return contratoGuardado;

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el contrato: " + e.getMessage(), e);
        }
    }

    /**
     * Establece las relaciones con tipos de atención
     */
    private void establecerRelacionesTiposAtencion(Contrato contrato, List<Long> tiposAtencionIds) {
        if (tiposAtencionIds != null && !tiposAtencionIds.isEmpty()) {
            List<TipoAtencion> tiposAtencion = tipoAtencionRepository.findAllById(tiposAtencionIds);
            for (TipoAtencion tipoAtencion : tiposAtencion) {
                tipoAtencion.setContratos(contrato);
                tipoAtencionRepository.save(tipoAtencion);
            }
        }
    }

    /**
     * Establece las relaciones con tipos de turno
     */
    private void establecerRelacionesTiposTurno(Contrato contrato, List<Long> tiposTurnoIds) {
        if (tiposTurnoIds != null && !tiposTurnoIds.isEmpty()) {
            List<TipoTurno> tiposTurno = tipoTurnoRepository.findAllById(tiposTurnoIds);
            for (TipoTurno tipoTurno : tiposTurno) {
                tipoTurno.setContratos(contrato);
                tipoTurnoRepository.save(tipoTurno);
            }
        }
    }

    /**
     * Establece las relaciones con procesos
     */
//    private void establecerRelacionesProcesos(Contrato contrato, List<Long> procesosIds) {
//        if (procesosIds != null && !procesosIds.isEmpty()) {
//            List<ProcesosContrato> procesos = procesoContratoRepository.findAllById(procesosIds);
//            for (ProcesosContrato proceso : procesos) {
//                proceso.setContrato(contrato);
//                procesoContratoRepository.save(proceso);
//            }
//        }
//    }

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

            // Actualizar títulos (Many-to-Many)
            if (contratoDTO.getTitulosIds() != null) {
                List<TitulosFormacionAcademica> titulos = titulosRepository.findAllById(contratoDTO.getTitulosIds());
                contratoExistente.setTitulosFormacionAcademica(titulos);
            }

            // Actualizar procesos (Many-to-Many)
            if (contratoDTO.getProcesosIds() != null) {
                List<Procesos> procesos = procesosRepository.findAllById(contratoDTO.getProcesosIds());
                contratoExistente.setProcesos(procesos);
            }

            // Guardar cambios del contrato
            Contrato contratoActualizado = contratoRepository.save(contratoExistente);

            // Limpiar relaciones existentes y establecer nuevas
            limpiarRelacionesExistentes(contratoId);
            establecerRelacionesTiposAtencion(contratoActualizado, contratoDTO.getTiposAtencionIds());
            establecerRelacionesTiposTurno(contratoActualizado, contratoDTO.getTiposTurnoIds());
            //establecerRelacionesProcesos(contratoActualizado, contratoDTO.getProcesosIds());

            return contratoActualizado;

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el contrato: " + e.getMessage(), e);
        }
    }

    /**
     * Limpia las relaciones existentes de un contrato
     */
    private void limpiarRelacionesExistentes(Long contratoId) {
        // Limpiar tipos de atención
        List<TipoAtencion> tiposAtencionExistentes = tipoAtencionRepository.findByContratosIdContrato(contratoId);
        for (TipoAtencion tipoAtencion : tiposAtencionExistentes) {
            tipoAtencion.setContratos(null);
            tipoAtencionRepository.save(tipoAtencion);
        }

        // Limpiar tipos de turno
        List<TipoTurno> tiposTurnoExistentes = tipoTurnoRepository.findByContratosIdContrato(contratoId);
        for (TipoTurno tipoTurno : tiposTurnoExistentes) {
            tipoTurno.setContratos(null);
            tipoTurnoRepository.save(tipoTurno);
        }

        // Limpiar procesos
//        List<ProcesosContrato> procesosExistentes = procesoContratoRepository.findByContratoIdContrato(contratoId);
//        for (ProcesosContrato proceso : procesosExistentes) {
//            proceso.setContrato(null);
//            procesoContratoRepository.save(proceso);
//        }
    }

    /**
     * Obtiene un contrato completo con todas sus relaciones
     */
    public ContratoTotalDTO obtenerContratoCompleto(Long contratoId) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new RuntimeException("Contrato not found with id: " + contratoId));

        ContratoTotalDTO dto = new ContratoTotalDTO();
        dto.setIdContrato(contrato.getIdContrato());
        dto.setNumContrato(contrato.getNumContrato());
        dto.setSupervisor(contrato.getSupervisor());
        dto.setApoyoSupervision(contrato.getApoyoSupervision());
        dto.setObjeto(contrato.getObjeto());
        dto.setContratista(contrato.getContratista());
        dto.setFechaInicio(contrato.getFechaInicio());
        dto.setFechaTerminacion(contrato.getFechaTerminacion());
        dto.setAnio(contrato.getAnio());
        dto.setObservaciones(contrato.getObservaciones());

        // Obtener IDs de títulos (Many-to-Many)
        if (contrato.getTitulosFormacionAcademica() != null) {
            dto.setTitulosIds(contrato.getTitulosFormacionAcademica().stream()
                    .map(TitulosFormacionAcademica::getIdTipoFormacionAcademica)
                    .collect(Collectors.toList()));
        }

        // Obtener IDs de procesos (Many-to-Many)
        if (contrato.getProcesos() != null) {
            dto.setProcesosIds(contrato.getProcesos().stream()
                    .map(Procesos::getIdProceso)
                    .collect(Collectors.toList()));
        }

        // Obtener IDs de relaciones Many-to-One
        List<TipoAtencion> tiposAtencion = tipoAtencionRepository.findByContratosIdContrato(contratoId);
        dto.setTiposAtencionIds(tiposAtencion.stream()
                .map(TipoAtencion::getIdTipoAtencion)
                .collect(Collectors.toList()));

        List<TipoTurno> tiposTurno = tipoTurnoRepository.findByContratosIdContrato(contratoId);
        dto.setTiposTurnoIds(tiposTurno.stream()
                .map(TipoTurno::getIdTipoTurno)
                .collect(Collectors.toList()));

//        List<ProcesosContrato> procesos = procesoContratoRepository.findByContratoIdContrato(contratoId);
//        dto.setProcesosIds(procesos.stream()
//                .map(ProcesosContrato::getIdProcesoContrato)
//                .collect(Collectors.toList()));

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

        // Limpiar relaciones primero
        limpiarRelacionesExistentes(contratoId);

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

    public ContratoDTO update(ContratoDTO detalleContratoDTO, Long id) {
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

    public List<ProcesosDTO> agregarProcesoAContrato(Long idContrato, Long idProceso) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        Procesos procesos = procesosRepository.findById(idProceso)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));

        contrato.getProcesos().add(procesos);
        Contrato contratoActualizado = contratoRepository.save(contrato);

        return procesosContratoMapper.toDTOList(contratoActualizado.getProcesos());
    }

    public List<TitulosFormacionAcademicaDTO> obtenerTitulosPorContrato(Long idContrato) {
        List<TitulosFormacionAcademica> titulosFormacionAcademica = contratoRepository.findTitulosByIdContrato(idContrato).orElse(new ArrayList<>());
        return titulosFormacionAcademica.stream()
                .map(titulosContratoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProcesosDTO> obtenerProcesosPorContrato(Long idContrato) {
        List<Procesos> procesos = contratoRepository.findProcesosByIdContrato(idContrato).orElse(new ArrayList<>());
        return procesos.stream()
                .map(procesosContratoMapper::toDTO)
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

    public ProcesosDTO actualizarProcesosDeContrato(Long idProceso, List<Long> nuevosProcesosIds) {
        Procesos procesos = procesosRepository.findById(idProceso)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));

        // Contratos actuales
        List<Contrato> todos = contratoRepository.findAll();
        for (Contrato contrato : todos) {
            contrato.getProcesos().remove(procesos);
        }

        contratoRepository.saveAll(todos); // guardar la limpieza primero

        // Nuevos contratos a asociar
        List<Contrato> nuevosContratos = contratoRepository.findAllById(nuevosProcesosIds);
        for (Contrato contrato : nuevosContratos) {
            contrato.getProcesos().add(procesos);
        }

        contratoRepository.saveAll(nuevosContratos); // guardar las nuevas asociaciones

        return procesosContratoMapper.toDTO(procesos);
    }

    public void eliminarTituloDeContrato(Long idContrato, Long idTitulo) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new RuntimeException("Contratono encontrado"));
        TitulosFormacionAcademica titulosFormacionAcademica = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("titulo no encontrado"));

        contrato.getTitulosFormacionAcademica().remove(titulosFormacionAcademica);
        contratoRepository.save(contrato);
    }

    public void eliminarProcesoDeContrato(Long idContrato, Long idProceso) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new RuntimeException("Contratono encontrado"));
        Procesos procesos = procesosRepository.findById(idProceso)
                .orElseThrow(() -> new RuntimeException("proceso no encontrado"));

        contrato.getProcesos().remove(procesos);
        contratoRepository.save(contrato);
    }

}
