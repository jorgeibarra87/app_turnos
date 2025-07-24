package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.EquipoDTO;
import com.turnos.enfermeria.model.dto.EquipoSelectionDTO;
import com.turnos.enfermeria.model.dto.MiembroPerfilDTO;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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
    private final EntityManager entityManager;



    public EquipoDTO create(EquipoDTO equipoDTO) {

//        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(equipoDTO.getIdCuadroTurno())
//                .orElseThrow(() -> new RuntimeException("Cuadro de turno no encontrado."));

        Equipo equipo = modelMapper.map(equipoDTO, Equipo.class);
        equipo.setIdEquipo(equipoDTO.getIdEquipo());
        equipo.setNombre(equipoDTO.getNombre());
//        equipo.setCuadroTurno(cuadroTurno);

        Equipo equipoGuardado = equipoRepository.save(equipo);

        return modelMapper.map(equipoGuardado, EquipoDTO.class);

    }

    public EquipoDTO ***REMOVED***(EquipoDTO detalleEquipoDTO, Long id) {
        Equipo equipoExistente = equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

//        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(detalleEquipoDTO.getIdCuadroTurno())
//                .orElseThrow(() -> new RuntimeException("Cuadro de turno no encontrado."));

        EquipoDTO equipoDTO = modelMapper.map(equipoExistente, EquipoDTO.class);

        // Actualizar los campos si no son nulos
        if (detalleEquipoDTO.getIdEquipo()!= null) {
            equipoExistente.setIdEquipo(detalleEquipoDTO.getIdEquipo());
        }
        if (detalleEquipoDTO.getNombre() != null) {
            equipoExistente.setNombre(detalleEquipoDTO.getNombre());
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










    /**
     * Genera un nombre único para el equipo basado en la categoría y subcategoría seleccionadas
     */
    public String generateEquipoName(EquipoSelectionDTO selection) {
        // Obtener el nombre de la subcategoría según la categoría
        String subcategoriaNombre = getSubcategoriaNombre(selection.getCategoria(), selection.getSubcategoria());

        // Limpiar y normalizar los nombres
        String categoriaNormalizada = normalizeNameForEquipo(selection.getCategoria());
        String subcategoriaNormalizada = normalizeNameForEquipo(subcategoriaNombre);

        // Construir el prefijo base: Equipo_Categoria_Subcategoria
        String basePrefix = "Equipo_" + categoriaNormalizada + "_" + subcategoriaNormalizada;

        // Obtener el siguiente consecutivo
        int nextConsecutive = getNextConsecutive(basePrefix);

        // Formatear el consecutivo con ceros a la izquierda
        String consecutiveFormatted = String.format("%02d", nextConsecutive);

        return basePrefix + "_" + consecutiveFormatted;
    }

    /**
     * Obtiene el nombre de la subcategoría según la categoría seleccionada
     */
    private String getSubcategoriaNombre(String categoria, String subcategoriaId) {
        switch (categoria.toUpperCase()) {
            case "SERVICIO":
                return servicioRepository.findById(Long.parseLong(subcategoriaId))
                        .map(servicio -> servicio.getNombre())
                        .orElse("UNKNOWN");

            case "MACROPROCESO":
                return macroprocesoRepository.findById(Long.parseLong(subcategoriaId))
                        .map(macroproceso -> macroproceso.getNombre())
                        .orElse("UNKNOWN");

            case "PROCESO":
                return procesoRepository.findById(Long.parseLong(subcategoriaId))
                        .map(proceso -> proceso.getNombre())
                        .orElse("UNKNOWN");

            case "SECCION":
                return seccionRepository.findById(Long.parseLong(subcategoriaId))
                        .map(seccion -> seccion.getNombre())
                        .orElse("UNKNOWN");

            case "SUBSECCION":
                return subseccionRepository.findById(Long.parseLong(subcategoriaId))
                        .map(subseccion -> subseccion.getNombre())
                        .orElse("UNKNOWN");

            case "MULTIPROCESO":
                return "MULTIPROCESO";

            default:
                return "UNKNOWN";
        }
    }

    /**
     * Obtiene el siguiente consecutivo para un prefijo dado
     */
    private int getNextConsecutive(String basePrefix) {
        // Buscar todos los equipos que empiecen con el prefijo base
        List<Equipo> existingEquipos = equipoRepository.findByNombreStartingWith(basePrefix);

        int maxConsecutive = 0;

        // Extraer el número consecutivo más alto
        for (Equipo equipo : existingEquipos) {
            String nombre = equipo.getNombre();
            if (nombre.startsWith(basePrefix + "_")) {
                String consecutivePart = nombre.substring(basePrefix.length() + 1);
                try {
                    int consecutive = Integer.parseInt(consecutivePart);
                    maxConsecutive = Math.max(maxConsecutive, consecutive);
                } catch (NumberFormatException e) {
                    // Ignorar nombres que no sigan el patrón esperado
                }
            }
        }

        return maxConsecutive + 1;
    }

    /**
     * Crea un nuevo equipo con nombre generado automáticamente
     */
    public Equipo createEquipoWithGeneratedName(EquipoSelectionDTO selection) {
        String generatedName = generateEquipoName(selection);

        Equipo equipo = new Equipo();
        equipo.setNombre(generatedName);
        equipo.setEstado(true);

        return equipoRepository.save(equipo);
    }

    /**
     * Normaliza el nombre para usar en el equipo:
     * - Convierte a mayúsculas
     * - Reemplaza espacios por guiones bajos
     * - Elimina caracteres especiales
     * - Reemplaza múltiples guiones bajos consecutivos por uno solo
     */
    private String normalizeNameForEquipo(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "UNKNOWN";
        }

        return name.trim()
                .toUpperCase()
                .replaceAll("\\s+", "_")                    // Espacios por guiones bajos
                .replaceAll("[^A-Z0-9_]", "")               // Solo letras, números y guiones bajos
                .replaceAll("_{2,}", "_")                   // Múltiples guiones bajos por uno solo
                .replaceAll("^_+|_+$", "");                // Eliminar guiones bajos al inicio/final
    }

    public List<MiembroPerfilDTO> obtenerMiembrosConPerfil(Long equipoId) {
        String sql = "SELECT p.id_persona, p.nombre_completo, tfa.titulo " +
                "FROM usuarios_equipo ue " +
                "JOIN persona p ON ue.id_persona = p.id_persona " +
                "LEFT JOIN usuarios_titulos ut ON ut.id_persona = p.id_persona " +
                "LEFT JOIN titulos_formacion_academica tfa ON ut.id_titulo = tfa.id_titulo " +
                "WHERE ue.id_equipo = :equipoId";

        List<Object[]> resultados = entityManager.createNativeQuery(sql)
                .setParameter("equipoId", equipoId)
                .getResultList();

        Map<Long, MiembroPerfilDTO> map = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            Long idPersona = ((Number) fila[0]).longValue();
            String nombre = (String) fila[1];
            String titulo = (String) fila[2];

            map.computeIfAbsent(idPersona, id -> new MiembroPerfilDTO(id, nombre, new ArrayList<>()));
            if (titulo != null) {
                map.get(idPersona).getTitulos().add(titulo);
            }
        }

        return new ArrayList<>(map.values());
    }

}
