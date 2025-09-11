package com.turnos.enfermeria.service;

import com.turnos.enfermeria.exception.custom.TurnoValidationException;
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
        // Buscar entidades relacionadas usando métodos auxiliares
        Usuario usuario = buscarUsuario(turnoDTO.getIdPersona());
        CuadroTurno cuadroTurno = buscarCuadroTurno(turnoDTO.getIdCuadroTurno());

        // Validaciones de negocio
        validarDatosTurno(turnoDTO);
        validarSolapamientoTurnos(turnoDTO);
        validarReglasNegocio(turnoDTO, usuario, cuadroTurno);

        // Crear y configurar el turno
        Turnos turno = modelMapper.map(turnoDTO, Turnos.class);
        configurarTurno(turno, turnoDTO, usuario, cuadroTurno);

        // Guardar turno
        Turnos turnoGuardado = turnosRepository.save(turno);

        // Registrar cambio en historial
        TurnoDTO turnoGuardadoDTO = modelMapper.map(turnoGuardado, TurnoDTO.class);
        registrarCambioTurno(turnoGuardado.getIdTurno(), turnoGuardadoDTO, "CREACION");

        return turnoGuardadoDTO;
    }

    public TurnoDTO actualizarTurno(Long id, TurnoDTO turnoDetallesDTO) {
        // Buscar turno existente
        Turnos turnoExistente = buscarTurno(id);

        // Guardar estado anterior en historial
        TurnoDTO estadoAnterior = modelMapper.map(turnoExistente, TurnoDTO.class);
        registrarCambioTurno(id, estadoAnterior, "ACTUALIZACION_ANTERIOR");

        // Actualizar campos
        actualizarCamposTurno(turnoExistente, turnoDetallesDTO);

        // Recalcular valores derivados
        turnoExistente.setTotalHoras(calcularHorasTrabajadas(
                turnoExistente.getFechaInicio(), turnoExistente.getFechaFin()));
        turnoExistente.setVersion(generarNuevaVersion(turnoExistente.getVersion()));

        // Guardar cambios
        Turnos turnoActualizado = turnosRepository.save(turnoExistente);

        // Registrar nuevo estado en historial
        TurnoDTO estadoNuevo = modelMapper.map(turnoActualizado, TurnoDTO.class);
        registrarCambioTurno(id, estadoNuevo, "ACTUALIZACION");

        return estadoNuevo;
    }

    public void eliminarTurno(Long id) {
        // Buscar turno
        Turnos turnoEliminar = buscarTurno(id);

        // Registrar en historial antes de eliminar
        TurnoDTO turnoDTO = modelMapper.map(turnoEliminar, TurnoDTO.class);
        registrarCambioTurno(id, turnoDTO, "ELIMINACION");

        // Eliminar turno
        turnosRepository.deleteById(id);
    }

    public Optional<TurnoDTO> findById(Long idTurno) {
        return turnosRepository.findById(idTurno)
                .map(turno -> modelMapper.map(turno, TurnoDTO.class));
    }

    public List<TurnoDTO> findAll() {
        return turnosRepository.findAll()
                .stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    public List<TurnoDTO> obtenerTurnos() {
        return findAll();
    }

    public List<CambiosTurnoDTO> obtenerHistorialTurno(Long idTurno) {
        List<CambiosTurno> cambios = cambiosTurnoRepository.findByTurno_IdTurno(idTurno);
        return cambios.stream()
                .map(cambio -> modelMapper.map(cambio, CambiosTurnoDTO.class))
                .collect(Collectors.toList());
    }

    public List<CambiosTurnoDTO> obtenerCambiosPorTurno(Long idTurno) {
        return cambiosTurnoRepository.findCambiosTurnoDTOByTurno(idTurno);
    }

    public TurnoDTO restaurarTurno(Long idCambio) {
        // Buscar cambio en historial
        CambiosTurno cambio = buscarCambioTurno(idCambio);

        // Determinar si restaurar turno existente o crear nuevo
        Optional<Turnos> turnoOpt = turnosRepository.findById(cambio.getTurno().getIdTurno());
        Turnos turnoRestaurado = turnoOpt.orElse(new Turnos());

        // Restaurar datos desde el cambio
        restaurarDatosDesdeHistorial(turnoRestaurado, cambio);

        // Guardar turno restaurado
        Turnos turnoGuardado = turnosRepository.save(turnoRestaurado);

        return modelMapper.map(turnoGuardado, TurnoDTO.class);
    }

    @Transactional
    public List<TurnoDTO> restaurarTurnosPorVersion(String version) {
        List<CambiosTurno> cambiosTurno = cambiosTurnoRepository.findByVersion(version);

        if (cambiosTurno.isEmpty()) {
            throw new RuntimeException("No se encontraron turnos con la versión " + version);
        }

        return cambiosTurno.stream()
                .map(this::restaurarTurnoDesdeHistorial)
                .collect(Collectors.toList());
    }

    public List<TurnoDTO> cambiarEstadoDeTodosLosTurnos(String estadoActual, String nuevoEstado) {
        List<Turnos> turnos = turnosRepository.findByEstadoTurno(estadoActual);

        turnos.forEach(turno -> turno.setEstadoTurno(nuevoEstado));
        List<Turnos> turnosActualizados = turnosRepository.saveAll(turnos);

        return turnosActualizados.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    public List<TurnoDTO> obtenerTurnosPorCuadro(Long idCuadroTurno) {
        // Verificar que el cuadro existe
        buscarCuadroTurno(idCuadroTurno);

        List<Turnos> turnos = turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno);
        return turnos.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    public List<TurnoDTO> obtenerTurnosPorCuadroConFiltros(Long idCuadroTurno, String estado,
                                                           LocalDate fechaDesde, LocalDate fechaHasta) {
        // Verificar que el cuadro existe
        buscarCuadroTurno(idCuadroTurno);

        List<Turnos> turnos = aplicarFiltrosTurnos(idCuadroTurno, estado, fechaDesde, fechaHasta);

        return turnos.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    // ===== MÉTODOS AUXILIARES DE BÚSQUEDA =====

    private Usuario buscarUsuario(Long id) {
        return id != null ? usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado.")) : null;
    }

    private CuadroTurno buscarCuadroTurno(Long id) {
        return id != null ? cuadroTurnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cuadro de turno no encontrado.")) : null;
    }

    private Turnos buscarTurno(Long id) {
        return turnosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
    }

    private CambiosTurno buscarCambioTurno(Long id) {
        return cambiosTurnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cambio de turno no encontrado"));
    }

    // ===== MÉTODOS AUXILIARES DE VALIDACIÓN =====

    private void validarDatosTurno(TurnoDTO turnoDTO) {
        if (turnoDTO.getFechaInicio() == null || turnoDTO.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas.");
        }
        if (turnoDTO.getFechaInicio().isAfter(turnoDTO.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
    }

    private void validarSolapamientoTurnos(TurnoDTO turnoDTO) {
        List<Turnos> turnosSolapados = turnosRepository.findTurnosSolapados(
                turnoDTO.getIdPersona(), turnoDTO.getFechaInicio(), turnoDTO.getFechaFin());
        if (!turnosSolapados.isEmpty()) {
            throw new TurnoValidationException(
                    String.format("Ya existe un turno en este horario para el usuario. " +
                                    "Conflicto desde %s hasta %s.",
                            turnoDTO.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            turnoDTO.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                    "POST", "/turnos"
            );
        }
    }

    private void validarReglasNegocio(TurnoDTO turnoDTO, Usuario usuario, CuadroTurno cuadroTurno) {
        Long usuarioId = usuario.getIdPersona();
        LocalDate fechaTurno = turnoDTO.getFechaInicio().toLocalDate();
        long horasTurno = calcularHorasTrabajadas(turnoDTO.getFechaInicio(), turnoDTO.getFechaFin());

        // Validar horas mensuales
        validarHorasMensuales(usuarioId, fechaTurno, horasTurno);

        // Validar turnos de 24 horas y límites diarios
        validarLimitesHorarios(usuarioId, fechaTurno, horasTurno, cuadroTurno);
    }

    private void validarHorasMensuales(Long usuarioId, LocalDate fechaTurno, long horasTurno) {
        int horasMensuales = turnosRepository.obtenerHorasMensuales(
                usuarioId,
                String.valueOf(fechaTurno.getMonthValue()),
                String.valueOf(fechaTurno.getYear())
        );
        if (horasMensuales + horasTurno > 280) {
            throw new TurnoValidationException(
                    String.format("No se pueden superar las 280 horas mensuales. " +
                                    "Horas actuales del mes: %d, horas del nuevo turno: %d, total: %d horas. " +
                                    "Máximo permitido: 280 horas.",
                            horasMensuales, horasTurno, horasMensuales + horasTurno),
                    "POST", "/turnos"
            );
        }
    }

    private void validarLimitesHorarios(Long usuarioId, LocalDate fechaTurno, long horasTurno, CuadroTurno cuadroTurno) {
        List<Turnos> turnosDelDia = turnosRepository.obtenerTurnosPorFecha(usuarioId, fechaTurno);
        long horasDiariasTotales = turnosDelDia.stream().mapToLong(Turnos::getTotalHoras).sum() + horasTurno;

        if (horasTurno == 24) {
            if (!cuadroTurno.getTurnoExcepcion()) {
                throw new TurnoValidationException(
                        String.format("El usuario no puede ser asignado a turnos de 24 horas. " +
                                "El cuadro '%s' no permite turnos de excepción.", cuadroTurno.getNombre()),
                        "POST", "/turnos"
                );
            }
        } else {
            if (horasTurno > 12) {
                throw new TurnoValidationException(
                        String.format("No se pueden asignar más de 12 horas consecutivas. " +
                                "Horas solicitadas: %d horas. Máximo permitido: 12 horas.", horasTurno),
                        "POST", "/turnos"
                );
            }
            if (horasDiariasTotales > 18) {
                throw new TurnoValidationException(
                        String.format("No se pueden superar las 18 horas diarias. " +
                                        "Horas actuales del día: %d, horas del nuevo turno: %d, total: %d horas. " +
                                        "Máximo permitido: 18 horas.",
                                horasDiariasTotales - horasTurno, horasTurno, horasDiariasTotales),
                        "POST", "/turnos"
                );
            }
        }
    }

    // ===== MÉTODOS AUXILIARES DE CONFIGURACIÓN =====

    private void configurarTurno(Turnos turno, TurnoDTO dto, Usuario usuario, CuadroTurno cuadroTurno) {
        turno.setUsuario(usuario);
        turno.setCuadroTurno(cuadroTurno);
        turno.setTotalHoras(calcularHorasTrabajadas(dto.getFechaInicio(), dto.getFechaFin()));
        turno.setVersion(cuadroTurno.getVersion());
        turno.setComentarios(dto.getComentarios());
        turno.setTipoTurno(dto.getTipoTurno() != null ? dto.getTipoTurno() : "Presencial");
        turno.setJornada(calcularJornadaAutomatica(dto.getFechaInicio(), dto.getFechaFin()));
        turno.setEstadoTurno(dto.getEstadoTurno() != null ? dto.getEstadoTurno() : "abierto");
        if (dto.getJornada() != null && !dto.getJornada().trim().isEmpty()) {
            turno.setJornada(dto.getJornada());
            System.out.println("⚠️ Jornada manual aplicada: " + dto.getJornada());
        }
    }

    /**
     * Registra todos los turnos existentes en historial al cambiar versión del cuadro
     */
    public void registrarTurnosEnHistorialAlCambiarVersion(Long idCuadroTurno, String versionAnterior, String nuevaVersion, String nuevoEstadoTurnos) {
        try {
            // Obtener todos los turnos del cuadro
            List<Turnos> turnos = turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno);

            for (Turnos turno : turnos) {
                // Crear registro en historial con versión anterior (mantener estado actual)
                CambiosTurno cambioVersionAnterior = new CambiosTurno();
                cambioVersionAnterior.setTurno(turno);
                cambioVersionAnterior.setCuadroTurno(turno.getCuadroTurno());
                cambioVersionAnterior.setUsuario(turno.getUsuario());
                cambioVersionAnterior.setFechaCambio(LocalDateTime.now());
                cambioVersionAnterior.setFechaInicio(turno.getFechaInicio());
                cambioVersionAnterior.setFechaFin(turno.getFechaFin());
                cambioVersionAnterior.setTotalHoras(turno.getTotalHoras());
                cambioVersionAnterior.setEstadoTurno(turno.getEstadoTurno()); // Estado actual
                cambioVersionAnterior.setVersion(versionAnterior);
                cambioVersionAnterior.setComentarios(turno.getComentarios());
                cambioVersionAnterior.setTipoTurno(turno.getTipoTurno() != null ? turno.getTipoTurno() : "Presencial");
                cambioVersionAnterior.setJornada(turno.getJornada() != null ? turno.getJornada() :
                        calcularJornadaAutomatica(turno.getFechaInicio(), turno.getFechaFin()));

                cambiosTurnoRepository.save(cambioVersionAnterior);
                // ACTUALIZAR TURNO ACTIVO CON NUEVA VERSIÓN Y NUEVO ESTADO
                turno.setVersion(nuevaVersion);
                turno.setEstadoTurno(nuevoEstadoTurnos);
                // Crear registro en historial con nueva versión Y NUEVO ESTADO
                CambiosTurno cambioVersionNueva = new CambiosTurno();
                cambioVersionNueva.setTurno(turno);
                cambioVersionNueva.setCuadroTurno(turno.getCuadroTurno());
                cambioVersionNueva.setUsuario(turno.getUsuario());
                cambioVersionNueva.setFechaCambio(LocalDateTime.now());
                cambioVersionNueva.setFechaInicio(turno.getFechaInicio());
                cambioVersionNueva.setFechaFin(turno.getFechaFin());
                cambioVersionNueva.setTotalHoras(turno.getTotalHoras());
                cambioVersionNueva.setEstadoTurno(nuevoEstadoTurnos); // ← NUEVO ESTADO
                cambioVersionNueva.setVersion(nuevaVersion);
                cambioVersionNueva.setComentarios(turno.getComentarios());
                cambioVersionNueva.setTipoTurno(turno.getTipoTurno() != null ? turno.getTipoTurno() : "Presencial");
                cambioVersionNueva.setJornada(turno.getJornada() != null ? turno.getJornada() :
                        calcularJornadaAutomatica(turno.getFechaInicio(), turno.getFechaFin()));

                cambiosTurnoRepository.save(cambioVersionNueva);
            }

            // Guardar turnos actualizados
            turnosRepository.saveAll(turnos);

            System.out.println(String.format("✅ Registrados %d turnos en historial: %s -> %s (estado: %s)",
                    turnos.size(), versionAnterior, nuevaVersion, nuevoEstadoTurnos));

        } catch (Exception e) {
            System.err.println("Error al registrar turnos en historial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarCamposTurno(Turnos turnoExistente, TurnoDTO turnoDetallesDTO) {
        boolean fechasCambiaron = false;
        if (turnoDetallesDTO.getFechaInicio() != null) {
            turnoExistente.setFechaInicio(turnoDetallesDTO.getFechaInicio());
            fechasCambiaron = true;
        }
        if (turnoDetallesDTO.getFechaFin() != null) {
            turnoExistente.setFechaFin(turnoDetallesDTO.getFechaFin());
            fechasCambiaron = true;
        }
        if (turnoDetallesDTO.getEstadoTurno() != null) {
            turnoExistente.setEstadoTurno(turnoDetallesDTO.getEstadoTurno());
        }
        if (turnoDetallesDTO.getComentarios() != null) {
            turnoExistente.setComentarios(turnoDetallesDTO.getComentarios());
        }
        // ✅ RECALCULAR JORNADA SI LAS FECHAS CAMBIARON
        if (fechasCambiaron) {
            String nuevaJornada = calcularJornadaAutomatica(
                    turnoExistente.getFechaInicio(),
                    turnoExistente.getFechaFin()
            );
            turnoExistente.setJornada(nuevaJornada);
            System.out.println("✅ Jornada recalculada automáticamente: " + nuevaJornada);
        }
    }

    private void restaurarDatosDesdeHistorial(Turnos turno, CambiosTurno cambio) {
        turno.setUsuario(cambio.getUsuario());
        turno.setFechaInicio(cambio.getFechaInicio());
        turno.setFechaFin(cambio.getFechaFin());
        turno.setEstadoTurno(cambio.getEstadoTurno());
        turno.setTotalHoras(cambio.getTotalHoras());
        turno.setVersion(generarNuevaVersion(cambio.getVersion()));
        turno.setComentarios(cambio.getComentarios());
    }

    private TurnoDTO restaurarTurnoDesdeHistorial(CambiosTurno cambio) {
        Optional<Turnos> turnoOpt = turnosRepository.findById(cambio.getTurno().getIdTurno());
        Turnos turnoRestaurado = turnoOpt.orElse(new Turnos());

        restaurarDatosDesdeHistorial(turnoRestaurado, cambio);
        turnoRestaurado.setVersion(cambio.getVersion()); // Mantener versión original

        Turnos turnoGuardado = turnosRepository.save(turnoRestaurado);
        return modelMapper.map(turnoGuardado, TurnoDTO.class);
    }

    private List<Turnos> aplicarFiltrosTurnos(Long idCuadroTurno, String estado,
                                              LocalDate fechaDesde, LocalDate fechaHasta) {
        if (estado != null && fechaDesde != null && fechaHasta != null) {
            return turnosRepository.findByCuadroTurnoAndEstadoAndFechaRange(
                    idCuadroTurno, estado, fechaDesde.atStartOfDay(), fechaHasta.atTime(23, 59, 59));
        } else if (estado != null) {
            return turnosRepository.findByCuadroTurno_IdCuadroTurnoAndEstadoTurno(idCuadroTurno, estado);
        } else if (fechaDesde != null && fechaHasta != null) {
            return turnosRepository.findByCuadroTurnoAndFechaRange(
                    idCuadroTurno, fechaDesde.atStartOfDay(), fechaHasta.atTime(23, 59, 59));
        } else {
            return turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno);
        }
    }
    // ===== MÉTODOS AUXILIARES DE REGISTRO =====
    private void registrarCambioTurno(Long idTurno, TurnoDTO turnoDTO, String tipoCambio) {
        Turnos turnoOriginal = buscarTurno(idTurno);
        CuadroTurno cuadroTurno = turnoOriginal.getCuadroTurno();
        CambiosTurno cambio = modelMapper.map(turnoDTO, CambiosTurno.class);
        cambio.setTurno(turnoOriginal);
        cambio.setFechaCambio(LocalDateTime.now());
        cambio.setUsuario(turnoOriginal.getUsuario());
        cambio.setCuadroTurno(cuadroTurno);
        cambio.setVersion(cuadroTurno.getVersion());
        cambio.setTipoTurno(turnoOriginal.getTipoTurno() != null ? turnoOriginal.getTipoTurno() : "Presencial");
        cambio.setJornada(turnoOriginal.getJornada() != null ? turnoOriginal.getJornada() :
                calcularJornadaAutomatica(turnoOriginal.getFechaInicio(), turnoOriginal.getFechaFin()));

        cambiosTurnoRepository.save(cambio);
    }

    // ===== MÉTODOS AUXILIARES DE CÁLCULO =====

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
        if (versionAnterior == null || !versionAnterior.contains("_v")) {
            return generarVersion(LocalDateTime.now());
        }

        String[] partes = versionAnterior.split("_v");
        if (partes.length == 2) {
            try {
                String baseVersion = partes[0];
                int numeroVersion = Integer.parseInt(partes[1]);
                return baseVersion + "_v" + (numeroVersion + 1);
            } catch (NumberFormatException e) {
                return partes[0] + "_v2";
            }
        }
        return versionAnterior + "_v2";
    }

    /**
     * Calcula la jornada automáticamente basada en la hora de inicio del turno
     */
    private String calcularJornadaAutomatica(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return "Tarde"; // Por defecto
        }

        // Calcular duración del turno
        long horasDuracion = Duration.between(fechaInicio, fechaFin).toHours();

        // Si es turno de 24 horas
        if (horasDuracion == 24) {
            return "24 Horas";
        }

        // Obtener hora de inicio (0-23)
        int horaInicio = fechaInicio.getHour();

        // Definir jornadas según hora de inicio
        if (horaInicio >= 6 && horaInicio < 12) {
            return "Mañana";
        } else if (horaInicio >= 12 && horaInicio < 18) {
            return "Tarde";
        } else {
            // 22:00 - 05:59 (incluye madrugada)
            return "Noche";
        }
    }
}
