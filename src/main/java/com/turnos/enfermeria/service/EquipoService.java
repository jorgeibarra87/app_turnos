package com.turnos.enfermeria.service;

import com.turnos.enfermeria.events.CambioEquipoEvent;
import com.turnos.enfermeria.model.dto.EquipoDTO;
import com.turnos.enfermeria.model.dto.EquipoSelectionDTO;
import com.turnos.enfermeria.model.dto.MiembroPerfilDTO;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.model.entity.Usuario;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final ServicioRepository servicioRepository;
    private final MacroprocesosRepository macroprocesoRepository;
    private final ProcesosRepository procesoRepository;
    private final SeccionesServicioRepository seccionRepository;
    private final SubseccionesServicioRepository subseccionRepository;
    private final CambiosEquipoService cambiosEquipoService;
    private final ApplicationEventPublisher eventPublisher;

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
        equipo.setObservaciones(equipoDTO.getObservaciones());

        Equipo equipoGuardado = equipoRepository.save(equipo);
        // Registrar cambio
        cambiosEquipoService.registrarCambioEquipo(null, equipoGuardado, "CREACION");

        // PUBLICAR EVENTO PARA NOTIFICACIONES
        eventPublisher.publishEvent(new CambioEquipoEvent(
                equipoGuardado.getIdEquipo(),
                "CREACIÓN DE EQUIPO",
                "Se ha creado un nuevo equipo: " + equipoGuardado.getNombre() +
                        ". Categoría: " + (selection != null ? selection.getCategoria() : "Individual") +
                        ". Subcategoría: " + (selection != null ? selection.getSubcategoria() : "No especificada")
        ));
        return modelMapper.map(equipoGuardado, EquipoDTO.class);
    }

    public EquipoDTO update(EquipoDTO detalleEquipoDTO, Long id) {
        return update(detalleEquipoDTO, id, null);
    }

    public EquipoDTO update(EquipoDTO detalleEquipoDTO, Long id, EquipoSelectionDTO selection) {
        Equipo equipoExistente = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        String nombreAnterior = equipoExistente.getNombre();

        // Actualizar nombre usando lógica unificada
        if (detalleEquipoDTO.getNombre() != null || selection != null) {
            String nuevoNombre = generateOrValidateName(detalleEquipoDTO.getNombre(), selection);
            equipoExistente.setNombre(nuevoNombre);
        }

        // Actualizar otros campos si no son nulos
        if (detalleEquipoDTO.getIdEquipo() != null) {
            equipoExistente.setIdEquipo(detalleEquipoDTO.getIdEquipo());
        }
        if (detalleEquipoDTO.getObservaciones() != null) {
            equipoExistente.setObservaciones(detalleEquipoDTO.getObservaciones());
        }

        Equipo equipoActualizado = equipoRepository.save(equipoExistente);
        // Registrar cambio
        cambiosEquipoService.registrarCambioEquipo(equipoActualizado, equipoActualizado, "MODIFICACION");

        // PUBLICAR EVENTO PARA NOTIFICACIONES
        eventPublisher.publishEvent(new CambioEquipoEvent(
                equipoActualizado.getIdEquipo(),
                "MODIFICACIÓN DE EQUIPO",
                "Se ha modificado el equipo ID: " + id +
                        ". Nombre anterior: " + nombreAnterior +
                        ". Nuevo nombre: " + equipoActualizado.getNombre()
        ));
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
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));

        String nombreEquipo = equipo.getNombre();

        // 🔥 DESVINCULAR TODAS LAS PERSONAS DEL EQUIPO
        List<Usuario> usuariosDelEquipo = usuarioRepository.findDistinctByEquipos_IdEquipo(idEquipo);

        for (Usuario usuario : usuariosDelEquipo) {
            cambiosEquipoService.registrarCambioPersonaEquipo(
                    usuario.getPersona(),
                    equipo, // equipoAnterior
                    null, // equipoNuevo = null porque se elimina el equipo
                    "DESVINCULACION"
            );

            // Remover la relación
            usuario.getEquipos().remove(equipo);
            usuarioRepository.save(usuario);
        }

        // Registrar eliminación del equipo
        cambiosEquipoService.registrarCambioEquipo(equipo, null, "ELIMINACION");

        // PUBLICAR EVENTO PARA NOTIFICACIONES
        eventPublisher.publishEvent(new CambioEquipoEvent(
                idEquipo,
                "ELIMINACIÓN DE EQUIPO",
                "Se ha eliminado el equipo: " + nombreEquipo +
                        ". Se desvincularon " + usuariosDelEquipo.size() + " personas del equipo."
        ));
        equipoRepository.deleteById(idEquipo);
    }

//    public void delete(Long idEquipo) {
//        equipoRepository.findById(idEquipo)
//                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));
//        equipoRepository.deleteById(idEquipo);
//    }

    public EquipoDTO createWithGeneratedName(EquipoSelectionDTO selection) {
        // Crear un DTO vacío
        EquipoDTO equipoDTO = new EquipoDTO();
        equipoDTO.setEstado(true);
        equipoDTO.setObservaciones(selection.getObservaciones());
        return create(equipoDTO, selection);
    }

    public EquipoDTO updateWithGeneratedName(Long idEquipo, EquipoSelectionDTO selection) {
        // Crear un DTO vacío
        EquipoDTO equipoDTO = new EquipoDTO();
        equipoDTO.setObservaciones(selection.getObservaciones());
        return update(equipoDTO, idEquipo, selection);
    }

    // Método de generación de nombres
    public String generateEquipoName(EquipoSelectionDTO selection) {
        if (selection == null || selection.getCategoria() == null || selection.getSubcategoria() == null) {
            throw new IllegalArgumentException("Categoría y subcategoría no pueden ser nulos");
        }

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
