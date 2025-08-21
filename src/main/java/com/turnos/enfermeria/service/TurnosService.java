package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.CambiosTurnoDTO;
import com.turnos.enfermeria.model.dto.TurnoDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TurnosService {

    private final TurnosRepository turnosRepository;
    private final ContratoRepository contratoRepository;
    private final TipoTurnoRepository tipoTurnoRepository;
    private final UsuarioContratoRepository usuarioContratoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final CambiosTurnoRepository cambiosTurnoRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public TurnoDTO create(TurnoDTO turnoDTO) {
        Usuario usuario = usuarioRepository.findById(turnoDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("El usuario es obligatorio."));

        if (turnoDTO.getFechaInicio() == null || turnoDTO.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas.");
        }
        if (turnoDTO.getFechaInicio().isAfter(turnoDTO.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(turnoDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("Cuadro de turno no encontrado."));

        // Verificar si hay solapamiento de turnos
        List<Turnos> turnosSolapados = turnosRepository.findTurnosSolapados(
                turnoDTO.getIdPersona(), turnoDTO.getFechaInicio(), turnoDTO.getFechaFin());
        if (!turnosSolapados.isEmpty()) {
            throw new IllegalArgumentException("Ya existe un turno en este horario para el usuario.");
        }

        Long usuarioId = usuario.getIdPersona();
        LocalDate fechaTurno = turnoDTO.getFechaInicio().toLocalDate();
        long horasTurno = calcularHorasTrabajadas(turnoDTO.getFechaInicio(), turnoDTO.getFechaFin());

        // Obtener contrato del usuario para validar si puede tener turnos excepcionales
//        Contrato contrato = gestorContratoRepository.findContratoByUsuarioId(usuarioId)
//                .orElseThrow(() -> new RuntimeException("No se encontró un contrato asignado para el usuario."));

        // Validar horas mensuales
        int horasMensuales = turnosRepository.obtenerHorasMensuales(
                usuarioId,
                String.valueOf(fechaTurno.getMonthValue()), // Convertir mes a String
                String.valueOf(fechaTurno.getYear())       // Convertir año a String
        );
        if (horasMensuales + horasTurno > 280) {
            throw new IllegalArgumentException("El usuario no puede superar las 280 horas trabajadas en el mes.");
        }

        // Obtener los turnos del mismo día
        List<Turnos> turnosDelDia = turnosRepository.obtenerTurnosPorFecha(usuarioId, fechaTurno);
        long horasDiariasTotales = turnosDelDia.stream().mapToLong(Turnos::getTotalHoras).sum() + horasTurno;

        // Validar turnos de 24 horas
        if (horasTurno == 24) {
            if (!cuadroTurno.getTurnoExcepcion()) {
                throw new IllegalArgumentException("este usuario no puede asignarse turnos de 24 horas.");
            }
        } else {
            if (horasTurno > 12) {
                throw new IllegalArgumentException("No se pueden asignar más de 12 horas seguidas.");
            }
            if (horasDiariasTotales > 18) {
                throw new IllegalArgumentException("No se pueden asignar más de 18 horas en un día.");
            }
        }

        // Crear el turno después de validar
        Turnos turno = modelMapper.map(turnoDTO, Turnos.class);
        turno.setUsuario(usuario);
        turno.setCuadroTurno(cuadroTurno);
        turno.setTotalHoras(calcularHorasTrabajadas(turnoDTO.getFechaInicio(), turnoDTO.getFechaFin()));
        turno.setVersion(generarVersion(turnoDTO.getFechaInicio()));
        turno.setComentarios(turnoDTO.getComentarios());

        Turnos turnoGuardado = turnosRepository.save(turno);

        // Guardar en historial como creación
        TurnoDTO turnoGuardadoDTO = modelMapper.map(turno, TurnoDTO.class);
        guardarCambioTurno(turno.getIdTurno(), turnoGuardadoDTO);

        return modelMapper.map(turnoGuardado, TurnoDTO.class);
    }



    public Optional<TurnoDTO> findById(Long idTurno) {
        return turnosRepository.findById(idTurno)
                .map(turno -> modelMapper.map(turno, TurnoDTO.class)); // Convertir a DTO
    }

    public List<TurnoDTO> findAll() {
        return turnosRepository.findAll()
                .stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    public List<CambiosTurnoDTO> obtenerCambiosPorTurno(Long idTurno) {
        return cambiosTurnoRepository.findCambiosTurnoDTOByTurno(idTurno);
    }


    private void guardarCambioTurno(Long idTurno, TurnoDTO turnoDTO) {
        // Buscar el turno original en la base de datos
        Turnos turnoOriginal = turnosRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        // Crear la entidad de cambio de turno usando ModelMapper
        CambiosTurno cambio = modelMapper.map(turnoDTO, CambiosTurno.class);

        // Asignar correctamente el turno original
        cambio.setTurno(turnoOriginal);
        cambio.setUsuario(turnoOriginal.getUsuario());
        cambio.setFechaCambio(LocalDateTime.now()); // Registrar la fecha del cambio
        cambio.setComentarios(turnoOriginal.getComentarios());

        // Guardar el cambio en la base de datos
        cambiosTurnoRepository.save(cambio);
    }

    /**
     * Obtiene todos los turnos actuales en formato DTO.
     */
    public List<TurnoDTO> obtenerTurnos() {
        List<Turnos> turnos = turnosRepository.findAll();
        return turnos.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de cambios de un turno específico en formato DTO.
     */
    public List<CambiosTurnoDTO> obtenerHistorialTurno(Long idTurno) {
        List<CambiosTurno> cambios = cambiosTurnoRepository.findByTurno_IdTurno(idTurno);
        return cambios.stream()
                .map(cambio -> modelMapper.map(cambio, CambiosTurnoDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Registra un nuevo turno y guarda la versión anterior en el historial.
     */
    public TurnoDTO actualizarTurno(Long id, TurnoDTO turnoDetallesDTO) {
        Turnos turnoExistente = turnosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        // Guardar el cambio en el historial antes de actualizar
        TurnoDTO turnoDTO = modelMapper.map(turnoExistente, TurnoDTO.class);
        guardarCambioTurno(turnoExistente.getIdTurno(), turnoDTO);

        // Actualizar los campos si no son nulos
        if (turnoDetallesDTO.getFechaInicio() != null) {
            turnoExistente.setFechaInicio(turnoDetallesDTO.getFechaInicio());
        }
        if (turnoDetallesDTO.getFechaFin() != null) {
            turnoExistente.setFechaFin(turnoDetallesDTO.getFechaFin());
        }
        if (turnoDetallesDTO.getEstadoTurno() != null) {
            turnoExistente.setEstadoTurno(turnoDetallesDTO.getEstadoTurno());
        }

        // Recalcular horas trabajadas y generar nueva versión
        turnoExistente.setTotalHoras(calcularHorasTrabajadas(
                turnoExistente.getFechaInicio(), turnoExistente.getFechaFin()));
        turnoExistente.setVersion(generarNuevaVersion(turnoExistente.getVersion()));

        // Guardar en la base de datos
        Turnos turnoActualizado = turnosRepository.save(turnoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(turnoActualizado, TurnoDTO.class);
    }

    public void eliminarTurno(Long id) {
        // Buscar el turno en la base de datos
        Turnos turnoEliminar = turnosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));

        // Convertir la entidad a DTO
        TurnoDTO turnoDTO = modelMapper.map(turnoEliminar, TurnoDTO.class);

        // Registrar el cambio antes de eliminar
        registrarCambioTurno(turnoEliminar.getIdTurno(), turnoDTO, "ELIMINACION");

        // Eliminar el turno
        turnosRepository.deleteById(id);
    }

    private void registrarCambioTurno(Long idTurno, TurnoDTO turnoDTO, String tipoCambio) {
        // Buscar el turno original en la base de datos
        Turnos turnoOriginal = turnosRepository.findById(idTurno)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));

        // Mapear el DTO a la entidad CambiosTurno
        CambiosTurno cambio = modelMapper.map(turnoDTO, CambiosTurno.class);

        // Asignar datos adicionales
        cambio.setTurno(turnoOriginal);
        cambio.setFechaCambio(LocalDateTime.now());
        //cambio.setTipoCambio(tipoCambio); // Registrar el tipo de cambio (CREACION, MODIFICACION, ELIMINACION)

        // Guardar el cambio en la base de datos
        cambiosTurnoRepository.save(cambio);
    }

    private long calcularHorasTrabajadas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            return 0;
        }
        return Duration.between(inicio, fin).toHours();
    }


    private String generarVersion(LocalDateTime fechaInicio) {
        return fechaInicio.format(DateTimeFormatter.ofPattern("MMyy")) + "_v1";
    }

    private String generarNuevaVersion(String versionAnterior) {
        String baseVersion = versionAnterior.split("_v")[0];
        int nuevaVersion = Integer.parseInt(versionAnterior.split("_v")[1]) + 1;
        return baseVersion + "_v" + nuevaVersion;
    }

    /**
     * Restaura un turno a una versión anterior.
     */

    public TurnoDTO restaurarTurno(Long idCambio) {
        // Buscar el cambio de turno en el historial
        CambiosTurno cambio = cambiosTurnoRepository.findById(idCambio)
                .orElseThrow(() -> new EntityNotFoundException("Cambio de turno no encontrado"));

        Optional<Turnos> turnoOpt = turnosRepository.findById(cambio.getTurno().getIdTurno());

        Turnos turnoRestaurado;
        if (turnoOpt.isPresent()) {
            turnoRestaurado = turnoOpt.get();
        } else {
            turnoRestaurado = new Turnos();
            turnoRestaurado.setUsuario(cambio.getUsuario());
        }

        // Restaurar los valores desde el cambio de turno
        turnoRestaurado.setFechaInicio(cambio.getFechaInicio());
        turnoRestaurado.setFechaFin(cambio.getFechaFin());
        turnoRestaurado.setEstadoTurno(cambio.getEstadoTurno());
        turnoRestaurado.setTotalHoras(cambio.getTotalHoras());
        turnoRestaurado.setVersion(generarNuevaVersion(cambio.getVersion()));

        // Guardar el turno restaurado en la base de datos
        Turnos turnoGuardado = turnosRepository.save(turnoRestaurado);

        // Convertir la entidad Turnos a TurnoDTO y retornarla
        return modelMapper.map(turnoGuardado, TurnoDTO.class);
    }

    public List<TurnoDTO> cambiarEstadoDeTodosLosTurnos(String estadoActual, String nuevoEstado) {
        // Obtener todos los turnos con el estado actual
        List<Turnos> turnos = turnosRepository.findByEstadoTurno(estadoActual);

        // Cambiar el estado en cada turno
        for (Turnos turno : turnos) {
            turno.setEstadoTurno(nuevoEstado);
        }

        // Guardar los cambios en la base de datos
        List<Turnos> turnosActualizados = turnosRepository.saveAll(turnos);

        // Convertir la lista de entidades a DTOs y retornarla
        return turnosActualizados.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    /** 📌 Restaurar turnos que tengan la versión indicada */
    @Transactional
    public List<TurnoDTO> restaurarTurnosPorVersion(String version) {
        // Buscar todos los cambios de turno que tienen la versión indicada
        List<CambiosTurno> cambiosTurno = cambiosTurnoRepository.findByVersion(version);

        if (cambiosTurno.isEmpty()) {
            throw new RuntimeException("No se encontraron turnos con la versión " + version);
        }

        // Restaurar cada turno basado en su cambio registrado
        List<TurnoDTO> turnosRestaurados = cambiosTurno.stream().map(cambio -> {
            Optional<Turnos> turnoOpt = turnosRepository.findById(cambio.getTurno().getIdTurno());
            Turnos turnoRestaurado;

            if (turnoOpt.isPresent()) {
                // Si el turno ya existe, actualizarlo con los datos de la versión guardada
                turnoRestaurado = turnoOpt.get();
            } else {
                // Si no existe, crear uno nuevo
                turnoRestaurado = new Turnos();
                turnoRestaurado.setUsuario(cambio.getUsuario());
            }

            // Asignar valores desde el cambio registrado
            turnoRestaurado.setFechaInicio(cambio.getFechaInicio());
            turnoRestaurado.setFechaFin(cambio.getFechaFin());
            turnoRestaurado.setEstadoTurno(cambio.getEstadoTurno());
            turnoRestaurado.setTotalHoras(cambio.getTotalHoras());
            turnoRestaurado.setVersion(version);

            // Guardar el turno restaurado en la base de datos
            Turnos turnoGuardado = turnosRepository.save(turnoRestaurado);

            // Convertir la entidad restaurada a DTO antes de retornarla
            return modelMapper.map(turnoGuardado, TurnoDTO.class);
        }).collect(Collectors.toList());

        return turnosRestaurados;
    }



    /**
     * Obtiene todos los turnos asociados a un cuadro de turno específico.
     *
     * @param idCuadroTurno ID del cuadro de turno
     * @return Lista de turnos en formato DTO
     * @throws RuntimeException si no se encuentra el cuadro de turno
     */
    public List<TurnoDTO> obtenerTurnosPorCuadro(Long idCuadroTurno) {
        // Verificar que el cuadro de turno existe
        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(idCuadroTurno)
                .orElseThrow(() -> new RuntimeException("Cuadro de turno no encontrado con ID: " + idCuadroTurno));

        // Obtener todos los turnos asociados al cuadro
        List<Turnos> turnos = turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno);

        // Convertir las entidades a DTOs
        return turnos.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene turnos por cuadro con filtros adicionales opcionales.
     *
     * @param idCuadroTurno ID del cuadro de turno
     * @param estado Estado del turno (opcional)
     * @param fechaDesde Fecha desde (opcional)
     * @param fechaHasta Fecha hasta (opcional)
     * @return Lista de turnos filtrados en formato DTO
     */
    public List<TurnoDTO> obtenerTurnosPorCuadroConFiltros(Long idCuadroTurno,
                                                           String estado,
                                                           LocalDate fechaDesde,
                                                           LocalDate fechaHasta) {
        // Verificar que el cuadro de turno existe
        cuadroTurnoRepository.findById(idCuadroTurno)
                .orElseThrow(() -> new RuntimeException("Cuadro de turno no encontrado con ID: " + idCuadroTurno));

        List<Turnos> turnos;

        // Aplicar filtros según los parámetros proporcionados
        if (estado != null && fechaDesde != null && fechaHasta != null) {
            // Filtrar por cuadro, estado y rango de fechas
            turnos = turnosRepository.findByCuadroTurnoAndEstadoAndFechaRange(
                    idCuadroTurno, estado, fechaDesde.atStartOfDay(), fechaHasta.atTime(23, 59, 59));
        } else if (estado != null) {
            // Filtrar solo por cuadro y estado
            turnos = turnosRepository.findByCuadroTurno_IdCuadroTurnoAndEstadoTurno(idCuadroTurno, estado);
        } else if (fechaDesde != null && fechaHasta != null) {
            // Filtrar por cuadro y rango de fechas
            turnos = turnosRepository.findByCuadroTurnoAndFechaRange(
                    idCuadroTurno, fechaDesde.atStartOfDay(), fechaHasta.atTime(23, 59, 59));
        } else {
            // Solo filtrar por cuadro de turno
            turnos = turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno);
        }

        // Convertir las entidades a DTOs
        return turnos.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

}

//    public Turnos create(Turnos turno) {
//        return crearTurno(turno);
//    }
//
//    public Turnos crearTurno(Turnos turno) {
//        turno.setTotalHoras(calcularHorasTrabajadas(turno.getFechaInicio(), turno.getFechaFin()));
//        turno.setVersion(generarVersion(turno.getFechaInicio()));
//        Turnos nuevoTurno = guardarTurno(turno);
//        registrarCambioTurno(nuevoTurno, "CREACION");
//        return nuevoTurno;
//    }

//    public Optional<Turnos> findById(Long idTurno) {
//        return turnosRepository.findById(idTurno);
//    }

//    public List<Turnos> findAll() {
//        return turnosRepository.findAll();
//    }

//    public List<CambiosTurno> obtenerCambiosPorTurno(Long idTurno) {
//        return CambiosTurnoRepository.findByTurnoOriginal_IdTurno(idTurno);
//    }

//    public Turnos guardarTurno(Turnos turnos) {
//        if (turnos.getUsuario() == null) {
//            throw new IllegalArgumentException("El usuario es obligatorio.");
//        }
//        if (turnos.getFechaInicio() == null || turnos.getFechaFin() == null) {
//            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas.");
//        }
//        if (turnos.getFechaInicio().isAfter(turnos.getFechaFin())) {
//            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
//        }
//
//        List<Turnos> turnosSolapados = turnosRepository.findTurnosSolapados(
//                turnos.getUsuario().getIdPersona(), turnos.getFechaInicio(), turnos.getFechaFin());
//        if (!turnosSolapados.isEmpty()) {
//            throw new IllegalArgumentException("Ya existe un turno en este horario para el usuario.");
//        }
//
//        turnos.setTotalHoras(calcularHorasTrabajadas(turnos.getFechaInicio(), turnos.getFechaFin()));
//        turnos.setVersion(generarVersion(turnos.getFechaInicio()));
//
//        return turnosRepository.save(turnos);
//    }

//    private void guardarCambioTurno(Turnos turno) {
//        CambiosTurno cambio = new CambiosTurno();
//        cambio.setTurnoOriginal(turno);
//        cambio.setUsuario(turno.getUsuario());
//        cambio.setFechaInicio(turno.getFechaInicio());
//        cambio.setFechaFin(turno.getFechaFin());
//        cambio.setTotalHoras(turno.getTotalHoras());
//        cambio.setTipoTurno(turno.getTipoTurno());
//        cambio.setEstadoTurno(turno.getEstadoTurno());
//        cambio.setJornada(turno.getJornada());
//        cambio.setComentarios(turno.getComentarios());
//        cambio.setFechaCambio(LocalDateTime.now()); // Se registra la fecha del cambio
//
//        cambiosTurnoRepository.save(cambio);
//    }

//    private void guardarCambioTurno(TurnoDTO turnoDTO) {
//        // Convertir el DTO a la entidad Turnos
//        Turnos turno = modelMapper.map(turnoDTO, Turnos.class);
//
//        // Crear la entidad de cambio de turno
//        CambiosTurno cambio = modelMapper.map(turno, CambiosTurno.class);
//
//        // Ajustar atributos adicionales
//        cambio.setTurnoOriginal(turno);
//        cambio.setFechaCambio(LocalDateTime.now()); // Registrar la fecha del cambio
//
//        // Guardar el cambio en la base de datos
//        cambiosTurnoRepository.save(cambio);
//    }

//    /**
//     * Obtiene todos los turnos actuales.
//     */
//    public List<Turnos> obtenerTurnos() {
//        return turnosRepository.findAll();
//    }
//
//    /**
//     * Obtiene el historial de cambios de un turno específico.
//     */
//    public List<CambiosTurno> obtenerHistorialTurno(Long idTurno) {
//        return cambiosTurnoRepository.findByTurnoOriginal_IdTurno(idTurno);
//    }

//    /**
//     * Registra un nuevo turno y guarda la versión anterior en el historial.
//     */
//    public Turnos actualizarTurno(Long id, Turnos turnoDetalles) {
//        Turnos turnoExistente = turnosRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
//
//        guardarCambioTurno(turnoExistente);
//
//        if (turnoDetalles.getFechaInicio() != null) {
//            turnoExistente.setFechaInicio(turnoDetalles.getFechaInicio());
//        }
//        if (turnoDetalles.getFechaFin() != null) {
//            turnoExistente.setFechaFin(turnoDetalles.getFechaFin());
//        }
//        if (turnoDetalles.getEstadoTurno() != null) {
//            turnoExistente.setEstadoTurno(turnoDetalles.getEstadoTurno());
//        }
//
//        turnoExistente.setTotalHoras(calcularHorasTrabajadas(
//                turnoExistente.getFechaInicio(), turnoExistente.getFechaFin()));
//        turnoExistente.setVersion(generarNuevaVersion(turnoExistente.getVersion()));
//
//        return turnosRepository.save(turnoExistente);
//    }

//    public void eliminarTurno(Long id) {
//        Turnos turnoEliminar = turnosRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
//
//        registrarCambioTurno(turnoEliminar, "ELIMINACION");
//        turnosRepository.deleteById(id);
//    }

//    private void registrarCambioTurno(Turnos turno, String tipoCambio) {
//        CambiosTurno cambio = new CambiosTurno();
//        cambio.setTurnoOriginal(turno);
//        cambio.setFechaCambio(LocalDateTime.now());
//        cambio.setUsuario(turno.getUsuario());
//        cambio.setFechaInicio(turno.getFechaInicio());
//        cambio.setFechaFin(turno.getFechaFin());
//        cambio.setTotalHoras(turno.getTotalHoras());
//        cambio.setEstadoTurno(turno.getEstadoTurno());
//        cambio.setJornada(turno.getJornada());
//        cambio.setTipoTurno(turno.getTipoTurno());
//        cambio.setComentarios(turno.getComentarios());
//        cambio.setVersion(turno.getVersion());
//        cambiosTurnoRepository.save(cambio);
//    }

//    public Turnos restaurarTurno(Long idCambio) {
//        CambiosTurno cambio = cambiosTurnoRepository.findById(idCambio)
//                .orElseThrow(() -> new EntityNotFoundException("Cambio de turno no encontrado"));
//
//        Optional<Turnos> turnoOpt = turnosRepository.findById(cambio.getTurnoOriginal().getIdTurno());
//
//        Turnos turnoRestaurado;
//        if (turnoOpt.isPresent()) {
//            turnoRestaurado = turnoOpt.get();
//        } else {
//            turnoRestaurado = new Turnos();
//            turnoRestaurado.setUsuario(cambio.getUsuario());
//        }
//
//        turnoRestaurado.setFechaInicio(cambio.getFechaInicio());
//        turnoRestaurado.setFechaFin(cambio.getFechaFin());
//        turnoRestaurado.setEstadoTurno(cambio.getEstadoTurno());
//        turnoRestaurado.setTotalHoras(cambio.getTotalHoras());
//        turnoRestaurado.setVersion(generarNuevaVersion(cambio.getVersion()));
//
//        return turnosRepository.save(turnoRestaurado);
//    }

//    public void cambiarEstadoDeTodosLosTurnos(String estadoActual, String nuevoEstado) {
//        List<Turnos> turnos = turnosRepository.findByEstadoTurno(estadoActual);
//
//        for (Turnos turno : turnos) {
//            turno.setEstadoTurno(nuevoEstado);
//        }
//
//        // Guarda los cambios en la base de datos
//        turnosRepository.saveAll(turnos);
//    }