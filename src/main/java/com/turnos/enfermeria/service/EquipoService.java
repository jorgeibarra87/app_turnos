package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.EquipoDTO;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.model.entity.TipoFormacionAcademica;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import com.turnos.enfermeria.repository.CuadroTurnoRepository;
import com.turnos.enfermeria.repository.EquipoRepository;
import com.turnos.enfermeria.repository.TipoFormacionAcademicaRepository;
import com.turnos.enfermeria.repository.TitulosFormacionAcademicaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final ModelMapper modelMapper;
    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final TitulosFormacionAcademicaRepository titulosRepository;
    private final TipoFormacionAcademicaRepository tipoFormacionRepository;
    private final UsuarioService usuarioService;

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

    public EquipoDTO update(EquipoDTO detalleEquipoDTO, Long id) {
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
     * Crea un equipo basado en un perfil específico (título de formación académica)
     * @param idTitulo ID del título de formación académica
     * @return EquipoDTO del equipo creado
     */
    public EquipoDTO createEquipoByPerfil(Long idTitulo, List<Long> idsUsuarios) {
        // Validar que existe el título
        TitulosFormacionAcademica titulo = titulosRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("Título de formación académica no encontrado."));

        // Obtener el nombre del perfil y limpiar caracteres especiales
        String nombrePerfil = limpiarNombrePerfil(titulo.getTitulo());

        // Generar el nombre del equipo con numeración secuencial
        String nombreEquipo = generarNombreEquipo(nombrePerfil);

        // Crear el equipo
        Equipo nuevoEquipo = new Equipo();
        nuevoEquipo.setNombre(nombreEquipo);

        // Guardar el equipo
        Equipo equipoGuardado = equipoRepository.save(nuevoEquipo);

        // Crear las asociaciones usuario-equipo con los IDs proporcionados
        if (idsUsuarios != null && !idsUsuarios.isEmpty()) {
            usuarioService.actualizarUsuariosDeEquipo(equipoGuardado.getIdEquipo(), idsUsuarios);
        }


        return modelMapper.map(equipoGuardado, EquipoDTO.class);
    }

    /**
     * Genera el nombre del equipo con formato: Perfil_{nombrePerfil}_Equipo_{numero}
     * @param nombrePerfil Nombre del perfil limpio
     * @return Nombre del equipo generado
     */
    private String generarNombreEquipo(String nombrePerfil) {
        String prefijo = "Perfil_" + nombrePerfil + "_Equipo_";

        // Buscar equipos existentes con este prefijo
        List<Equipo> equiposExistentes = equipoRepository.findByNombreStartingWith(prefijo);

        int numeroSiguiente = obtenerSiguienteNumero(equiposExistentes, prefijo);

        return prefijo + String.format("%02d", numeroSiguiente);
    }

    /**
     * Obtiene el siguiente número disponible para el equipo
     * @param equiposExistentes Lista de equipos existentes con el prefijo
     * @param prefijo Prefijo del nombre del equipo
     * @return Siguiente número disponible
     */
    private int obtenerSiguienteNumero(List<Equipo> equiposExistentes, String prefijo) {
        int maxNumero = 0;

        // Patrón para extraer el número al final del nombre
        Pattern patron = Pattern.compile(Pattern.quote(prefijo) + "(\\d+)$");

        for (Equipo equipo : equiposExistentes) {
            Matcher matcher = patron.matcher(equipo.getNombre());
            if (matcher.find()) {
                int numero = Integer.parseInt(matcher.group(1));
                maxNumero = Math.max(maxNumero, numero);
            }
        }

        return maxNumero + 1;
    }

    /**
     * Limpia el nombre del perfil removiendo caracteres especiales y espacios
     * @param nombreOriginal Nombre original del perfil
     * @return Nombre limpio para usar en el equipo
     */
    private String limpiarNombrePerfil(String nombreOriginal) {
        if (nombreOriginal == null || nombreOriginal.trim().isEmpty()) {
            return "SinEspecificar";
        }

        return nombreOriginal
                .trim()
                .replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]", "") // Remover caracteres especiales excepto letras y espacios
                .replaceAll("\\s+", "_") // Reemplazar espacios por guiones bajos
                .replaceAll("_+", "_") // Evitar múltiples guiones bajos consecutivos
                .replaceAll("^_|_$", ""); // Remover guiones bajos al inicio y final
    }

    /**
     * Obtiene todos los equipos de un perfil específico
     * @param idTitulo ID del título de formación académica
     * @return Lista de equipos del perfil
     */
    public List<EquipoDTO> findEquiposByPerfil(Long idTitulo) {
        // Validar que existe el título
        TitulosFormacionAcademica titulo = titulosRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("Título de formación académica no encontrado."));

        String nombrePerfil = limpiarNombrePerfil(titulo.getTitulo());
        String prefijo = "Perfil_" + nombrePerfil + "_Equipo_";

        return equipoRepository.findByNombreStartingWith(prefijo)
                .stream()
                .map(equipo -> modelMapper.map(equipo, EquipoDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los equipos de un tipo de formación específico
     * @param idTipoFormacion ID del tipo de formación académica
     * @return Lista de equipos del tipo de formación
     */
    public List<EquipoDTO> findEquiposByTipoFormacion(Long idTipoFormacion) {
        // Validar que existe el tipo de formación
        TipoFormacionAcademica tipoFormacion = tipoFormacionRepository.findById(idTipoFormacion)
                .orElseThrow(() -> new RuntimeException("Tipo de formación académica no encontrado."));

        String nombrePerfil = limpiarNombrePerfil(tipoFormacion.getTipo());
        String prefijo = "Perfil_" + nombrePerfil + "_Equipo_";

        return equipoRepository.findByNombreStartingWith(prefijo)
                .stream()
                .map(equipo -> modelMapper.map(equipo, EquipoDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Cuenta cuántos equipos existen para un perfil específico
     * @param idTitulo ID del título de formación académica
     * @return Número de equipos del perfil
     */
    public long contarEquiposByPerfil(Long idTitulo) {
        TitulosFormacionAcademica titulo = titulosRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("Título de formación académica no encontrado."));

        String nombrePerfil = limpiarNombrePerfil(titulo.getTitulo());
        String prefijo = "Perfil_" + nombrePerfil + "_Equipo_";

        return equipoRepository.countByNombreStartingWith(prefijo);
    }

}
