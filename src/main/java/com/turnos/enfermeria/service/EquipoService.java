package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.EquipoDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.CuadroTurno;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.repository.CuadroTurnoRepository;
import com.turnos.enfermeria.repository.EquipoRepository;
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
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final ModelMapper modelMapper;
    private final CuadroTurnoRepository cuadroTurnoRepository;

    public EquipoDTO create(EquipoDTO equipoDTO) {

        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(equipoDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("Cuadro de turno no encontrado."));

        Equipo equipo = modelMapper.map(equipoDTO, Equipo.class);
        equipo.setIdEquipo(equipoDTO.getIdEquipo());
        equipo.setNombre(equipoDTO.getNombre());
        equipo.setCuadroTurno(cuadroTurno);

        Equipo equipoGuardado = equipoRepository.save(equipo);

        return modelMapper.map(equipoGuardado, EquipoDTO.class);

    }

    public EquipoDTO update(EquipoDTO detalleEquipoDTO, Long id) {
        Equipo equipoExistente = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(detalleEquipoDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("Cuadro de turno no encontrado."));

        EquipoDTO equipoDTO = modelMapper.map(equipoExistente, EquipoDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleEquipoDTO.getIdEquipo()!= null) {
            equipoExistente.setIdEquipo(detalleEquipoDTO.getIdEquipo());
        }
        if (detalleEquipoDTO.getNombre() != null) {
            equipoExistente.setNombre(detalleEquipoDTO.getNombre());
        }
        if (detalleEquipoDTO.getIdCuadroTurno() != null) {
            equipoExistente.setCuadroTurno(cuadroTurno);
        }

        // Guardar en la base de datos
        Equipo equipoActualizado = equipoRepository.save(equipoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(equipoActualizado, EquipoDTO.class);
    }

    public Optional<EquipoDTO> findById(Long idEquipo) {
        return equipoRepository.findById(idEquipo)
                .map(equipo -> modelMapper.map(equipo, EquipoDTO.class)); // Convertir a DTO
    }

    public List<EquipoDTO> findAll() {
        return equipoRepository.findAll()
                .stream()
                .map(equipo -> modelMapper.map(equipo, EquipoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long idEquipo) {
        // Buscar el bloque en la base de datos
        Equipo equipoEliminar = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));

        // Convertir la entidad a DTO
        EquipoDTO equipoDTO = modelMapper.map(equipoEliminar, EquipoDTO.class);

        equipoRepository.deleteById(idEquipo);
    }
}
