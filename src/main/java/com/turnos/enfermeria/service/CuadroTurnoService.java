package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.CambiosCuadroTurnoDTO;
import com.turnos.enfermeria.model.dto.CuadroTurnoDTO;
import com.turnos.enfermeria.model.dto.TurnoDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
public class CuadroTurnoService {

    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final TurnosRepository turnosRepository;
    private final MacroprocesosRepository macroprocesosRepository;
    private final ProcesosRepository procesosRepository;
    private final ServicioRepository servicioRepository;
    private final EquipoRepository equipoRepository;
    private final SeccionesServicioRepository seccionesServicioRepository;
    private final ProcesosAtencionRepository procesosAtencionRepository;
    private final CambiosCuadroTurnoRepository cambiosCuadroTurnoRepository;
    private final CambiosCuadroTurnoService cambiosCuadroTurnoService;
    private final ModelMapper modelMapper;

    @Transactional
    public CuadroTurnoDTO crearCuadroTurno(CuadroTurnoDTO cuadroTurnoDTO) {
        Macroprocesos macroprocesos = null;
        if (cuadroTurnoDTO.getIdMacroproceso() != null) {
            macroprocesos = macroprocesosRepository.findById(cuadroTurnoDTO.getIdMacroproceso())
                    .orElseThrow(() -> new RuntimeException("macroproceso no encontrado."));
        }
        Procesos procesos = null;
        if (cuadroTurnoDTO.getIdProceso() != null) {
            procesos = procesosRepository.findById(cuadroTurnoDTO.getIdProceso())
                    .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));
        }
        Servicio servicio = null;
        if (cuadroTurnoDTO.getIdServicios() != null) {
            servicio = servicioRepository.findById(cuadroTurnoDTO.getIdServicios())
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));
        }
        SeccionesServicio seccionesServicio = null;
        if (cuadroTurnoDTO.getIdSeccionesServicios() != null) {
            seccionesServicio = seccionesServicioRepository.findById(cuadroTurnoDTO.getIdSeccionesServicios())
                    .orElseThrow(() -> new RuntimeException("Seccion Servicio no encontrada."));
        }
        ProcesosAtencion procesosAtencion = null;
        if (cuadroTurnoDTO.getIdProcesosAtencion() != null) {
            procesosAtencion = procesosAtencionRepository.findById(cuadroTurnoDTO.getIdProcesosAtencion())
                    .orElseThrow(() -> new RuntimeException("Proceso Atencion no encontrado."));
        }
        Equipo equipo = null;
        if(cuadroTurnoDTO.getIdEquipo() != null){
            equipo = equipoRepository.findById(cuadroTurnoDTO.getIdEquipo())
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado."));
        }
        // Convertir DTO a Entidad
        CuadroTurno cuadroTurno = modelMapper.map(cuadroTurnoDTO, CuadroTurno.class);

        cuadroTurno.setAnio(cuadroTurnoDTO.getAnio());
        cuadroTurno.setEstadoCuadro(cuadroTurnoDTO.getEstadoCuadro());
        cuadroTurno.setMes(cuadroTurnoDTO.getMes());
        cuadroTurno.setNombre(cuadroTurnoDTO.getNombre());
        cuadroTurno.setVersion(generarNuevaVersion(cuadroTurnoDTO.getVersion(), cuadroTurnoDTO.getAnio(), cuadroTurnoDTO.getMes()));
        cuadroTurno.setMacroProcesos(macroprocesos);
        cuadroTurno.setProcesos(procesos);
        cuadroTurno.setServicios(servicio);
        cuadroTurno.setEquipos(equipo);
        cuadroTurno.setProcesosAtencion(procesosAtencion);
        cuadroTurno.setSeccionesServicios(seccionesServicio);
        cuadroTurno.setTurnoExcepcion(cuadroTurnoDTO.getTurnoExcepcion());

        // Guardar en la base de datos
        CuadroTurno nuevoCuadro = cuadroTurnoRepository.save(cuadroTurno);

        // Registrar cambio
        cambiosCuadroTurnoService.registrarCambioCuadroTurno(nuevoCuadro, "CREACION");

        // Convertir Entidad a DTO y devolverlo
        return modelMapper.map(nuevoCuadro, CuadroTurnoDTO.class);
    }

    public List<CuadroTurnoDTO> obtenerCuadrosTurno() {
        List<CuadroTurno> cuadros = cuadroTurnoRepository.findAll();

        // Convertir la lista de entidades a DTOs usando ModelMapper
        return cuadros.stream()
                .map(cuadro -> modelMapper.map(cuadro, CuadroTurnoDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<CuadroTurnoDTO> findById(Long idCuadroTurno) {
        return cuadroTurnoRepository.findById(idCuadroTurno)
                .map(cuadroTurno -> modelMapper.map(cuadroTurno, CuadroTurnoDTO.class)); // Convertir a DTO
    }

    public List<CambiosCuadroTurnoDTO> obtenerHistorialCuadroTurno(Long id) {
        List<CambiosCuadroTurno> historial = cambiosCuadroTurnoRepository.findByCuadroTurno_IdCuadroTurno(id);

        return historial.stream()
                .map(cambio -> {
                    CambiosCuadroTurnoDTO dto = modelMapper.map(cambio, CambiosCuadroTurnoDTO.class);
                    dto.setIdCuadroTurno(cambio.getCuadroTurno().getIdCuadroTurno()); // Asignar ID manualmente
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public CuadroTurnoDTO actualizarCuadroTurno(Long id, CuadroTurnoDTO cuadroTurnoDTO) {

        Macroprocesos macroprocesos = null;
        if (cuadroTurnoDTO.getIdMacroproceso() != null) {
            macroprocesos = macroprocesosRepository.findById(cuadroTurnoDTO.getIdMacroproceso())
                    .orElseThrow(() -> new RuntimeException("macroproceso no encontrado."));
        }
        Procesos procesos = null;
        if (cuadroTurnoDTO.getIdProceso() != null) {
            procesos = procesosRepository.findById(cuadroTurnoDTO.getIdProceso())
                    .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));
        }
        Servicio servicio = null;
        if (cuadroTurnoDTO.getIdServicios() != null) {
            servicio = servicioRepository.findById(cuadroTurnoDTO.getIdServicios())
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));
        }
        SeccionesServicio seccionesServicio = null;
        if (cuadroTurnoDTO.getIdSeccionesServicios() != null) {
            seccionesServicio = seccionesServicioRepository.findById(cuadroTurnoDTO.getIdSeccionesServicios())
                    .orElseThrow(() -> new RuntimeException("Seccion Servicio no encontrada."));
        }
        ProcesosAtencion procesosAtencion = null;
        if (cuadroTurnoDTO.getIdProcesosAtencion() != null) {
            procesosAtencion = procesosAtencionRepository.findById(cuadroTurnoDTO.getIdProcesosAtencion())
                    .orElseThrow(() -> new RuntimeException("Proceso Atencion no encontrado."));
        }
        Equipo equipo = null;
        if(cuadroTurnoDTO.getIdEquipo() != null){
            equipo = equipoRepository.findById(cuadroTurnoDTO.getIdEquipo())
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado."));
        }
        Optional<CuadroTurno> optionalCuadro = cuadroTurnoRepository.findById(id);
        if (optionalCuadro.isPresent()) {
            CuadroTurno cuadroExistente = optionalCuadro.get();
            // Guardamos solo los datos relevantes en un objeto de historial
            cambiosCuadroTurnoService.registrarCambioCuadroTurno(cuadroExistente, "ACTUALIZACION");
            // Mapear los datos desde el DTO al objeto existente
            cuadroExistente.setNombre(cuadroTurnoDTO.getNombre());
            cuadroExistente.setAnio(cuadroTurnoDTO.getAnio());
            cuadroExistente.setMes(cuadroTurnoDTO.getMes());
            cuadroExistente.setTurnoExcepcion(cuadroTurnoDTO.getTurnoExcepcion());
            cuadroExistente.setEstadoCuadro(cuadroTurnoDTO.getEstadoCuadro());
            cuadroExistente.setMacroProcesos(macroprocesos);
            cuadroExistente.setProcesos(procesos);
            cuadroExistente.setServicios(servicio);
            cuadroExistente.setEquipos(equipo);
            cuadroExistente.setProcesosAtencion(procesosAtencion);
            cuadroExistente.setSeccionesServicios(seccionesServicio);
            // Si el estado cambia a "cerrado", generamos una nueva versión
            if (!"cerrado".equalsIgnoreCase(cuadroExistente.getEstadoCuadro()) &&
                    "cerrado".equalsIgnoreCase(cuadroTurnoDTO.getEstadoCuadro())) {
                cuadroExistente.setVersion(generarNuevaVersion(cuadroExistente.getVersion(), cuadroExistente.getAnio(), cuadroExistente.getMes()));
            }
            CuadroTurno cuadroActualizado = cuadroTurnoRepository.save(cuadroExistente);
            // Convertimos la entidad actualizada de vuelta a DTO
            return modelMapper.map(cuadroActualizado, CuadroTurnoDTO.class);
        }
        throw new RuntimeException("CuadroTurno no encontrado");
    }
    @Transactional
    public void eliminarCuadroTurno(Long id) {
        Optional<CuadroTurno> optionalCuadro = cuadroTurnoRepository.findById(id);

        if (optionalCuadro.isPresent()) {
            CuadroTurno cuadroEliminar = optionalCuadro.get();

            // Convertimos la entidad a DTO antes de eliminarla
            //CuadroTurnoDTO cuadroTurnoDTO = modelMapper.map(cuadroEliminar, CuadroTurnoDTO.class);

            // Registrar el cambio antes de eliminar
            cambiosCuadroTurnoService.registrarCambioCuadroTurno(cuadroEliminar, "ELIMINACION");

            // Eliminar el cuadro de turnos
            cuadroTurnoRepository.deleteById(id);
        } else {
            throw new RuntimeException("CuadroTurno no encontrado");
        }
    }

    private void registrarCambioCuadroTurno(CuadroTurnoDTO cuadroTurnoDTO, String tipoCambio) {
        Macroprocesos macroprocesos = null;
        if (cuadroTurnoDTO.getIdMacroproceso() != null) {
            macroprocesos = macroprocesosRepository.findById(cuadroTurnoDTO.getIdMacroproceso())
                    .orElseThrow(() -> new RuntimeException("macroproceso no encontrado."));
        }
        Procesos procesos = null;
        if (cuadroTurnoDTO.getIdProceso() != null) {
            procesos = procesosRepository.findById(cuadroTurnoDTO.getIdProceso())
                    .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));
        }
        Servicio servicio = null;
        if (cuadroTurnoDTO.getIdServicios() != null) {
            servicio = servicioRepository.findById(cuadroTurnoDTO.getIdServicios())
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));
        }
        Equipo equipo = null;
        if(cuadroTurnoDTO.getIdEquipo() != null){
            equipo = equipoRepository.findById(cuadroTurnoDTO.getIdEquipo())
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado."));
        }
        SeccionesServicio seccionesServicio = null;
        if (cuadroTurnoDTO.getIdSeccionesServicios() != null) {
            seccionesServicio = seccionesServicioRepository.findById(cuadroTurnoDTO.getIdSeccionesServicios())
                    .orElseThrow(() -> new RuntimeException("Seccion Servicio no encontrada."));
        }
        ProcesosAtencion procesosAtencion = null;
        if (cuadroTurnoDTO.getIdProcesosAtencion() != null) {
            procesosAtencion = procesosAtencionRepository.findById(cuadroTurnoDTO.getIdProcesosAtencion())
                    .orElseThrow(() -> new RuntimeException("Proceso Atencion no encontrado."));
        }

        // Convertimos el DTO a la entidad CambiosCuadroTurno
        CambiosCuadroTurno cambio = modelMapper.map(cuadroTurnoDTO, CambiosCuadroTurno.class);
        // Obtener la entidad CuadroTurno desde el repositorio usando el ID del DTO
        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(cuadroTurnoDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("CuadroTurno no encontrado"));
        cambio.setCuadroTurno(cuadroTurno);
        cambio.setFechaCambio(LocalDateTime.now());
        cambio.setNombre(cuadroTurnoDTO.getNombre());
        cambio.setMes(cuadroTurnoDTO.getMes());
        cambio.setAnio(cuadroTurnoDTO.getAnio());
        cambio.setTurnoExcepcion(cuadroTurnoDTO.getTurnoExcepcion());
        cambio.setEstadoCuadro(cuadroTurnoDTO.getEstadoCuadro());
        cambio.setVersion(cuadroTurnoDTO.getVersion());
        cambio.setMacroProcesos(macroprocesos);
        cambio.setProcesos(procesos);
        cambio.setServicios(servicio);
        cambio.setEquipos(equipo);
        cambio.setProcesoAtencion(procesosAtencion);
        cambio.setSeccionesServicios(seccionesServicio);
        cambiosCuadroTurnoRepository.save(cambio);
    }
    private String generarNuevaVersion(String versionAnterior, String anio, String mes) {
        String baseVersion = mes + anio.substring(2);
        int nuevaVersion = 1;
        if (versionAnterior != null && versionAnterior.startsWith(baseVersion)) {
            String[] partes = versionAnterior.split("_v");
            nuevaVersion = Integer.parseInt(partes[1]) + 1;
        }
        return baseVersion + "_v" + nuevaVersion;
    }

    /**
     * Restaura un cuadro de turno a una versión anterior.
     */
    public CuadroTurnoDTO restaurarCuadroTurno(Long idCambio) {
        CambiosCuadroTurno cambio = cambiosCuadroTurnoRepository.findById(idCambio)
                .orElseThrow(() -> new EntityNotFoundException("Cambio de cuadro de turno no encontrado"));
        CuadroTurno cuadroOriginal = cambio.getCuadroTurno();
        // Restaurar datos desde el cambio
        cuadroOriginal.setNombre(cambio.getNombre());
        cuadroOriginal.setAnio(cambio.getAnio());
        cuadroOriginal.setMes(cambio.getMes());
        cuadroOriginal.setEstadoCuadro(cambio.getEstadoCuadro());
        cuadroOriginal.setVersion(cambio.getVersion());
        cuadroOriginal.setTurnoExcepcion(cambio.getTurnoExcepcion());
        // Guardar la restauración en la base de datos
        CuadroTurno cuadroActualizado = cuadroTurnoRepository.save(cuadroOriginal);
        // Convertir la entidad actualizada a DTO antes de devolverla
        return modelMapper.map(cuadroActualizado, CuadroTurnoDTO.class);
    }
    @Transactional
    public Map<String, List<?>> cambiarEstadoDeCuadrosYTurnos(String estadoActual, String nuevoEstado) {
        // 1️⃣ Cambiar estado de los cuadros de turno
        List<CuadroTurno> cuadros = cuadroTurnoRepository.findByEstadoCuadro(estadoActual);
        for (CuadroTurno cuadro : cuadros) {
            cuadro.setEstadoCuadro(nuevoEstado);
        }
        cuadroTurnoRepository.saveAll(cuadros);
        // 2️⃣ Cambiar estado de los turnos asociados
        List<Turnos> turnos = turnosRepository.findByEstadoTurno(estadoActual);
        for (Turnos turno : turnos) {
            turno.setEstadoTurno(nuevoEstado);
        }
        turnosRepository.saveAll(turnos);

        // 3️⃣ Convertir las listas a DTOs
        List<CuadroTurnoDTO> cuadrosDTO = cuadros.stream()
                .map(cuadro -> modelMapper.map(cuadro, CuadroTurnoDTO.class))
                .collect(Collectors.toList());

        List<TurnoDTO> turnosDTO = turnos.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());

        // 4️⃣ Retornar ambas listas en un mapa
        Map<String, List<?>> cambios = new HashMap<>();
        cambios.put("cuadrosActualizados", cuadrosDTO);
        cambios.put("turnosActualizados", turnosDTO);
        return cambios;
    }
    public CuadroTurnoDTO actualizarTurnoExcepcion(Long id, Boolean nuevoValor, String tipoCambio) {
        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CuadroTurno no encontrado"));
        cuadroTurno.setTurnoExcepcion(nuevoValor);
        cuadroTurnoRepository.save(cuadroTurno);
        // Registrar el cambio
        CuadroTurnoDTO dto = modelMapper.map(cuadroTurno, CuadroTurnoDTO.class);
        registrarCambioCuadroTurno(dto, tipoCambio);
        return dto;
    }
}