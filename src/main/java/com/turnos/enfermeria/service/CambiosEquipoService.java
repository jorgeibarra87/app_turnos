package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.CambiosEquipoDTO;
import com.turnos.enfermeria.model.dto.CambiosPersonaEquipoDTO;
import com.turnos.enfermeria.model.entity.CambiosEquipo;
import com.turnos.enfermeria.model.entity.CambiosPersonaEquipo;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.repository.CambiosEquipoRepository;
import com.turnos.enfermeria.repository.CambiosPersonaEquipoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CambiosEquipoService {

    private final CambiosEquipoRepository cambiosEquipoRepository;
    private final CambiosPersonaEquipoRepository cambiosPersonaEquipoRepository;
    private final ModelMapper modelMapper;

    /**
     * Registra cambios en equipos
     */
    public void registrarCambioEquipo(Equipo equipoAnterior, Equipo equipoNuevo, String tipoCambio) {
        CambiosEquipo cambio = new CambiosEquipo();
        cambio.setEquipo(equipoNuevo != null ? equipoNuevo : equipoAnterior);
        cambio.setFechaCambio(LocalDateTime.now());
        cambio.setTipoCambio(tipoCambio);

        if (equipoAnterior != null) {
            cambio.setNombreAnterior(equipoAnterior.getNombre());
            cambio.setEstadoAnterior(equipoAnterior.getEstado());
        }

        if (equipoNuevo != null) {
            cambio.setNombreNuevo(equipoNuevo.getNombre());
            cambio.setEstadoNuevo(equipoNuevo.getEstado());
        }

        cambiosEquipoRepository.save(cambio);
    }

    /**
     * Registra cambios en asignación de personas a equipos
     */
    public void registrarCambioPersonaEquipo(Persona persona, Equipo equipoAnterior,
                                             Equipo equipoNuevo, String tipoCambio) {
        CambiosPersonaEquipo cambio = new CambiosPersonaEquipo();
        cambio.setPersona(persona);
        cambio.setEquipo(equipoNuevo != null ? equipoNuevo : equipoAnterior);
        cambio.setFechaCambio(LocalDateTime.now());
        cambio.setTipoCambio(tipoCambio);
        cambio.setEquipoAnterior(equipoAnterior);
        cambio.setEquipoNuevo(equipoNuevo);

        cambiosPersonaEquipoRepository.save(cambio);
    }

    /**
     * Obtener historial de cambios de un equipo
     */
    public List<CambiosEquipoDTO> obtenerHistorialEquipo(Long idEquipo) {
        List<CambiosEquipo> historial = cambiosEquipoRepository.findByEquipo_IdEquipoOrderByFechaCambioDesc(idEquipo);

        return historial.stream()
                .map(cambio -> modelMapper.map(cambio, CambiosEquipoDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Obtener historial de cambios de asignaciones de una persona
     */
    public List<CambiosPersonaEquipoDTO> obtenerHistorialPersona(Long idPersona) {
        List<CambiosPersonaEquipo> historial = cambiosPersonaEquipoRepository
                .findByPersona_IdPersonaOrderByFechaCambioDesc(idPersona);

        return historial.stream()
                .map(cambio -> modelMapper.map(cambio, CambiosPersonaEquipoDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Obtener historial completo relacionado a un cuadro (equipos y personas)
     */
    public Map<String, Object> obtenerHistorialPorCuadro(Long cuadroId) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            // Obtener historial de cambios de equipos (puedes filtrar por cuadro si tienes esa relación)
            List<CambiosEquipo> cambiosEquipos = cambiosEquipoRepository.findAllByOrderByFechaCambioDesc();
            List<CambiosEquipoDTO> historialEquipos = cambiosEquipos.stream()
                    .map(cambio -> {
                        CambiosEquipoDTO dto = modelMapper.map(cambio, CambiosEquipoDTO.class);
                        dto.setIdEquipo(cambio.getEquipo().getIdEquipo());
                        if (cambio.getEquipo() != null) {
                            dto.setNombreEquipo(cambio.getEquipo().getNombre());
                            dto.setEstadoActual(cambio.getEquipo().getEstado());
                        }
                        return dto;
                    })
                    .limit(20) // Últimos 20 cambios
                    .collect(Collectors.toList());

            // Obtener historial de cambios de personas
            List<CambiosPersonaEquipo> cambiosPersonas = cambiosPersonaEquipoRepository.findAllByOrderByFechaCambioDesc();
            List<CambiosPersonaEquipoDTO> historialPersonas = cambiosPersonas.stream()
                    .map(cambio -> {
                        CambiosPersonaEquipoDTO dto = modelMapper.map(cambio, CambiosPersonaEquipoDTO.class);

                        if (cambio.getPersona() != null) {
                            dto.setIdPersona(cambio.getPersona().getIdPersona());
                            dto.setNombrePersona(cambio.getPersona().getNombreCompleto());
                            dto.setDocumentoPersona(cambio.getPersona().getDocumento());
                        }

                        if (cambio.getEquipo() != null) {
                            dto.setNombreEquipo(cambio.getEquipo().getNombre());
                        }

                        if (cambio.getEquipoAnterior() != null) {
                            dto.setEquipoAnteriorId(cambio.getEquipoAnterior().getIdEquipo());
                            dto.setNombreEquipoAnterior(cambio.getEquipoAnterior().getNombre());
                        }

                        if (cambio.getEquipoNuevo() != null) {
                            dto.setEquipoNuevoId(cambio.getEquipoNuevo().getIdEquipo());
                            dto.setNombreEquipoNuevo(cambio.getEquipoNuevo().getNombre());
                        }

                        return dto;
                    })
                    .limit(30) // Últimos 30 cambios
                    .collect(Collectors.toList());

            resultado.put("historialEquipos", historialEquipos);
            resultado.put("historialPersonas", historialPersonas);

        } catch (Exception e) {
            log.error("Error obteniendo historial por cuadro {}: {}", cuadroId, e.getMessage());
            resultado.put("historialEquipos", new ArrayList<>());
            resultado.put("historialPersonas", new ArrayList<>());
            resultado.put("error", e.getMessage());
        }

        return resultado;
    }
}
