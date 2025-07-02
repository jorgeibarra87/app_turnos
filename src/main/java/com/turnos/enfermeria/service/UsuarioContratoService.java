package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.UsuarioContratoDTO;
import com.turnos.enfermeria.model.dto.UsuarioContratoTotalDTO;
import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.Roles;
import com.turnos.enfermeria.model.entity.UsuarioContrato;
import com.turnos.enfermeria.model.entity.Usuario;
import com.turnos.enfermeria.repository.ContratoRepository;
import com.turnos.enfermeria.repository.RolesRepository;
import com.turnos.enfermeria.repository.UsuarioContratoRepository;
import com.turnos.enfermeria.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UsuarioContratoService {

    private final UsuarioContratoRepository usuarioContratoRepository;
    private final ModelMapper modelMapper;
    private final UsuarioRepository usuarioRepository;
    private final ContratoRepository contratoRepository;
    private final RolesRepository rolesRepository;

    public UsuarioContratoDTO create(UsuarioContratoDTO usuarioContratoDTO) {

        Usuario usuario = usuarioRepository.findById(usuarioContratoDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("El usuario es obligatorio."));

        Contrato contrato = contratoRepository.findById(usuarioContratoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("El contrato es obligatorio."));

        Roles roles = rolesRepository.findById(usuarioContratoDTO.getIdRol())
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));

        UsuarioContrato usuarioContrato = modelMapper.map(usuarioContratoDTO, UsuarioContrato.class);
        usuarioContrato.setIdUsuarioContrato(usuarioContratoDTO.getIdUsuarioContrato());
        usuarioContrato.setUsuario(usuario);
        usuarioContrato.setContrato(contrato);
        usuarioContrato.setRoles(roles);

        UsuarioContrato usuarioContratoGuardado = usuarioContratoRepository.save(usuarioContrato);
        return modelMapper.map(usuarioContratoGuardado, UsuarioContratoDTO.class);
    }

    public UsuarioContratoDTO ***REMOVED***(UsuarioContratoDTO detalleUsuarioContratoDTO, Long id) {
        UsuarioContrato usuarioContratoExistente = usuarioContratoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("usuario no encontrado"));

        Usuario usuario = usuarioRepository.findById(detalleUsuarioContratoDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("El usuario es obligatorio."));

        Contrato contrato = contratoRepository.findById(detalleUsuarioContratoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("El contrato es obligatorio."));

        UsuarioContratoDTO usuarioContratoDTO = modelMapper.map(usuarioContratoExistente, UsuarioContratoDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleUsuarioContratoDTO.getIdUsuarioContrato()!= null) {
            usuarioContratoExistente.setIdUsuarioContrato(detalleUsuarioContratoDTO.getIdUsuarioContrato());
        }
        if (detalleUsuarioContratoDTO.getIdPersona() != null) {
            usuarioContratoExistente.setUsuario(usuario);
        }
        if (detalleUsuarioContratoDTO.getIdContrato() != null) {
            usuarioContratoExistente.setContrato(contrato);
        }

        // Guardar en la base de datos
        UsuarioContrato usuarioContratoActualizado = usuarioContratoRepository.save(usuarioContratoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(usuarioContratoActualizado, UsuarioContratoDTO.class);
    }

    public Optional<UsuarioContratoDTO> findById(Long idUsuarioContrato) {
        return usuarioContratoRepository.findById(idUsuarioContrato)
                .map(usuarioContrato -> modelMapper.map(usuarioContrato, UsuarioContratoDTO.class)); // Convertir a DTO
    }

    public List<UsuarioContratoDTO> findAll() {
        return usuarioContratoRepository.findAll()
                .stream()
                .map(usuarioContrato -> modelMapper.map(usuarioContrato, UsuarioContratoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idUsuarioContrato) {
        // Buscar el bloque en la base de datos
        UsuarioContrato usuarioContratoEliminar = usuarioContratoRepository.findById(idUsuarioContrato)
                .orElseThrow(() -> new EntityNotFoundException("usuario no encontrado"));

        // Convertir la entidad a DTO
        UsuarioContratoDTO usuarioContratoDTO = modelMapper.map(usuarioContratoEliminar, UsuarioContratoDTO.class);

        usuarioContratoRepository.deleteById(idUsuarioContrato);
    }

//    public UsuarioContratoTotalDTO obtenerInformacionUsuario(String documento) {
//        UsuarioContratoTotalDTO info = usuarioContratoRepository.findUsuarioInfoByDocumento(documento);
//
//        if (info == null) {
//            throw new EntityNotFoundException("Usuario no encontrado con documento: " + documento);
//        }
//
//        return info;
//    }
//
//    public UsuarioContratoTotalDTO obtenerInformacionUsuarioCompleta(String documento) {
//        List<UsuarioContratoTotalDTO> resultados = usuarioContratoRepository.findAllUsuarioInfoByDocumento(documento);
//
//        if (resultados.isEmpty()) {
//            throw new EntityNotFoundException("Usuario no encontrado con documento: " + documento);
//        }
//
//        // Tomar el primer resultado y concatenar múltiples valores
//        UsuarioContratoTotalDTO primer = resultados.get(0);
//
//        String profesiones = resultados.stream()
//                .map(UsuarioContratoTotalDTO::getProfesion)
//                .filter(Objects::nonNull)
//                .distinct()
//                .collect(Collectors.joining(", "));
//
//        String contratos = resultados.stream()
//                .map(UsuarioContratoTotalDTO::getContrato)
//                .filter(Objects::nonNull)
//                .distinct()
//                .collect(Collectors.joining(", "));
//
//        String roles = resultados.stream()
//                .map(UsuarioContratoTotalDTO::getRol)
//                .filter(Objects::nonNull)
//                .distinct()
//                .collect(Collectors.joining(", "));
//
//        return new UsuarioContratoTotalDTO(
//                primer.getDocumento(),
//                primer.getNombre(),
//                primer.getTelefono(),
//                primer.getEmail(),
//                profesiones.isEmpty() ? "Sin profesión" : profesiones,
//                contratos.isEmpty() ? "Sin contrato" : contratos,
//                roles.isEmpty() ? "Sin rol" : roles
//        );
//    }

    public UsuarioContratoTotalDTO obtenerInformacionUsuarioCompleta(String documento) {
        List<UsuarioContratoTotalDTO> resultados = usuarioContratoRepository.findAllUsuarioInfoByDocumento(documento);

        if (resultados.isEmpty()) {
            throw new EntityNotFoundException("Usuario no encontrado con documento: " + documento);
        }

        UsuarioContratoTotalDTO base = resultados.get(0);

        String profesiones = resultados.stream()
                .map(UsuarioContratoTotalDTO::getProfesion)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        String contratos = resultados.stream()
                .map(UsuarioContratoTotalDTO::getContrato)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        String roles = resultados.stream()
                .map(UsuarioContratoTotalDTO::getRol)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        return new UsuarioContratoTotalDTO(
                base.getDocumento(),
                base.getNombre(),
                base.getTelefono(),
                base.getEmail(),
                profesiones.isEmpty() ? "Sin profesión" : profesiones,
                contratos.isEmpty() ? "Sin contrato" : contratos,
                roles.isEmpty() ? "Sin rol" : roles
        );
    }

}
