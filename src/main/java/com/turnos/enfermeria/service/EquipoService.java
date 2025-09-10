package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.EquipoDTO;
import com.turnos.enfermeria.model.dto.EquipoSelectionDTO;
import com.turnos.enfermeria.model.dto.MiembroPerfilDTO;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final ModelMapper modelMapper;
    private final ServicioRepository servicioRepository;
    private final MacroprocesosRepository macroprocesoRepository;
    private final ProcesosRepository procesoRepository;
    private final SeccionesServicioRepository seccionRepository;
    private final SubseccionesServicioRepository subseccionRepository;

    private String generateOrValidateName(String providedName, EquipoSelectionDTO selection) {
        // Si no se proporciona nombre O se proporciona selection, generar automáticamente
        if (providedName == null || providedName.trim().isEmpty() || selection != null) {
            return generateEquipoName(selection);
        }
        return providedName.trim();
    }

    public EquipoDTO create(EquipoDTO equipoDTO) {
        return create(equipoDTO, null);
    }

    public EquipoDTO create(EquipoDTO equipoDTO, EquipoSelectionDTO selection) {
        Equipo equipo = modelMapper.map(equipoDTO, Equipo.class);

        String nombre = generateOrValidateName(equipoDTO.getNombre(), selection);
        equipo.setNombre(nombre);
        equipo.setEstado(true); // Asegurar que siempre tenga estado

        Equipo equipoGuardado = equipoRepository.save(equipo);
        return modelMapper.map(equipoGuardado, EquipoDTO.class);
    }

    public EquipoDTO update(EquipoDTO detalleEquipoDTO, Long id) {
        return update(detalleEquipoDTO, id, null);
    }

    public EquipoDTO update(EquipoDTO detalleEquipoDTO, Long id, EquipoSelectionDTO selection) {
        Equipo equipoExistente = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // Actualizar nombre usando lógica unificada
        if (detalleEquipoDTO.getNombre() != null || selection != null) {
            String nuevoNombre = generateOrValidateName(detalleEquipoDTO.getNombre(), selection);
            equipoExistente.setNombre(nuevoNombre);
        }

        // Actualizar otros campos si no son nulos
        if (detalleEquipoDTO.getIdEquipo() != null) {
            equipoExistente.setIdEquipo(detalleEquipoDTO.getIdEquipo());
        }

        Equipo equipoActualizado = equipoRepository.save(equipoExistente);
        return modelMapper.map(equipoActualizado, EquipoDTO.class);
    }

    public Optional<EquipoDTO> findById(Long idEquipo) {
        return equipoRepository.findById(idEquipo)
                .map(equipo -> modelMapper.map(equipo, EquipoDTO.class));
    }

    public List<EquipoDTO> findAll() {
        return equipoRepository.findAllByOrderByIdEquipoAsc()
                .stream()
                .map(equipo -> modelMapper.map(equipo, EquipoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idEquipo) {
        equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));
        equipoRepository.deleteById(idEquipo);
    }

    // MÉTODOS NUEVOS PARA EL FRONTEND
    public EquipoDTO createWithGeneratedName(EquipoSelectionDTO selection) {
        // Crear un DTO vacío
        EquipoDTO equipoDTO = new EquipoDTO();
        equipoDTO.setEstado(true);
        return create(equipoDTO, selection);
    }

    public EquipoDTO updateWithGeneratedName(Long idEquipo, EquipoSelectionDTO selection) {
        // Crear un DTO vacío
        EquipoDTO equipoDTO = new EquipoDTO();
        return update(equipoDTO, idEquipo, selection);
    }

    // Método de generación de nombres
    public String generateEquipoName(EquipoSelectionDTO selection) {
        if (selection == null || selection.getCategoria() == null || selection.getSubcategoria() == null) {
            throw new IllegalArgumentException("Categoría y subcategoría no pueden ser nulos");
        }

        // RESTAURAR: Mantener categoría como viene del frontend (Proceso, Servicio, etc.)
        String categoria = selection.getCategoria(); // Mantener como "Proceso", "Servicio", etc.
        String subcategoria = selection.getSubcategoria().toUpperCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^A-Z0-9_]", "");

        String baseNombre = "EQUIPO_" + categoria + "_" + subcategoria;
        Long conteo = equipoRepository.countByNombreStartingWith(baseNombre);
        String consecutivo = String.format("_%02d", conteo + 1);

        return baseNombre + consecutivo;
    }

    public Equipo createEquipoWithGeneratedName(EquipoSelectionDTO selection) {
        EquipoDTO resultado = createWithGeneratedName(selection);
        return modelMapper.map(resultado, Equipo.class);
    }

    public List<MiembroPerfilDTO> obtenerMiembrosConPerfil(Long equipoId) {
        List<Object[]> resultados = equipoRepository.findMiembrosConPerfilRaw(equipoId);

        Map<Long, MiembroPerfilDTO> map = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            Long idPersona = ((Number) fila[0]).longValue();
            String nombre = (String) fila[1];
            String titulo = (String) fila[2];
            String documento = (String) fila[3];

            map.computeIfAbsent(idPersona, id -> new MiembroPerfilDTO(id, nombre, new ArrayList<>(), documento));
            if (titulo != null) {
                map.get(idPersona).getTitulos().add(titulo);
            }
        }

        return new ArrayList<>(map.values());
    }
}
