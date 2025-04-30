package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.CambiosTurnoDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.CambiosTurnoRepository;
import com.turnos.enfermeria.repository.CuadroTurnoRepository;
import com.turnos.enfermeria.repository.TurnosRepository;
import com.turnos.enfermeria.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CambiosTurnoService {

    private final CambiosTurnoRepository cambiosTurnoRepo;
    private final ModelMapper modelMapper;
    private  final TurnosRepository turnosRepository;
    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final UsuarioRepository usuarioRepository;

    public CambiosTurnoDTO create(CambiosTurnoDTO cambiosTurnoDTO) {
        CambiosTurno cambiosTurno = modelMapper.map(cambiosTurnoDTO,CambiosTurno.class);

        // Obtener el objeto Turnos a partir del ID
        Turnos turno = turnosRepository.findById(cambiosTurnoDTO.getIdTurno())
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        // Obtener el objeto CuadroTurno a partir del ID
        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(cambiosTurnoDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("CuadroTurno no encontrado"));
        // Obtener el objeto usuario a partir del ID
        Usuario usuario = usuarioRepository.findById(cambiosTurnoDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        cambiosTurno.setTurno(turno);
        cambiosTurno.setCuadroTurno(cuadroTurno);
        cambiosTurno.setUsuario(usuario);
        cambiosTurno.setFechaCambio(cambiosTurnoDTO.getFechaCambio());
        cambiosTurno.setFechaInicio(cambiosTurnoDTO.getFechaInicio());
        cambiosTurno.setFechaFin(cambiosTurnoDTO.getFechaFin());
        cambiosTurno.setTotalHoras(cambiosTurnoDTO.getTotalHoras());
        cambiosTurno.setTipoTurno(cambiosTurnoDTO.getTipoTurno());
        cambiosTurno.setEstadoTurno(cambiosTurnoDTO.getEstadoTurno());
        cambiosTurno.setJornada(cambiosTurnoDTO.getJornada());
        cambiosTurno.setVersion(cambiosTurnoDTO.getVersion());
        cambiosTurno.setComentarios(cambiosTurnoDTO.getComentarios());

        CambiosTurno cambiosTurnoGuardado = cambiosTurnoRepo.save(cambiosTurno);
        return modelMapper.map(cambiosTurnoGuardado, CambiosTurnoDTO.class);
    }

    public CambiosTurnoDTO update(CambiosTurnoDTO detalleCambiosTurnoDTO, Long id) {

        CambiosTurno cambiosTurnoExistente = cambiosTurnoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("CambiosTurno no encontrado"));

        CambiosTurnoDTO cambiosTurnoDTO = modelMapper.map(cambiosTurnoExistente, CambiosTurnoDTO.class);

        // Obtener el objeto Turnos a partir del ID
        Turnos turno = turnosRepository.findById(cambiosTurnoDTO.getIdTurno())
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        // Obtener el objeto CuadroTurno a partir del ID
        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(cambiosTurnoDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("CuadroTurno no encontrado"));
        // Obtener el objeto usuario a partir del ID
        Usuario usuario = usuarioRepository.findById(cambiosTurnoDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar los campos si no son nulos
        if (detalleCambiosTurnoDTO.getIdCambio()!= null) {
            cambiosTurnoExistente.setIdCambio(detalleCambiosTurnoDTO.getIdCambio());
        }
        if (detalleCambiosTurnoDTO.getIdCambio()!= null) {
            cambiosTurnoExistente.setIdCambio(detalleCambiosTurnoDTO.getIdCambio());
        }
        if (detalleCambiosTurnoDTO.getIdTurno()!= null) {
            cambiosTurnoExistente.setTurno(turno);
        }
        if (detalleCambiosTurnoDTO.getIdCuadroTurno()!= null) {
            cambiosTurnoExistente.setCuadroTurno(cuadroTurno);
        }
        if (detalleCambiosTurnoDTO.getIdUsuario()!= null) {
            cambiosTurnoExistente.setUsuario(usuario);
        }
        if (detalleCambiosTurnoDTO.getFechaCambio()!= null) {
            cambiosTurnoExistente.setFechaCambio(detalleCambiosTurnoDTO.getFechaCambio());
        }
        if (detalleCambiosTurnoDTO.getFechaInicio()!= null) {
            cambiosTurnoExistente.setFechaInicio(detalleCambiosTurnoDTO.getFechaInicio());
        }
        if (detalleCambiosTurnoDTO.getFechaFin()!= null) {
            cambiosTurnoExistente.setFechaFin(detalleCambiosTurnoDTO.getFechaFin());
        }
        if (detalleCambiosTurnoDTO.getTotalHoras()!= null) {
            cambiosTurnoExistente.setTotalHoras(detalleCambiosTurnoDTO.getTotalHoras());
        }
        if (detalleCambiosTurnoDTO.getTipoTurno()!= null) {
            cambiosTurnoExistente.setTipoTurno(detalleCambiosTurnoDTO.getTipoTurno());
        }
        if (detalleCambiosTurnoDTO.getEstadoTurno()!= null) {
            cambiosTurnoExistente.setEstadoTurno(detalleCambiosTurnoDTO.getEstadoTurno());
        }
        if (detalleCambiosTurnoDTO.getJornada()!= null) {
            cambiosTurnoExistente.setJornada(detalleCambiosTurnoDTO.getJornada());
        }
        if (detalleCambiosTurnoDTO.getVersion()!= null) {
            cambiosTurnoExistente.setVersion(detalleCambiosTurnoDTO.getVersion());
        }
        if (detalleCambiosTurnoDTO.getComentarios()!= null) {
            cambiosTurnoExistente.setComentarios(detalleCambiosTurnoDTO.getComentarios());
        }


        // Guardar en la base de datos
        CambiosTurno cambiosTurnoActualizado = cambiosTurnoRepo.save(cambiosTurnoExistente);

        return modelMapper.map(cambiosTurnoActualizado, CambiosTurnoDTO.class);
    }

    public Optional<CambiosTurnoDTO> findById(Long idCambio) {
        return cambiosTurnoRepo.findById(idCambio)
                .map(cambiosTurno -> modelMapper.map(cambiosTurno, CambiosTurnoDTO.class)); // Convertir a DTO
    }

    public List<CambiosTurnoDTO> findAll() {

        return cambiosTurnoRepo.findAll()
                .stream()
                .map(cambiosTurno -> modelMapper.map(cambiosTurno, CambiosTurnoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long idCambio) {

        // Buscar el bloque en la base de datos
        CambiosTurno cambiosTurnoEliminar = cambiosTurnoRepo.findById(idCambio)
                .orElseThrow(() -> new EntityNotFoundException("cambio no encontrado"));

        // Convertir la entidad a DTO
        CambiosTurnoDTO cambiosTurnoDTO = modelMapper.map(cambiosTurnoEliminar, CambiosTurnoDTO.class);

        cambiosTurnoRepo.deleteById(idCambio);
    }

    public List<CambiosTurnoDTO> obtenerCambiosPorTurno(Long idTurno) {
        List<CambiosTurno> cambios = cambiosTurnoRepo.findByTurno_IdTurno(idTurno);
        return cambios.stream()
                .map(cambio -> {
                    CambiosTurnoDTO dto = modelMapper.map(cambio, CambiosTurnoDTO.class);
                    dto.setIdTurno(cambio.getTurno().getIdTurno());
                    dto.setIdUsuario(cambio.getUsuario().getIdPersona());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
