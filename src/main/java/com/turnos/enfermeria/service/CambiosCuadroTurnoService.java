package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.CambiosCuadroTurnoDTO;
import com.turnos.enfermeria.model.dto.CuadroTurnoDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CambiosCuadroTurnoService {

    private final CambiosCuadroTurnoRepository cambiosCuadroTurnoRepository;
    private final CambiosProcesosAtencionRepository cambiosProcesosAtencionRepository;
    private final ModelMapper modelMapper;
    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final MacroprocesosRepository macroprocesosRepository;
    private final ProcesosRepository procesosRepository;
    private final ServicioRepository servicioRepository;
    private final EquipoRepository equipoRepository;
    private final SeccionesServicioRepository seccionesServicioRepository;
    private final SubseccionesServicioRepository subseccionesServicioRepository;
    private final ProcesosAtencionRepository procesosAtencionRepository;

    public CambiosCuadroTurnoDTO create(CambiosCuadroTurnoDTO cambiosCuadroTurnoDTO) {
        // Buscar entidades relacionadas usando métodos auxiliares
        CuadroTurno cuadroTurno = buscarCuadroTurno(cambiosCuadroTurnoDTO.getIdCuadroTurno());
        Macroprocesos macroprocesos = buscarMacroproceso(cambiosCuadroTurnoDTO.getIdMacroproceso());
        Procesos procesos = buscarProceso(cambiosCuadroTurnoDTO.getIdProcesos());
        Servicio servicio = buscarServicio(cambiosCuadroTurnoDTO.getIdServicios());
        SeccionesServicio seccionesServicio = buscarSeccionServicio(cambiosCuadroTurnoDTO.getIdSeccionesServicios());
        Equipo equipo = buscarEquipo(cambiosCuadroTurnoDTO.getIdEquipo());

        // Convertir DTO a Entidad
        CambiosCuadroTurno cambiosCuadroTurno = modelMapper.map(cambiosCuadroTurnoDTO, CambiosCuadroTurno.class);
        configurarCambiosCuadroTurno(cambiosCuadroTurno, cambiosCuadroTurnoDTO, cuadroTurno,
                macroprocesos, procesos, servicio, seccionesServicio, equipo);

        // Guardar en la base de datos
        CambiosCuadroTurno cambioGuardado = cambiosCuadroTurnoRepository.save(cambiosCuadroTurno);

        return modelMapper.map(cambioGuardado, CambiosCuadroTurnoDTO.class);
    }

    public CambiosCuadroTurnoDTO update(CambiosCuadroTurnoDTO cambiosCuadroTurnoDTO, Long id) {
        // Buscar entidades relacionadas usando métodos auxiliares
        CuadroTurno cuadroTurno = buscarCuadroTurno(cambiosCuadroTurnoDTO.getIdCuadroTurno());
        Macroprocesos macroprocesos = buscarMacroproceso(cambiosCuadroTurnoDTO.getIdMacroproceso());
        Procesos procesos = buscarProceso(cambiosCuadroTurnoDTO.getIdProcesos());
        Servicio servicio = buscarServicio(cambiosCuadroTurnoDTO.getIdServicios());
        SeccionesServicio seccionesServicio = buscarSeccionServicio(cambiosCuadroTurnoDTO.getIdSeccionesServicios());
        Equipo equipo = buscarEquipo(cambiosCuadroTurnoDTO.getIdEquipo());

        Optional<CambiosCuadroTurno> optionalCambio = cambiosCuadroTurnoRepository.findById(id);
        if (optionalCambio.isPresent()) {
            CambiosCuadroTurno cambioExistente = optionalCambio.get();

            actualizarCambioExistente(cambioExistente, cambiosCuadroTurnoDTO, cuadroTurno,
                    macroprocesos, procesos, servicio, seccionesServicio, equipo);

            CambiosCuadroTurno cambioActualizado = cambiosCuadroTurnoRepository.save(cambioExistente);
            return modelMapper.map(cambioActualizado, CambiosCuadroTurnoDTO.class);
        }
        throw new EntityNotFoundException("Cambio de CuadroTurno no encontrado");
    }

    public Optional<CambiosCuadroTurnoDTO> findById(Long idCambioCuadro) {
        return cambiosCuadroTurnoRepository.findById(idCambioCuadro)
                .map(cambiosCuadroTurno -> modelMapper.map(cambiosCuadroTurno, CambiosCuadroTurnoDTO.class));
    }

    public List<CambiosCuadroTurnoDTO> findAll() {
        return cambiosCuadroTurnoRepository.findAll()
                .stream()
                .map(cambiosCuadroTurno -> modelMapper.map(cambiosCuadroTurno, CambiosCuadroTurnoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idCambioCuadro) {
        Optional<CambiosCuadroTurno> optionalCambio = cambiosCuadroTurnoRepository.findById(idCambioCuadro);

        if (optionalCambio.isPresent()) {
            cambiosCuadroTurnoRepository.deleteById(idCambioCuadro);
        } else {
            throw new EntityNotFoundException("Cambio de CuadroTurno no encontrado");
        }
    }

    public void registrarCambioCuadroTurno(CuadroTurnoDTO cuadroTurnoDTO, String tipoCambio) {
        // Buscar entidades relacionadas usando métodos auxiliares
        Macroprocesos macroprocesos = buscarMacroproceso(cuadroTurnoDTO.getIdMacroproceso());
        Procesos procesos = buscarProceso(cuadroTurnoDTO.getIdProceso());
        Servicio servicio = buscarServicio(cuadroTurnoDTO.getIdServicio());
        Equipo equipo = buscarEquipo(cuadroTurnoDTO.getIdEquipo());
        SeccionesServicio seccionesServicio = buscarSeccionServicio(cuadroTurnoDTO.getIdSeccionesServicios());
        SubseccionesServicio subseccionesServicio = buscarSubseccionServicio(cuadroTurnoDTO.getIdSubseccionServicio());

        // Convertir DTO a Entidad
        CambiosCuadroTurno cambio = modelMapper.map(cuadroTurnoDTO, CambiosCuadroTurno.class);

        // Obtener cuadro turno original
        CuadroTurno cuadroTurno = buscarCuadroTurno(cuadroTurnoDTO.getIdCuadroTurno());

        // Configurar el cambio
        configurarCambioHistorial(cambio, cuadroTurnoDTO, cuadroTurno, macroprocesos,
                procesos, servicio, equipo, seccionesServicio, subseccionesServicio);

        cambiosCuadroTurnoRepository.save(cambio);
    }

    @Transactional
    public void registrarCambioProcesosAtencion(ProcesosAtencion procesosAtencion, String tipoCambio) {
        CambiosProcesosAtencion cambio = new CambiosProcesosAtencion();

        cambio.setCuadroTurno(procesosAtencion.getCuadroTurno());
        cambio.setProcesos(procesosAtencion.getProcesos());
        cambio.setEstado(procesosAtencion.getEstado());
        cambio.setDetalle(procesosAtencion.getDetalle());
        cambio.setFechaCambio(LocalDateTime.now());

        cambiosProcesosAtencionRepository.save(cambio);
    }

    // ===== MÉTODOS AUXILIARES =====

    private CuadroTurno buscarCuadroTurno(Long id) {
        return id != null ? cuadroTurnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CuadroTurno no encontrado.")) : null;
    }

    private Macroprocesos buscarMacroproceso(Long id) {
        return id != null ? macroprocesosRepository.findById(id)
                .orElse(null) : null;
    }

    private Procesos buscarProceso(Long id) {
        return id != null ? procesosRepository.findById(id)
                .orElse(null) : null;
    }

    private Servicio buscarServicio(Long id) {
        return id != null ? servicioRepository.findById(id)
                .orElse(null) : null;
    }

    private SeccionesServicio buscarSeccionServicio(Long id) {
        return id != null ? seccionesServicioRepository.findById(id)
                .orElse(null) : null;
    }

    private SubseccionesServicio buscarSubseccionServicio(Long id) {
        return id != null ? subseccionesServicioRepository.findById(id)
                .orElse(null) : null;
    }

    private Equipo buscarEquipo(Long id) {
        return id != null ? equipoRepository.findById(id)
                .orElse(null) : null;
    }

    private void configurarCambiosCuadroTurno(CambiosCuadroTurno cambio, CambiosCuadroTurnoDTO dto,
                                              CuadroTurno cuadroTurno, Macroprocesos macro, Procesos proceso,
                                              Servicio servicio, SeccionesServicio seccion, Equipo equipo) {
        cambio.setCuadroTurno(cuadroTurno);
        cambio.setMacroProcesos(macro);
        cambio.setProcesos(proceso);
        cambio.setServicios(servicio);
        cambio.setSeccionesServicios(seccion);
        cambio.setEquipos(equipo);
        cambio.setFechaCambio(dto.getFechaCambio() != null ? dto.getFechaCambio() : LocalDateTime.now());
        cambio.setNombre(dto.getNombre());
        cambio.setMes(dto.getMes());
        cambio.setAnio(dto.getAnio());
        cambio.setEstadoCuadro(dto.getEstadoCuadro());
        cambio.setVersion(dto.getVersion());
        cambio.setTurnoExcepcion(dto.getTurnoExcepcion());
        cambio.setCategoria(dto.getCategoria());
        cambio.setEstado(dto.getEstado());
    }

    private void actualizarCambioExistente(CambiosCuadroTurno cambioExistente, CambiosCuadroTurnoDTO dto,
                                           CuadroTurno cuadroTurno, Macroprocesos macro, Procesos proceso,
                                           Servicio servicio, SeccionesServicio seccion, Equipo equipo) {
        if (dto.getIdCuadroTurno() != null) cambioExistente.setCuadroTurno(cuadroTurno);
        if (dto.getIdMacroproceso() != null) cambioExistente.setMacroProcesos(macro);
        if (dto.getIdProcesos() != null) cambioExistente.setProcesos(proceso);
        if (dto.getIdServicios() != null) cambioExistente.setServicios(servicio);
        if (dto.getIdSeccionesServicios() != null) cambioExistente.setSeccionesServicios(seccion);
        if (dto.getIdEquipo() != null) cambioExistente.setEquipos(equipo);
        if (dto.getFechaCambio() != null) cambioExistente.setFechaCambio(dto.getFechaCambio());
        if (dto.getNombre() != null) cambioExistente.setNombre(dto.getNombre());
        if (dto.getMes() != null) cambioExistente.setMes(dto.getMes());
        if (dto.getAnio() != null) cambioExistente.setAnio(dto.getAnio());
        if (dto.getEstadoCuadro() != null) cambioExistente.setEstadoCuadro(dto.getEstadoCuadro());
        if (dto.getVersion() != null) cambioExistente.setVersion(dto.getVersion());
        if (dto.getTurnoExcepcion() != null) cambioExistente.setTurnoExcepcion(dto.getTurnoExcepcion());
        if (dto.getCategoria() != null) cambioExistente.setCategoria(dto.getCategoria());
        if (dto.getEstado() != null) cambioExistente.setEstado(dto.getEstado());
    }

    private void configurarCambioHistorial(CambiosCuadroTurno cambio, CuadroTurnoDTO dto, CuadroTurno cuadroTurno,
                                           Macroprocesos macro, Procesos proceso, Servicio servicio, Equipo equipo,
                                           SeccionesServicio seccion, SubseccionesServicio subseccion) {
        cambio.setCuadroTurno(cuadroTurno);
        cambio.setFechaCambio(LocalDateTime.now());
        cambio.setNombre(dto.getNombre());
        cambio.setMes(dto.getMes());
        cambio.setAnio(dto.getAnio());
        cambio.setTurnoExcepcion(dto.getTurnoExcepcion());
        cambio.setEstadoCuadro(dto.getEstadoCuadro());
        cambio.setVersion(dto.getVersion());
        cambio.setMacroProcesos(macro);
        cambio.setProcesos(proceso);
        cambio.setServicios(servicio);
        cambio.setEquipos(equipo);
        cambio.setSeccionesServicios(seccion);
        cambio.setSubseccionesServicios(subseccion);
        cambio.setCategoria(dto.getCategoria());
        cambio.setEstado(dto.getEstado());
    }
}
