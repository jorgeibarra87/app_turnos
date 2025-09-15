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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
}
