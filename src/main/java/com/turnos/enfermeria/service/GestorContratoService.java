package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.GestorContratoDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.GestorContrato;
import com.turnos.enfermeria.model.entity.Usuario;
import com.turnos.enfermeria.repository.ContratoRepository;
import com.turnos.enfermeria.repository.GestorContratoRepository;
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
public class GestorContratoService {

    private final GestorContratoRepository gestorContratoRepository;
    private final ModelMapper modelMapper;
    private final UsuarioRepository usuarioRepository;
    private final ContratoRepository contratoRepository;

    public GestorContratoDTO create(GestorContratoDTO gestorContratoDTO) {

        Usuario usuario = usuarioRepository.findById(gestorContratoDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("El usuario es obligatorio."));

        Contrato contrato = contratoRepository.findById(gestorContratoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("El contrato es obligatorio."));

        GestorContrato gestorContrato = modelMapper.map(gestorContratoDTO, GestorContrato.class);
        gestorContrato.setIdGestorContrato(gestorContratoDTO.getIdGestorContrato());
        gestorContrato.setUsuario(usuario);
        gestorContrato.setContrato(contrato);

        GestorContrato gestorContratoGuardado = gestorContratoRepository.save(gestorContrato);

        return modelMapper.map(gestorContratoGuardado, GestorContratoDTO.class);

    }

    public GestorContratoDTO update(GestorContratoDTO detalleGestorContratoDTO, Long id) {
        GestorContrato gestorContratoExistente = gestorContratoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("gestor no encontrado"));

        Usuario usuario = usuarioRepository.findById(detalleGestorContratoDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("El usuario es obligatorio."));

        Contrato contrato = contratoRepository.findById(detalleGestorContratoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("El contrato es obligatorio."));

        GestorContratoDTO gestorContratoDTO = modelMapper.map(gestorContratoExistente, GestorContratoDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleGestorContratoDTO.getIdGestorContrato()!= null) {
            gestorContratoExistente.setIdGestorContrato(detalleGestorContratoDTO.getIdGestorContrato());
        }
        if (detalleGestorContratoDTO.getIdPersona() != null) {
            gestorContratoExistente.setUsuario(usuario);
        }
        if (detalleGestorContratoDTO.getIdContrato() != null) {
            gestorContratoExistente.setContrato(contrato);
        }

        // Guardar en la base de datos
        GestorContrato gestorContratoActualizado = gestorContratoRepository.save(gestorContratoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(gestorContratoActualizado, GestorContratoDTO.class);
    }

    public Optional<GestorContratoDTO> findById(Long idGestorContrato) {
        return gestorContratoRepository.findById(idGestorContrato)
                .map(gestorContrato -> modelMapper.map(gestorContrato, GestorContratoDTO.class)); // Convertir a DTO
    }

    public List<GestorContratoDTO> findAll() {
        return gestorContratoRepository.findAll()
                .stream()
                .map(gestorContrato -> modelMapper.map(gestorContrato, GestorContratoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idGestorContrato) {
        // Buscar el bloque en la base de datos
        GestorContrato gestorContratoEliminar = gestorContratoRepository.findById(idGestorContrato)
                .orElseThrow(() -> new EntityNotFoundException("gestor no encontrado"));

        // Convertir la entidad a DTO
        GestorContratoDTO gestorContratoDTO = modelMapper.map(gestorContratoEliminar, GestorContratoDTO.class);

        gestorContratoRepository.deleteById(idGestorContrato);
    }

}
