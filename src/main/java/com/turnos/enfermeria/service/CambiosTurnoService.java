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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CambiosTurnoService {

    private final CambiosTurnoRepository cambiosTurnoRepo;
    private final ModelMapper modelMapper;
    private final TurnosRepository turnosRepository;
    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final UsuarioRepository usuarioRepository;

    public CambiosTurnoDTO create(CambiosTurnoDTO cambiosTurnoDTO) {
        // Buscar entidades relacionadas usando métodos auxiliares
        Turnos turno = buscarTurno(cambiosTurnoDTO.getIdTurno());
        CuadroTurno cuadroTurno = buscarCuadroTurno(cambiosTurnoDTO.getIdCuadroTurno());
        Usuario usuario = buscarUsuario(cambiosTurnoDTO.getIdUsuario());

        // Convertir DTO a Entidad
        CambiosTurno cambiosTurno = modelMapper.map(cambiosTurnoDTO, CambiosTurno.class);

        // Configurar el cambio de turno
        configurarCambioTurno(cambiosTurno, cambiosTurnoDTO, turno, cuadroTurno, usuario);

        // Guardar en la base de datos
        CambiosTurno cambioGuardado = cambiosTurnoRepo.save(cambiosTurno);

        return modelMapper.map(cambioGuardado, CambiosTurnoDTO.class);
    }

    public CambiosTurnoDTO update(CambiosTurnoDTO detalleCambiosTurnoDTO, Long id) {
        // Buscar cambio existente
        CambiosTurno cambioExistente = buscarCambioTurno(id);

        // Buscar entidades relacionadas usando métodos auxiliares
        Turnos turno = buscarTurno(detalleCambiosTurnoDTO.getIdTurno());
        CuadroTurno cuadroTurno = buscarCuadroTurno(detalleCambiosTurnoDTO.getIdCuadroTurno());
        Usuario usuario = buscarUsuario(detalleCambiosTurnoDTO.getIdUsuario());

        // Actualizar campos del cambio existente
        actualizarCambioExistente(cambioExistente, detalleCambiosTurnoDTO, turno, cuadroTurno, usuario);

        // Guardar cambios
        CambiosTurno cambioActualizado = cambiosTurnoRepo.save(cambioExistente);

        return modelMapper.map(cambioActualizado, CambiosTurnoDTO.class);
    }

    public Optional<CambiosTurnoDTO> findById(Long idCambio) {
        return cambiosTurnoRepo.findById(idCambio)
                .map(cambiosTurno -> modelMapper.map(cambiosTurno, CambiosTurnoDTO.class));
    }

    public List<CambiosTurnoDTO> findAll() {
        return cambiosTurnoRepo.findAll()
                .stream()
                .map(cambiosTurno -> modelMapper.map(cambiosTurno, CambiosTurnoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idCambio) {
        // Buscar cambio a eliminar
        Optional<CambiosTurno> optionalCambio = cambiosTurnoRepo.findById(idCambio);

        if (optionalCambio.isPresent()) {
            cambiosTurnoRepo.deleteById(idCambio);
        } else {
            throw new EntityNotFoundException("Cambio de turno no encontrado");
        }
    }

    public List<CambiosTurnoDTO> obtenerCambiosPorTurno(Long idTurno) {
        List<CambiosTurno> cambios = cambiosTurnoRepo.findByTurno_IdTurno(idTurno);

        return cambios.stream()
                .map(this::mapearCambioConRelaciones)
                .collect(Collectors.toList());
    }

    // ===== MÉTODOS AUXILIARES DE BÚSQUEDA =====

    private Turnos buscarTurno(Long id) {
        return id != null ? turnosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado")) : null;
    }

    private CuadroTurno buscarCuadroTurno(Long id) {
        return id != null ? cuadroTurnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CuadroTurno no encontrado")) : null;
    }

    private Usuario buscarUsuario(Long id) {
        return id != null ? usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado")) : null;
    }

    private CambiosTurno buscarCambioTurno(Long id) {
        return cambiosTurnoRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CambiosTurno no encontrado"));
    }

    // ===== MÉTODOS AUXILIARES DE CONFIGURACIÓN =====

    private void configurarCambioTurno(CambiosTurno cambio, CambiosTurnoDTO dto,
                                       Turnos turno, CuadroTurno cuadroTurno, Usuario usuario) {
        cambio.setTurno(turno);
        cambio.setCuadroTurno(cuadroTurno);
        cambio.setUsuario(usuario);
        cambio.setFechaCambio(dto.getFechaCambio());
        cambio.setFechaInicio(dto.getFechaInicio());
        cambio.setFechaFin(dto.getFechaFin());
        cambio.setTotalHoras(dto.getTotalHoras());
        cambio.setTipoTurno(dto.getTipoTurno());
        cambio.setEstadoTurno(dto.getEstadoTurno());
        cambio.setJornada(dto.getJornada());
        cambio.setVersion(dto.getVersion());
        cambio.setComentarios(dto.getComentarios());
    }

    private void actualizarCambioExistente(CambiosTurno cambioExistente, CambiosTurnoDTO dto,
                                           Turnos turno, CuadroTurno cuadroTurno, Usuario usuario) {
        // Actualizar relaciones si los IDs están presentes
        if (dto.getIdTurno() != null) cambioExistente.setTurno(turno);
        if (dto.getIdCuadroTurno() != null) cambioExistente.setCuadroTurno(cuadroTurno);
        if (dto.getIdUsuario() != null) cambioExistente.setUsuario(usuario);

        // Actualizar campos básicos si no son nulos
        if (dto.getIdCambio() != null) cambioExistente.setIdCambio(dto.getIdCambio());
        if (dto.getFechaCambio() != null) cambioExistente.setFechaCambio(dto.getFechaCambio());
        if (dto.getFechaInicio() != null) cambioExistente.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) cambioExistente.setFechaFin(dto.getFechaFin());
        if (dto.getTotalHoras() != null) cambioExistente.setTotalHoras(dto.getTotalHoras());
        if (dto.getTipoTurno() != null) cambioExistente.setTipoTurno(dto.getTipoTurno());
        if (dto.getEstadoTurno() != null) cambioExistente.setEstadoTurno(dto.getEstadoTurno());
        if (dto.getJornada() != null) cambioExistente.setJornada(dto.getJornada());
        if (dto.getVersion() != null) cambioExistente.setVersion(dto.getVersion());
        if (dto.getComentarios() != null) cambioExistente.setComentarios(dto.getComentarios());
    }

    private CambiosTurnoDTO mapearCambioConRelaciones(CambiosTurno cambio) {
        CambiosTurnoDTO dto = modelMapper.map(cambio, CambiosTurnoDTO.class);

        // Asignar IDs de relaciones manualmente para asegurar consistencia
        if (cambio.getTurno() != null) {
            dto.setIdTurno(cambio.getTurno().getIdTurno());
        }
        if (cambio.getUsuario() != null) {
            dto.setIdUsuario(cambio.getUsuario().getIdPersona());
        }
        if (cambio.getCuadroTurno() != null) {
            dto.setIdCuadroTurno(cambio.getCuadroTurno().getIdCuadroTurno());
        }

        return dto;
    }
}
