package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.repository.BloqueServicioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class BloqueServicioService {

    private final BloqueServicioRepository bloqueServicioRepo;
    private final ModelMapper modelMapper;

    public BloqueServicioDTO create(BloqueServicioDTO bloqueServicioDTO) {
        BloqueServicio bloqueServicio = modelMapper.map(bloqueServicioDTO, BloqueServicio.class);
        //bloqueServicio.setIdBloqueServicio(bloqueServicioDTO.getIdBloqueServicio());
        bloqueServicio.setNombre(bloqueServicioDTO.getNombre());

        BloqueServicio bloqueServicioGuardado = bloqueServicioRepo.save(bloqueServicio);

        return modelMapper.map(bloqueServicioGuardado, BloqueServicioDTO.class);

    }


    public BloqueServicioDTO update(BloqueServicioDTO detalleBloqueServicioDTO, Long id) {
        BloqueServicio bloqueServicioExistente = bloqueServicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("BloqueServicio no encontrado"));

        BloqueServicioDTO bloqueServicioDTO = modelMapper.map(bloqueServicioExistente, BloqueServicioDTO.class);

        // Actualizar los campos si no son nulos
//        if (detalleBloqueServicioDTO.getIdBloqueServicio()!= null) {
//            bloqueServicioExistente.setIdBloqueServicio(detalleBloqueServicioDTO.getIdBloqueServicio());
//        }
        if (detalleBloqueServicioDTO.getNombre() != null) {
            bloqueServicioExistente.setNombre(detalleBloqueServicioDTO.getNombre());
        }

        // Guardar en la base de datos
        BloqueServicio bloqueServicioActualizado = bloqueServicioRepo.save(bloqueServicioExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(bloqueServicioActualizado, BloqueServicioDTO.class);
    }

    public Optional<BloqueServicioDTO> findById(Long idBloqueServicio) {
        return bloqueServicioRepo.findById(idBloqueServicio)
                .map(bloqueServicio -> modelMapper.map(bloqueServicio, BloqueServicioDTO.class)); // Convertir a DTO
    }

    public List<BloqueServicioDTO> findAll() {
        return bloqueServicioRepo.findAll()
                .stream()
                .map(bloqueServicio -> modelMapper.map(bloqueServicio, BloqueServicioDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idBloqueServicio) {
        // Buscar el bloque en la base de datos
        BloqueServicio bloqueServicioEliminar = bloqueServicioRepo.findById(idBloqueServicio)
                .orElseThrow(() -> new EntityNotFoundException("Bloque no encontrado"));

        // Convertir la entidad a DTO
        BloqueServicioDTO bloqueServicioDTO = modelMapper.map(bloqueServicioEliminar, BloqueServicioDTO.class);

        bloqueServicioRepo.deleteById(idBloqueServicio);
    }
}
