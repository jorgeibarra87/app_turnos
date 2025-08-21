package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.MacroprocesosDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.Macroprocesos;
import com.turnos.enfermeria.repository.MacroprocesosRepository;
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
public class MacroprocesosService {

    private final MacroprocesosRepository macroprocesosRepo;
    private final ModelMapper modelMapper;

    public Macroprocesos create(Macroprocesos macroprocesos) {

        return macroprocesosRepo.save(macroprocesos);
    }

    public MacroprocesosDTO create(MacroprocesosDTO macroprocesosDTO) {
        Macroprocesos macroprocesos = modelMapper.map(macroprocesosDTO, Macroprocesos.class);
        macroprocesos.setIdMacroproceso(macroprocesosDTO.getIdMacroproceso());
        macroprocesos.setNombre(macroprocesosDTO.getNombre());

        Macroprocesos macroprocesoGuardado = macroprocesosRepo.save(macroprocesos);

        return modelMapper.map(macroprocesoGuardado, MacroprocesosDTO.class);

    }

    public MacroprocesosDTO update(MacroprocesosDTO detalleMacroprocesosDTO, Long id) {
        Macroprocesos macroprocesoExistente = macroprocesosRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Macroproceso no encontrado"));

        MacroprocesosDTO macroprocesosDTO = modelMapper.map(macroprocesoExistente, MacroprocesosDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleMacroprocesosDTO.getIdMacroproceso()!= null) {
            macroprocesoExistente.setIdMacroproceso(detalleMacroprocesosDTO.getIdMacroproceso());
        }
        if (detalleMacroprocesosDTO.getNombre() != null) {
            macroprocesoExistente.setNombre(detalleMacroprocesosDTO.getNombre());
        }

        // Guardar en la base de datos
        Macroprocesos macroprocesoActualizado = macroprocesosRepo.save(macroprocesoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(macroprocesoActualizado, MacroprocesosDTO.class);
    }

    public Optional<MacroprocesosDTO> findById(Long idMacroproceso) {
        return macroprocesosRepo.findById(idMacroproceso)
                .map(macroprocesos -> modelMapper.map(macroprocesos, MacroprocesosDTO.class)); // Convertir a DTO
    }

    public List<MacroprocesosDTO> findAll() {
        return macroprocesosRepo.findAll()
                .stream()
                .map(macroprocesos -> modelMapper.map(macroprocesos, MacroprocesosDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long idMacroproceso) {
        // Buscar el bloque en la base de datos
        Macroprocesos macroprocesoEliminar = macroprocesosRepo.findById(idMacroproceso)
                .orElseThrow(() -> new EntityNotFoundException("macroproceso no encontrado"));

        // Convertir la entidad a DTO
        MacroprocesosDTO macroprocesosDTO = modelMapper.map(macroprocesoEliminar, MacroprocesosDTO.class);

        macroprocesosRepo.deleteById(idMacroproceso);
    }
}
