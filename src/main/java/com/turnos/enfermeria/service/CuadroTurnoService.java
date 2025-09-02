package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
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
    private final SubseccionesServicioRepository subseccionesServicioRepository;
    private final ProcesosAtencionRepository procesosAtencionRepository;
    private final CambiosCuadroTurnoRepository cambiosCuadroTurnoRepository;
    private final CambiosTurnoRepository cambiosTurnoRepository;
    private final CambiosProcesosAtencionRepository cambiosProcesosAtencionRepository;
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
        if (cuadroTurnoDTO.getIdServicio() != null) {
            servicio = servicioRepository.findById(cuadroTurnoDTO.getIdServicio())
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));
        }

        SeccionesServicio seccionesServicio = null;
        if (cuadroTurnoDTO.getIdSeccionesServicios() != null) {
            seccionesServicio = seccionesServicioRepository.findById(cuadroTurnoDTO.getIdSeccionesServicios())
                    .orElseThrow(() -> new RuntimeException("Seccion Servicio no encontrada."));
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
        cuadroTurno.setSeccionesServicios(seccionesServicio);
        cuadroTurno.setTurnoExcepcion(cuadroTurnoDTO.getTurnoExcepcion());

        // Guardar en la base de datos primero
        CuadroTurno nuevoCuadro = cuadroTurnoRepository.save(cuadroTurno);

        // Manejar procesos de atención si existen en el DTO
        if (cuadroTurnoDTO.getIdsProcesosAtencion() != null && !cuadroTurnoDTO.getIdsProcesosAtencion().isEmpty()) {
            for (Long idProcesoAtencion : cuadroTurnoDTO.getIdsProcesosAtencion()) {
                ProcesosAtencion procesoAtencion = procesosAtencionRepository.findById(idProcesoAtencion)
                        .orElseThrow(() -> new RuntimeException("Proceso de atención no encontrado: " + idProcesoAtencion));
                procesoAtencion.setCuadroTurno(nuevoCuadro);
                procesosAtencionRepository.save(procesoAtencion);
                cambiosCuadroTurnoService.registrarCambioProcesosAtencion(procesoAtencion, "CREACION");
            }
        }

        // Registrar cambio
        cambiosCuadroTurnoService.registrarCambioCuadroTurno(nuevoCuadro, "CREACION");

        // Convertir Entidad a DTO y devolverlo
        return modelMapper.map(nuevoCuadro, CuadroTurnoDTO.class);
    }

    public List<CuadroTurnoDTO> obtenerCuadrosTurno() {
        List<CuadroTurno> cuadros = cuadroTurnoRepository.findAllByOrderByIdCuadroTurnoAsc();

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

    public List<CambiosTurnoDTO> obtenerHistorialTurnos(Long id) {
        List<CambiosTurno> historial = cambiosTurnoRepository.findByCuadroTurno_IdCuadroTurno(id);

        return historial.stream()
                .map(cambio -> {
                    CambiosTurnoDTO dto = modelMapper.map(cambio, CambiosTurnoDTO.class);
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
        if (cuadroTurnoDTO.getIdServicio() != null) {
            servicio = servicioRepository.findById(cuadroTurnoDTO.getIdServicio())
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));
        }
        SeccionesServicio seccionesServicio = null;
        if (cuadroTurnoDTO.getIdSeccionesServicios() != null) {
            seccionesServicio = seccionesServicioRepository.findById(cuadroTurnoDTO.getIdSeccionesServicios())
                    .orElseThrow(() -> new RuntimeException("Seccion Servicio no encontrada."));
        }

        Equipo equipo = null;
        if(cuadroTurnoDTO.getIdEquipo() != null){
            equipo = equipoRepository.findById(cuadroTurnoDTO.getIdEquipo())
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado."));
        }

        Optional<CuadroTurno> optionalCuadro = cuadroTurnoRepository.findById(id);
        if (optionalCuadro.isPresent()) {
            CuadroTurno cuadroExistente = optionalCuadro.get();
            // Manejar procesos de atención si existen en el DTO
            if (cuadroTurnoDTO.getIdsProcesosAtencion() != null && !cuadroTurnoDTO.getIdsProcesosAtencion().isEmpty()) {
                for (Long idProcesoAtencion : cuadroTurnoDTO.getIdsProcesosAtencion()) {
                    ProcesosAtencion procesoAtencion = procesosAtencionRepository.findById(idProcesoAtencion)
                            .orElseThrow(() -> new RuntimeException("Proceso de atención no encontrado: " + idProcesoAtencion));
                    procesoAtencion.setCuadroTurno(cuadroExistente);
                    procesosAtencionRepository.save(procesoAtencion);
                    cambiosCuadroTurnoService.registrarCambioProcesosAtencion(procesoAtencion, "ACTUALIZACION");
                }
            }
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
            //cuadroExistente.setProcesosAtencion(procesosAtencion);
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
        if (cuadroTurnoDTO.getIdServicio() != null) {
            servicio = servicioRepository.findById(cuadroTurnoDTO.getIdServicio())
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
        cambio.setSeccionesServicios(seccionesServicio);
        cambio.setCategoria(cuadroTurnoDTO.getCategoria());

        // Agregar procesosAtencion si la categoría es multiproceso
        if ("multiproceso".equalsIgnoreCase(cuadroTurno.getCategoria())) {
            // 🔁 CONSULTA los procesos desde el repositorio (sin usar relación directa en CuadroTurno)
            //List<ProcesosAtencion> procesosAtencionList = procesosAtencionRepository.findByCuadroTurnoId(cuadroTurno.getIdCuadroTurno());
            //cambio.setProcesosAtencion(procesosAtencionList);
        }
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
    public CambiosEstadoDTO cambiarEstadoDeCuadrosYTurnos(String estadoActual, String nuevoEstado, List<Long> idsCuadros) {
        // 1️⃣ Cambiar estado de los cuadros de turno seleccionados
        List<CuadroTurno> cuadros = cuadroTurnoRepository.findAllById(idsCuadros).stream()
                .filter(cuadro -> estadoActual.equals(cuadro.getEstadoCuadro()))
                .peek(cuadro -> cuadro.setEstadoCuadro(nuevoEstado))
                .collect(Collectors.toList());
        cuadroTurnoRepository.saveAll(cuadros);

        // 2️⃣ Cambiar estado en cambios_cuadro_turno en base a la relación con cuadroTurno
        List<CambiosCuadroTurno> cambios = cambiosCuadroTurnoRepository
                .findByCuadroTurnoIdCuadroTurnoIn(idsCuadros).stream()
                .filter(cambio -> estadoActual.equals(cambio.getEstadoCuadro()))
                .peek(cambio -> cambio.setEstadoCuadro(nuevoEstado))
                .collect(Collectors.toList());
        cambiosCuadroTurnoRepository.saveAll(cambios);

        // 3️⃣ Cambiar estado de los turnos asociados a los cuadros seleccionados
        List<Turnos> turnos = turnosRepository
                .findByCuadroTurnoIdCuadroTurnoIn(idsCuadros).stream()
                .filter(turno -> estadoActual.equals(turno.getEstadoTurno()))
                .peek(turno -> turno.setEstadoTurno(nuevoEstado))
                .collect(Collectors.toList());
        turnosRepository.saveAll(turnos);

        // 4️⃣ Convertir a DTOs
        List<CuadroTurnoDTO> cuadrosDTO = cuadros.stream()
                .map(cuadro -> modelMapper.map(cuadro, CuadroTurnoDTO.class))
                .collect(Collectors.toList());

        List<TurnoDTO> turnosDTO = turnos.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());

        // 5️⃣ Armar el DTO de salida
        CambiosEstadoDTO dto = new CambiosEstadoDTO();
        dto.setCuadrosActualizados(cuadrosDTO);
        dto.setTurnosActualizados(turnosDTO);
        return dto;
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



    /**
     * Crea un nuevo cuadro de turno con nombre generado automáticamente
     */
    public CuadroTurno crearCuadroTurnoTotal(CuadroTurnoRequest request) {
        CuadroTurno cuadroTurno = new CuadroTurno();

        // Establecer datos comunes según categoría
        switch (request.getCategoria().toLowerCase()) {
            case "macroproceso":
                if (request.getIdMacroproceso() != null) {
                    Macroprocesos macro = macroprocesosRepository.findById(request.getIdMacroproceso())
                            .orElseThrow(() -> new EntityNotFoundException("Macroproceso no encontrado"));
                    cuadroTurno.setMacroProcesos(macro);
                }
                break;

            case "proceso":
                if (request.getIdProceso() != null) {
                    Procesos proceso = procesosRepository.findById(request.getIdProceso())
                            .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
                    cuadroTurno.setProcesos(proceso);
                }
                break;

            case "servicio":
                if (request.getIdServicio() != null) {
                    Servicio servicio = servicioRepository.findById(request.getIdServicio())
                            .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));
                    cuadroTurno.setServicios(servicio);
                }
                break;

            case "seccion":
                if (request.getIdSeccionServicio() != null) {
                    SeccionesServicio seccion = seccionesServicioRepository.findById(request.getIdSeccionServicio())
                            .orElseThrow(() -> new EntityNotFoundException("Sección de servicio no encontrada"));
                    cuadroTurno.setSeccionesServicios(seccion);
                }
                break;

            case "subseccion":
                if (request.getIdSubseccionServicio() != null) {
                    SubseccionesServicio subseccion = subseccionesServicioRepository.findById(request.getIdSeccionServicio())
                            .orElseThrow(() -> new EntityNotFoundException("subseccion de servicio no encontrada"));
                    cuadroTurno.setSubseccionesServicios(subseccion);
                }
                break;

            case "multiproceso":
                // Esta categoría usa múltiples procesos base
                if (request.getIdsProcesosAtencion() != null && !request.getIdsProcesosAtencion().isEmpty()) {
                    List<ProcesosAtencion> procesosAtencion = new ArrayList<>();

                    for (Long idProcesoBase : request.getIdsProcesosAtencion()) {
                        Procesos procesoBase = procesosRepository.findById(idProcesoBase)
                                .orElseThrow(() -> new EntityNotFoundException("Proceso base no encontrado con ID: " + idProcesoBase));

                        ProcesosAtencion nuevoProcesoAtencion = new ProcesosAtencion();
                        nuevoProcesoAtencion.setProcesos(procesoBase);
                        nuevoProcesoAtencion.setDetalle(procesoBase.getNombre()); // o como desees generar el detalle
                        nuevoProcesoAtencion.setCuadroTurno(cuadroTurno); // se asigna el cuadroTurno antes de guardar
                        procesosAtencionRepository.save(nuevoProcesoAtencion);
                        procesosAtencion.add(nuevoProcesoAtencion);
                        cambiosCuadroTurnoService.registrarCambioProcesosAtencion(nuevoProcesoAtencion, "CREACION");
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Categoría no válida: " + request.getCategoria());
        }

        // Relación con equipo (opcional)
        if (request.getIdEquipo() != null) {
            Equipo equipo = equipoRepository.findById(request.getIdEquipo())
                    .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));
            cuadroTurno.setEquipos(equipo);
        }

        // Establecer año, mes, nombre y versión
        cuadroTurno.setAnio(request.getAnio());
        cuadroTurno.setMes(request.getMes());
        cuadroTurno.setNombre(generarNombreCuadroTurno(cuadroTurno));
        cuadroTurno.setVersion(generarNuevaVersion(cuadroTurno.getVersion(), cuadroTurno.getAnio(), cuadroTurno.getMes()));
        cuadroTurno.setEstadoCuadro("abierto");
        cuadroTurno.setTurnoExcepcion(false);
        cuadroTurno.setCategoria(request.getCategoria());

        cambiosCuadroTurnoService.registrarCambioCuadroTurno(cuadroTurno, "CREACION");

        return cuadroTurnoRepository.save(cuadroTurno);
    }

    /**
     * Edita un cuadro de turno existente manteniendo su estructura básica
     */
    public CuadroTurno editarCuadroTurnoTotal(Long idCuadro, CuadroTurnoRequest request) {
        // Obtener el cuadro existente
        CuadroTurno cuadroExistente = cuadroTurnoRepository.findById(idCuadro)
                .orElseThrow(() -> new EntityNotFoundException("Cuadro de turno no encontrado"));

        // Validar que el cuadro esté en estado editable
        if (!"abierto".equalsIgnoreCase(cuadroExistente.getEstadoCuadro())) {
            throw new IllegalStateException("El cuadro de turno no está en estado editable");
        }

        // Actualizar relaciones según categoría
        switch (request.getCategoria().toLowerCase()) {
            case "macroproceso":
                cuadroExistente.setProcesos(null);
                cuadroExistente.setServicios(null);
                cuadroExistente.setSeccionesServicios(null);
                cuadroExistente.setSubseccionesServicios(null);
                procesosAtencionRepository.deleteByCuadroTurnoId(idCuadro);

                if (request.getIdMacroproceso() != null) {
                    Macroprocesos macro = macroprocesosRepository.findById(request.getIdMacroproceso())
                            .orElseThrow(() -> new EntityNotFoundException("Macroproceso no encontrado"));
                    cuadroExistente.setMacroProcesos(macro);
                } else {
                    cuadroExistente.setMacroProcesos(null);
                }
                break;

            case "proceso":
                cuadroExistente.setMacroProcesos(null);
                cuadroExistente.setServicios(null);
                cuadroExistente.setSeccionesServicios(null);
                cuadroExistente.setSubseccionesServicios(null);
                procesosAtencionRepository.deleteByCuadroTurnoId(idCuadro);

                if (request.getIdProceso() != null) {
                    Procesos proceso = procesosRepository.findById(request.getIdProceso())
                            .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
                    cuadroExistente.setProcesos(proceso);
                } else {
                    cuadroExistente.setProcesos(null);
                }
                break;

            case "servicio":
                cuadroExistente.setMacroProcesos(null);
                cuadroExistente.setProcesos(null);
                cuadroExistente.setSeccionesServicios(null);
                cuadroExistente.setSubseccionesServicios(null);
                procesosAtencionRepository.deleteByCuadroTurnoId(idCuadro);

                if (request.getIdServicio() != null) {
                    Servicio servicio = servicioRepository.findById(request.getIdServicio())
                            .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));
                    cuadroExistente.setServicios(servicio);
                } else {
                    cuadroExistente.setServicios(null);
                }
                break;

            case "seccion":
                cuadroExistente.setMacroProcesos(null);
                cuadroExistente.setProcesos(null);
                cuadroExistente.setServicios(null);
                cuadroExistente.setSubseccionesServicios(null);
                procesosAtencionRepository.deleteByCuadroTurnoId(idCuadro);

                if (request.getIdSeccionServicio() != null) {
                    SeccionesServicio seccion = seccionesServicioRepository.findById(request.getIdSeccionServicio())
                            .orElseThrow(() -> new EntityNotFoundException("Sección de servicio no encontrada"));
                    cuadroExistente.setSeccionesServicios(seccion);
                } else {
                    cuadroExistente.setSeccionesServicios(null);
                }
                break;

            case "subseccion":
                cuadroExistente.setMacroProcesos(null);
                cuadroExistente.setProcesos(null);
                cuadroExistente.setServicios(null);
                cuadroExistente.setSeccionesServicios(null);
                procesosAtencionRepository.deleteByCuadroTurnoId(idCuadro);

                if (request.getIdSubseccionServicio() != null) {
                    SubseccionesServicio subseccion = subseccionesServicioRepository.findById(request.getIdSubseccionServicio())
                            .orElseThrow(() -> new EntityNotFoundException("Subsección de servicio no encontrada"));
                    cuadroExistente.setSubseccionesServicios(subseccion);
                } else {
                    cuadroExistente.setSubseccionesServicios(null);
                }
                break;

            case "multiproceso":
                // Limpiar relaciones simples
                cuadroExistente.setMacroProcesos(null);
                cuadroExistente.setProcesos(null);
                cuadroExistente.setServicios(null);
                cuadroExistente.setSeccionesServicios(null);
                cuadroExistente.setSubseccionesServicios(null);

                // Eliminar procesos de atención existentes
                procesosAtencionRepository.deleteByCuadroTurnoId(idCuadro);

                // Crear nuevos procesos de atención
                if (request.getIdsProcesosAtencion() != null && !request.getIdsProcesosAtencion().isEmpty()) {
                    for (Long idProcesoBase : request.getIdsProcesosAtencion()) {
                        Procesos procesoBase = procesosRepository.findById(idProcesoBase)
                                .orElseThrow(() -> new EntityNotFoundException("Proceso base no encontrado con ID: " + idProcesoBase));

                        ProcesosAtencion nuevoProcesoAtencion = new ProcesosAtencion();
                        nuevoProcesoAtencion.setProcesos(procesoBase);
                        nuevoProcesoAtencion.setDetalle(procesoBase.getNombre());
                        nuevoProcesoAtencion.setCuadroTurno(cuadroExistente);

                        procesosAtencionRepository.save(nuevoProcesoAtencion);
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Categoría no válida: " + request.getCategoria());
        }

        // Resto del método (equipo, año, mes, versión, etc.)
        if (request.getIdEquipo() != null) {
            Equipo equipo = equipoRepository.findById(request.getIdEquipo())
                    .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));
            cuadroExistente.setEquipos(equipo);
        } else {
            cuadroExistente.setEquipos(null);
        }

        boolean cambioPeriodo = !cuadroExistente.getAnio().equals(request.getAnio()) ||
                !cuadroExistente.getMes().equals(request.getMes());

        cuadroExistente.setAnio(request.getAnio());
        cuadroExistente.setMes(request.getMes());

        if (cambioPeriodo) {
            cuadroExistente.setVersion(generarNuevaVersion(cuadroExistente.getVersion(),
                    request.getAnio(),
                    request.getMes()));
        }

        cuadroExistente.setNombre(generarNombreCuadroTurno(cuadroExistente));
        cuadroExistente.setCategoria(request.getCategoria());

        return cuadroTurnoRepository.save(cuadroExistente);
    }

    /**
     * Genera el nombre del cuadro de turno basado en la selección del usuario
     * Formato: CT_{consecutivo}_{Categoria}_{Identificador}_{Equipo}
     */
    private String generarNombreCuadroTurno(CuadroTurno cuadroTurno) {
        StringBuilder nombreBaseBuilder = new StringBuilder(); // sin "CT_"

        // Determinar la categoría principal
        String categoria = determinarCategoriaPrincipal(cuadroTurno);
        nombreBaseBuilder.append(categoria).append("_");

        // Agregar identificador específico
        String identificador = obtenerIdentificadorEspecifico(cuadroTurno, categoria);
        nombreBaseBuilder.append(identificador);

        // Agregar equipo si está disponible
        if (cuadroTurno.getEquipos() != null) {
            String equipoNombre = limpiarNombreParaId(cuadroTurno.getEquipos().getNombre());
            nombreBaseBuilder.append("_").append(equipoNombre);
        }

        String nombreBase = nombreBaseBuilder.toString();

        // Buscar nombres similares en base al nombre generado
        List<String> nombresSimilares = cuadroTurnoRepository.findNombresByBase(nombreBase);
        long count = nombresSimilares.size();

        return String.format("CT_%02d_%s", count + 1, nombreBase);
    }


    /**
     * Determina la categoría principal basada en la jerarquía de selección
     */
    private String determinarCategoriaPrincipal(CuadroTurno cuadroTurno) {
//        if (cuadroTurno.getProcesosAtencion() != null && !cuadroTurno.getProcesosAtencion().isEmpty()) {
//            return "ProcesoAtencion";
//        }
        if (cuadroTurno.getSeccionesServicios() != null) {
            return "Seccion";
        }  else if (cuadroTurno.getIdSubeccionServicio() != null) {
            return "Subseccion";
        }else if (cuadroTurno.getServicios() != null) {
            return "Servicio";
        } else if (cuadroTurno.getProcesos() != null) {
            return "Proceso";
        } else if (cuadroTurno.getMacroProcesos() != null) {
            return "Macroproceso";
        } else {
            return "Multiproceso";
        }
    }

    /**
     * Obtiene el identificador específico basado en la categoría
     */
    private String obtenerIdentificadorEspecifico(CuadroTurno cuadroTurno, String categoria) {
        switch (categoria) {
//            case "ProcesoAtencion":
//                // Para múltiples procesos, crear un identificador compuesto
//                if (cuadroTurno.getProcesosAtencion() != null && !cuadroTurno.getProcesosAtencion().isEmpty()) {
//                    if (cuadroTurno.getProcesosAtencion().size() == 1) {
//                        // Un solo proceso
//                        return limpiarNombreParaId(cuadroTurno.getProcesosAtencion().get(0).getDetalle());
//                    } else {
//                        // Múltiples procesos: usar un identificador genérico más contador
//                        return "MULTIPROCESO_" + cuadroTurno.getProcesosAtencion().size();
//                    }
//                }
//                return "PROCESO_ATENCION";
            case "Seccion":
                return limpiarNombreParaId(cuadroTurno.getSeccionesServicios().getNombre());
            case "Subseccion":
                return limpiarNombreParaId(cuadroTurno.getSubseccionesServicios().getNombre());
            case "Servicio":
                return limpiarNombreParaId(cuadroTurno.getServicios().getNombre());
            case "Proceso":
                return limpiarNombreParaId(cuadroTurno.getProcesos().getNombre());
            case "Macroproceso":
                return limpiarNombreParaId(cuadroTurno.getMacroProcesos().getNombre());
            default:
                return "Procesos";
        }
    }

    /**
     * Limpia el nombre para usar como identificador (sin espacios, caracteres especiales)
     */
    private String limpiarNombreParaId(String nombre) {
        if (nombre == null) return "UNKNOWN";

        return nombre.replaceAll("[^\\p{L}\\p{N}]", "_") // permite letras y números de cualquier idioma
                .replaceAll("_+", "_")              // reemplaza múltiples _ por uno solo
                .replaceAll("^_|_$", "")            // elimina _ al inicio o final
                .toUpperCase();
    }

    /**
     * Valida si ya existe un cuadro de turno similar
     * ACTUALIZADO: Considera múltiples procesos de atención
     */
    public boolean existeCuadroTurnoSimilar(CuadroTurnoRequest request) {
        return cuadroTurnoRepository.existsBySimilarConfigurationWithMultipleProcesses(
                request.getIdMacroproceso(),
                request.getIdProceso(),
                request.getIdServicio(),
                request.getIdSeccionServicio(),
                request.getIdSubseccionServicio(),
                //request.getIdsProcesosAtencion(), // Ahora es una lista
                request.getIdEquipo(),
                request.getAnio(),
                request.getMes()
        );
    }

    public List<ProcesosDTO> obtenerProcesosDesdeCuadroMultiproceso(Long idCuadroTurno) {
        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(idCuadroTurno)
                .orElseThrow(() -> new EntityNotFoundException("Cuadro de turno no encontrado"));

        // Validar que sea de tipo multiproceso
//        if (!"multiproceso".equalsIgnoreCase(cuadroTurno.getCategoria())) {
//            throw new IllegalArgumentException("El cuadro de turno no es de tipo multiproceso");
//        }

        // Obtener los procesos asociados desde ProcesosAtencion
        List<ProcesosAtencion> procesosAtencion = procesosAtencionRepository.findByCuadroTurnoId(idCuadroTurno);

        return procesosAtencion.stream()
                .map(pa -> {
                    Procesos proceso = pa.getProcesos();
                    return new ProcesosDTO(
                            proceso.getIdProceso(),
                            proceso.getNombre(),
                            proceso.getIdMacroproceso() != null ? proceso.getIdMacroproceso() : null,
                            proceso.getEstado()
                    );
                })
                .collect(Collectors.toList());
    }

}