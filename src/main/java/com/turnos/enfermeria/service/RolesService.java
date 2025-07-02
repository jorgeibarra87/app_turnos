package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.RolesDTO;
import com.turnos.enfermeria.model.entity.Roles;
import com.turnos.enfermeria.repository.RolesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RolesService {

    private final RolesRepository rolesRepo;
    private final ModelMapper modelMapper;

    public RolesDTO create(RolesDTO rolesDTO) {
        Roles roles = modelMapper.map(rolesDTO, Roles.class);
        roles.setIdRol(rolesDTO.getIdRol());
        roles.setRol(rolesDTO.getRol());
        roles.setDescripcion(rolesDTO.getDescripcion());

        Roles rolGuardado = rolesRepo.save(roles);

        return modelMapper.map(rolGuardado, RolesDTO.class);

    }

    public RolesDTO update(RolesDTO detalleRolesDTO, Long id) {
        Roles rolExistente = rolesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        RolesDTO rolesDTO = modelMapper.map(rolExistente, RolesDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleRolesDTO.getIdRol()!= null) {
            rolExistente.setIdRol(detalleRolesDTO.getIdRol());
        }
        if (detalleRolesDTO.getRol() != null) {
            rolExistente.setRol(detalleRolesDTO.getRol());
        }
        if (detalleRolesDTO.getDescripcion() != null) {
            rolExistente.setDescripcion(detalleRolesDTO.getDescripcion());
        }

        // Guardar en la base de datos
        Roles rolActualizado = rolesRepo.save(rolExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(rolActualizado, RolesDTO.class);
    }

    public Optional<RolesDTO> findById(Long id) {
        return rolesRepo.findById(id)
                .map(roles -> modelMapper.map(roles, RolesDTO.class)); // Convertir a DTO
    }

    public List<RolesDTO> findAll() {
        return rolesRepo.findAll()
                .stream()
                .map(roles -> modelMapper.map(roles, RolesDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long id) {
        // Buscar el bloque en la base de datos
        Roles rolEliminar = rolesRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));

        // Convertir la entidad a DTO
        RolesDTO rolesDTO = modelMapper.map(rolEliminar, RolesDTO.class);

        rolesRepo.deleteById(id);
    }
}
