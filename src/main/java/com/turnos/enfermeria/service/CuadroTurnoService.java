package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
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

    public CuadroTurnoDTO crearCuadroTurno(CuadroTurnoDTO cuadroTurnoDTO) {
        // Buscar entidades relacionadas
        Macroprocesos macroprocesos = buscarMacroproceso(cuadroTurnoDTO.getIdMacroproceso());
        Procesos procesos = buscarProceso(cuadroTurnoDTO.getIdProceso());
        Servicio servicio = buscarServicio(cuadroTurnoDTO.getIdServicio());
        SeccionesServicio seccionesServicio = buscarSeccionServicio(cuadroTurnoDTO.getIdSeccionesServicios());
        SubseccionesServicio subseccionesServicio = buscarSubseccionServicio(cuadroTurnoDTO.getIdSubseccionServicio());
        Equipo equipo = buscarEquipo(cuadroTurnoDTO.getIdEquipo());

        // Convertir DTO a Entidad
        CuadroTurno cuadroTurno = modelMapper.map(cuadroTurnoDTO, CuadroTurno.class);
        configurarCuadroTurno(cuadroTurno, cuadroTurnoDTO, macroprocesos, procesos, servicio, seccionesServicio, subseccionesServicio, equipo);

        // Guardar en la base de datos primero
        CuadroTurno nuevoCuadro = cuadroTurnoRepository.save(cuadroTurno);

        // Manejar procesos de atención si existen en el DTO
        procesarProcesosAtencion(cuadroTurnoDTO.getIdsProcesosAtencion(), nuevoCuadro, "CREACION");

        // Registrar cambio
        CuadroTurnoDTO dtoParaCambio = modelMapper.map(nuevoCuadro, CuadroTurnoDTO.class);
        cambiosCuadroTurnoService.registrarCambioCuadroTurno(dtoParaCambio, "CREACION");

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

    public CuadroTurnoDTO actualizarCuadroTurno(Long id, CuadroTurnoDTO cuadroTurnoDTO) {
        // Buscar entidades relacionadas
        Macroprocesos macroprocesos = buscarMacroproceso(cuadroTurnoDTO.getIdMacroproceso());
        Procesos procesos = buscarProceso(cuadroTurnoDTO.getIdProceso());
        Servicio servicio = buscarServicio(cuadroTurnoDTO.getIdServicio());
        SeccionesServicio seccionesServicio = buscarSeccionServicio(cuadroTurnoDTO.getIdSeccionesServicios());
        Equipo equipo = buscarEquipo(cuadroTurnoDTO.getIdEquipo());

        Optional<CuadroTurno> optionalCuadro = cuadroTurnoRepository.findById(id);
        if (optionalCuadro.isPresent()) {
            CuadroTurno cuadroExistente = optionalCuadro.get();

            // Manejar procesos de atención si existen en el DTO
            procesarProcesosAtencion(cuadroTurnoDTO.getIdsProcesosAtencion(), cuadroExistente, "ACTUALIZACION");

            cuadroExistente.setNombre(cuadroTurnoDTO.getNombre());
            cuadroExistente.setAnio(cuadroTurnoDTO.getAnio());
            cuadroExistente.setMes(cuadroTurnoDTO.getMes());
            cuadroExistente.setTurnoExcepcion(cuadroTurnoDTO.getTurnoExcepcion());
            cuadroExistente.setEstadoCuadro(cuadroTurnoDTO.getEstadoCuadro());
            cuadroExistente.setMacroProcesos(macroprocesos);
            cuadroExistente.setProcesos(procesos);
            cuadroExistente.setServicios(servicio);
            cuadroExistente.setEquipos(equipo);
            cuadroExistente.setCategoria(cuadroTurnoDTO.getCategoria());
            cuadroExistente.setEstado(cuadroTurnoDTO.getEstado());
            cuadroExistente.setSeccionesServicios(seccionesServicio);

            // Si el estado cambia a "cerrado", generamos una nueva versión
            if (!"cerrado".equalsIgnoreCase(cuadroExistente.getEstadoCuadro()) &&
                    "cerrado".equalsIgnoreCase(cuadroTurnoDTO.getEstadoCuadro())) {
                cuadroExistente.setVersion(generarNuevaVersion(cuadroExistente.getVersion(), cuadroExistente.getAnio(), cuadroExistente.getMes()));
            }

            // Mapear los datos desde el DTO al objeto existente
            CuadroTurno cuadroActualizado = cuadroTurnoRepository.save(cuadroExistente);

            // Guardamos solo los datos relevantes en un objeto de historial
            CuadroTurnoDTO dtoParaCambio = modelMapper.map(cuadroActualizado, CuadroTurnoDTO.class);
            cambiosCuadroTurnoService.registrarCambioCuadroTurno(dtoParaCambio, "ACTUALIZACION");

            // Convertimos la entidad actualizada de vuelta a DTO
            return modelMapper.map(cuadroActualizado, CuadroTurnoDTO.class);
        }
        throw new EntityNotFoundException("CuadroTurno no encontrado");
    }

    public void eliminarCuadroTurno(Long id) {
        Optional<CuadroTurno> optionalCuadro = cuadroTurnoRepository.findById(id);

        if (optionalCuadro.isPresent()) {
            CuadroTurno cuadroEliminar = optionalCuadro.get();

            // Registrar el cambio antes de eliminar
            CuadroTurnoDTO dtoParaCambio = modelMapper.map(cuadroEliminar, CuadroTurnoDTO.class);
            cambiosCuadroTurnoService.registrarCambioCuadroTurno(dtoParaCambio, "ELIMINACION");

            // Eliminar el cuadro de turnos
            cuadroTurnoRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("CuadroTurno no encontrado");
        }
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
                .orElseThrow(() -> new EntityNotFoundException("CuadroTurno no encontrado"));
        cuadroTurno.setTurnoExcepcion(nuevoValor);
        cuadroTurnoRepository.save(cuadroTurno);
        // Registrar el cambio
        CuadroTurnoDTO dto = modelMapper.map(cuadroTurno, CuadroTurnoDTO.class);
        cambiosCuadroTurnoService.registrarCambioCuadroTurno(dto, tipoCambio);
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
                    SubseccionesServicio subseccion = subseccionesServicioRepository.findById(request.getIdSubseccionServicio())
                            .orElseThrow(() -> new EntityNotFoundException("subseccion de servicio no encontrada"));
                    cuadroTurno.setSubseccionesServicios(subseccion);
                }
                break;

            case "multiproceso":
                // Esta categoría usa múltiples procesos base
                if (request.getIdsProcesosAtencion() != null && !request.getIdsProcesosAtencion().isEmpty()) {
                    for (Long idProcesoBase : request.getIdsProcesosAtencion()) {
                        Procesos procesoBase = procesosRepository.findById(idProcesoBase)
                                .orElseThrow(() -> new EntityNotFoundException("Proceso base no encontrado con ID: " + idProcesoBase));

                        ProcesosAtencion nuevoProcesoAtencion = new ProcesosAtencion();
                        nuevoProcesoAtencion.setProcesos(procesoBase);
                        nuevoProcesoAtencion.setDetalle(procesoBase.getNombre()); // o como desees generar el detalle
                        nuevoProcesoAtencion.setCuadroTurno(cuadroTurno); // se asigna el cuadroTurno antes de guardar
                        procesosAtencionRepository.save(nuevoProcesoAtencion);
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
        cuadroTurno.setEstado(request.getEstado());

        CuadroTurno savedCuadro = cuadroTurnoRepository.save(cuadroTurno);
        CuadroTurnoDTO dtoParaCambio = modelMapper.map(savedCuadro, CuadroTurnoDTO.class);
        cambiosCuadroTurnoService.registrarCambioCuadroTurno(dtoParaCambio, "CREACION");

        return savedCuadro;
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
                limpiarRelacionesExcepto(cuadroExistente, idCuadro, "macroproceso");
                if (request.getIdMacroproceso() != null) {
                    Macroprocesos macro = macroprocesosRepository.findById(request.getIdMacroproceso())
                            .orElseThrow(() -> new EntityNotFoundException("Macroproceso no encontrado"));
                    cuadroExistente.setMacroProcesos(macro);
                }
                break;

            case "proceso":
                limpiarRelacionesExcepto(cuadroExistente, idCuadro, "proceso");
                if (request.getIdProceso() != null) {
                    Procesos proceso = procesosRepository.findById(request.getIdProceso())
                            .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
                    cuadroExistente.setProcesos(proceso);
                }
                break;

            case "servicio":
                limpiarRelacionesExcepto(cuadroExistente, idCuadro, "servicio");
                if (request.getIdServicio() != null) {
                    Servicio servicio = servicioRepository.findById(request.getIdServicio())
                            .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));
                    cuadroExistente.setServicios(servicio);
                }
                break;

            case "seccion":
                limpiarRelacionesExcepto(cuadroExistente, idCuadro, "seccion");
                if (request.getIdSeccionServicio() != null) {
                    SeccionesServicio seccion = seccionesServicioRepository.findById(request.getIdSeccionServicio())
                            .orElseThrow(() -> new EntityNotFoundException("Sección de servicio no encontrada"));
                    cuadroExistente.setSeccionesServicios(seccion);
                }
                break;

            case "subseccion":
                limpiarRelacionesExcepto(cuadroExistente, idCuadro, "subseccion");
                if (request.getIdSubseccionServicio() != null) {
                    SubseccionesServicio subseccion = subseccionesServicioRepository.findById(request.getIdSubseccionServicio())
                            .orElseThrow(() -> new EntityNotFoundException("Subsección de servicio no encontrada"));
                    cuadroExistente.setSubseccionesServicios(subseccion);
                }
                break;

            case "multiproceso":
                limpiarRelacionesExcepto(cuadroExistente, idCuadro, "multiproceso");
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
        if (cuadroTurno.getSeccionesServicios() != null) {
            return "Seccion";
        } else if (cuadroTurno.getSubseccionesServicios() != null) {
            return "Subseccion";
        } else if (cuadroTurno.getServicios() != null) {
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
                request.getIdEquipo(),
                request.getAnio(),
                request.getMes()
        );
    }

    public List<ProcesosDTO> obtenerProcesosDesdeCuadroMultiproceso(Long idCuadroTurno) {
        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(idCuadroTurno)
                .orElseThrow(() -> new EntityNotFoundException("Cuadro de turno no encontrado"));

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

    // Métodos auxiliares para reducir duplicación
    private Macroprocesos buscarMacroproceso(Long id) {
        return id != null ? macroprocesosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Macroproceso no encontrado.")) : null;
    }

    private Procesos buscarProceso(Long id) {
        return id != null ? procesosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado.")) : null;
    }

    private Servicio buscarServicio(Long id) {
        return id != null ? servicioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado.")) : null;
    }

    private SeccionesServicio buscarSeccionServicio(Long id) {
        return id != null ? seccionesServicioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sección Servicio no encontrada.")) : null;
    }

    private SubseccionesServicio buscarSubseccionServicio(Long id) {
        return id != null ? subseccionesServicioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SubSección Servicio no encontrada.")) : null;
    }

    private Equipo buscarEquipo(Long id) {
        return id != null ? equipoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado.")) : null;
    }

    private void configurarCuadroTurno(CuadroTurno cuadroTurno, CuadroTurnoDTO dto,
                                       Macroprocesos macro, Procesos proceso, Servicio servicio,
                                       SeccionesServicio seccion, SubseccionesServicio subseccion, Equipo equipo) {
        cuadroTurno.setAnio(dto.getAnio());
        cuadroTurno.setEstadoCuadro(dto.getEstadoCuadro());
        cuadroTurno.setMes(dto.getMes());
        cuadroTurno.setNombre(dto.getNombre());
        cuadroTurno.setVersion(generarNuevaVersion(dto.getVersion(), dto.getAnio(), dto.getMes()));
        cuadroTurno.setMacroProcesos(macro);
        cuadroTurno.setProcesos(proceso);
        cuadroTurno.setServicios(servicio);
        cuadroTurno.setEquipos(equipo);
        cuadroTurno.setSeccionesServicios(seccion);
        cuadroTurno.setSubseccionesServicios(subseccion);
        cuadroTurno.setTurnoExcepcion(dto.getTurnoExcepcion());
        cuadroTurno.setCategoria(dto.getCategoria());
        cuadroTurno.setEstado(dto.getEstado());
    }

    private void procesarProcesosAtencion(List<Long> idsProcesos, CuadroTurno cuadro, String tipoOperacion) {
        if (idsProcesos != null && !idsProcesos.isEmpty()) {
            for (Long idProcesoAtencion : idsProcesos) {
                ProcesosAtencion procesoAtencion = procesosAtencionRepository.findById(idProcesoAtencion)
                        .orElseThrow(() -> new EntityNotFoundException("Proceso de atención no encontrado: " + idProcesoAtencion));
                procesoAtencion.setCuadroTurno(cuadro);
                procesosAtencionRepository.save(procesoAtencion);
                cambiosCuadroTurnoService.registrarCambioProcesosAtencion(procesoAtencion, tipoOperacion);
            }
        }
    }

    private void limpiarRelacionesExcepto(CuadroTurno cuadro, Long idCuadro, String categoriaActiva) {
        if (!"macroproceso".equals(categoriaActiva)) cuadro.setMacroProcesos(null);
        if (!"proceso".equals(categoriaActiva)) cuadro.setProcesos(null);
        if (!"servicio".equals(categoriaActiva)) cuadro.setServicios(null);
        if (!"seccion".equals(categoriaActiva)) cuadro.setSeccionesServicios(null);
        if (!"subseccion".equals(categoriaActiva)) cuadro.setSubseccionesServicios(null);
        procesosAtencionRepository.deleteByCuadroTurnoId(idCuadro);
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
}
