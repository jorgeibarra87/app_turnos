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

            // Guardar estado anterior
            String estadoAnterior = cuadroExistente.getEstadoCuadro();

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

            // Manejar versiones según los cambios de estado
            manejarVersionesPorEstado(cuadroExistente, estadoAnterior, cuadroTurnoDTO.getEstadoCuadro());

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

    /**
     * Maneja las versiones según los cambios de estado del cuadro
     */
    private void manejarVersionesPorEstado(CuadroTurno cuadro, String estadoAnterior, String nuevoEstado) {
        String baseVersion = cuadro.getMes() + cuadro.getAnio().substring(2);

        // Si se está cerrando el cuadro (de cualquier estado a "cerrado")
        if (!"cerrado".equalsIgnoreCase(estadoAnterior) && "cerrado".equalsIgnoreCase(nuevoEstado)) {
            // Mantener la versión actual (no cambiarla)
            if (cuadro.getVersion() == null) {
                cuadro.setVersion(baseVersion + "_v1");
            }
            return;
        }

        // Si se está reabriendo el cuadro (de "cerrado" a cualquier otro estado)
        if ("cerrado".equalsIgnoreCase(estadoAnterior) && !"cerrado".equalsIgnoreCase(nuevoEstado)) {
            // Incrementar la versión
            cuadro.setVersion(incrementarVersion(cuadro.getVersion(), cuadro.getAnio(), cuadro.getMes()));
            return;
        }

        // Para otros cambios de estado, mantener la versión actual
        if (cuadro.getVersion() == null) {
            cuadro.setVersion(baseVersion + "_v1");
        }
    }

    /**
     * Incrementa la versión manteniendo el formato mes+año_v+número
     */
    private String incrementarVersion(String versionActual, String anio, String mes) {
        String baseVersion = mes + anio.substring(2);

        if (versionActual != null && versionActual.startsWith(baseVersion)) {
            // Extraer el número de versión actual e incrementarlo
            String[] partes = versionActual.split("_v");
            if (partes.length == 2) {
                try {
                    int numeroVersion = Integer.parseInt(partes[1]);
                    return baseVersion + "_v" + (numeroVersion + 1);
                } catch (NumberFormatException e) {
                    return baseVersion + "_v2";
                }
            }
        }

        return baseVersion + "_v2";
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
        cuadroOriginal.setCategoria(cambio.getCategoria());
        cuadroOriginal.setEstado(cambio.getEstado());
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
    public CuadroTurnoDTO crearCuadroTurnoTotal(CuadroTurnoRequest request) {
        try {
            System.out.println("Datos recibidos: " + request.toString()); // Debug

            // Buscar entidades relacionadas usando métodos auxiliares
            Macroprocesos macroprocesos = buscarMacroproceso(request.getIdMacroproceso());
            Procesos procesos = buscarProceso(request.getIdProceso());
            Servicio servicio = buscarServicio(request.getIdServicio());
            SeccionesServicio seccionesServicio = buscarSeccionServicio(request.getIdSeccionServicio());
            SubseccionesServicio subseccionesServicio = buscarSubseccionServicio(request.getIdSubseccionServicio());
            Equipo equipo = buscarEquipo(request.getIdEquipo());

            System.out.println("Entidades encontradas correctamente"); // Debug

            // Crear cuadro de turno
            CuadroTurno cuadroTurno = new CuadroTurno();

            // ✅ ESTABLECER CAMPOS BÁSICOS PRIMERO
            cuadroTurno.setAnio(request.getAnio());
            cuadroTurno.setMes(request.getMes());
            cuadroTurno.setCategoria(request.getCategoria().toLowerCase());
            cuadroTurno.setEstado(request.getEstado() != null ? request.getEstado() : true);
            cuadroTurno.setEquipos(equipo);

            configurarCuadroSegunCategoria(cuadroTurno, request, macroprocesos, procesos,
                    servicio, seccionesServicio, subseccionesServicio, equipo);

            // El nombre se genera automáticamente en el backend
            cuadroTurno.setNombre(generarNombreCuadroTurno(cuadroTurno));
            cuadroTurno.setVersion(generarNuevaVersion(null, cuadroTurno.getAnio(), cuadroTurno.getMes()));
            cuadroTurno.setEstadoCuadro("abierto");
            cuadroTurno.setTurnoExcepcion(false);

            System.out.println("Cuadro configurado, guardando..."); // Debug

            // Guardar cuadro
            CuadroTurno savedCuadro = cuadroTurnoRepository.save(cuadroTurno);

            // Procesar procesos de atención para multiproceso
            if ("multiproceso".equalsIgnoreCase(request.getCategoria()) &&
                    request.getIdsProcesosAtencion() != null && !request.getIdsProcesosAtencion().isEmpty()) {
                procesarProcesosAtencionParaCreacion(request.getIdsProcesosAtencion(), savedCuadro);
            }

            // Registrar cambio en historial
            CuadroTurnoDTO dtoParaCambio = modelMapper.map(savedCuadro, CuadroTurnoDTO.class);
            cambiosCuadroTurnoService.registrarCambioCuadroTurno(dtoParaCambio, "CREACION");

            return modelMapper.map(savedCuadro, CuadroTurnoDTO.class);

        } catch (Exception e) {
            System.err.println("Error en crearCuadroTurnoTotal: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Edita un cuadro de turno existente manteniendo su estructura básica
     */
    public CuadroTurnoDTO editarCuadroTurnoTotal(Long idCuadro, CuadroTurnoRequest request) {
        // Buscar cuadro existente
        CuadroTurno cuadroExistente = cuadroTurnoRepository.findById(idCuadro)
                .orElseThrow(() -> new EntityNotFoundException("Cuadro de turno no encontrado"));

        // Validar estado editable
        if (!"abierto".equalsIgnoreCase(cuadroExistente.getEstadoCuadro())) {
            throw new IllegalStateException("El cuadro de turno no está en estado editable");
        }

        // Guardar estado anterior para el historial
        CuadroTurnoDTO estadoAnterior = modelMapper.map(cuadroExistente, CuadroTurnoDTO.class);

        // Buscar entidades relacionadas
        Macroprocesos macroprocesos = buscarMacroproceso(request.getIdMacroproceso());
        Procesos procesos = buscarProceso(request.getIdProceso());
        Servicio servicio = buscarServicio(request.getIdServicio());
        SeccionesServicio seccionesServicio = buscarSeccionServicio(request.getIdSeccionServicio());
        SubseccionesServicio subseccionesServicio = buscarSubseccionServicio(request.getIdSubseccionServicio());
        Equipo equipo = buscarEquipo(request.getIdEquipo());

        // Actualizar configuración del cuadro
        actualizarCuadroSegunCategoria(cuadroExistente, request, macroprocesos, procesos,
                servicio, seccionesServicio, subseccionesServicio, equipo);

        // Regenerar nombre automáticamente
        cuadroExistente.setNombre(generarNombreCuadroTurno(cuadroExistente));

        // Manejar cambio de período si es necesario
        boolean cambioPeriodo = !cuadroExistente.getAnio().equals(request.getAnio()) ||
                !cuadroExistente.getMes().equals(request.getMes());

        cuadroExistente.setAnio(request.getAnio());
        cuadroExistente.setMes(request.getMes());

        if (cambioPeriodo) {
            cuadroExistente.setVersion(generarNuevaVersion(cuadroExistente.getVersion(),
                    request.getAnio(), request.getMes()));
        }

        // Guardar cambios
        CuadroTurno cuadroActualizado = cuadroTurnoRepository.save(cuadroExistente);

        // Registrar cambios en historial
        cambiosCuadroTurnoService.registrarCambioCuadroTurno(estadoAnterior, "ACTUALIZACION_ANTERIOR");
        CuadroTurnoDTO estadoNuevo = modelMapper.map(cuadroActualizado, CuadroTurnoDTO.class);
        cambiosCuadroTurnoService.registrarCambioCuadroTurno(estadoNuevo, "ACTUALIZACION");


        return estadoNuevo;
    }

    // Método auxiliar para configurar cuadro según categoría
    private void configurarCuadroSegunCategoria(CuadroTurno cuadro, CuadroTurnoRequest request,
                                                Macroprocesos macro, Procesos proceso, Servicio servicio,
                                                SeccionesServicio seccion, SubseccionesServicio subseccion,
                                                Equipo equipo) {
        // Limpiar todas las relaciones primero
        cuadro.setMacroProcesos(null);
        cuadro.setProcesos(null);
        cuadro.setServicios(null);
        cuadro.setSeccionesServicios(null);
        cuadro.setSubseccionesServicios(null);
        cuadro.setEquipos(equipo);
        cuadro.setCategoria(request.getCategoria().toLowerCase());
        cuadro.setEstado(request.getEstado());

        // Asignar según categoría
        switch (request.getCategoria().toLowerCase()) {
            case "macroproceso":
                cuadro.setMacroProcesos(macro);
                break;
            case "proceso":
                cuadro.setProcesos(proceso);
                break;
            case "servicio":
                cuadro.setServicios(servicio);
                break;
            case "seccion":
                cuadro.setSeccionesServicios(seccion);
                break;
            case "subseccion":
                cuadro.setSubseccionesServicios(subseccion);
                break;
            case "multiproceso":
                // Los procesos se manejan por separado
                break;
            default:
                throw new IllegalArgumentException("Categoría no válida: " + request.getCategoria());
        }
    }

    // Método auxiliar para actualizar cuadro existente
    private void actualizarCuadroSegunCategoria(CuadroTurno cuadro, CuadroTurnoRequest request,
                                                Macroprocesos macro, Procesos proceso, Servicio servicio,
                                                SeccionesServicio seccion, SubseccionesServicio subseccion,
                                                Equipo equipo) {
        // Limpiar relaciones que no corresponden a la nueva categoría
        limpiarRelacionesExcepto(cuadro, cuadro.getIdCuadroTurno(), request.getCategoria().toLowerCase());

        // Configurar nueva relación
        configurarCuadroSegunCategoria(cuadro, request, macro, proceso, servicio, seccion, subseccion, equipo);
    }

    // Método auxiliar para procesar procesos de atención en creación
    private void procesarProcesosAtencionParaCreacion(List<Long> idsProcesoBase, CuadroTurno cuadro) {
        for (Long idProcesoBase : idsProcesoBase) {
            Procesos procesoBase = procesosRepository.findById(idProcesoBase)
                    .orElseThrow(() -> new EntityNotFoundException("Proceso base no encontrado con ID: " + idProcesoBase));

            ProcesosAtencion nuevoProcesoAtencion = new ProcesosAtencion();
            nuevoProcesoAtencion.setProcesos(procesoBase);
            nuevoProcesoAtencion.setDetalle(procesoBase.getNombre());
            nuevoProcesoAtencion.setCuadroTurno(cuadro);

            procesosAtencionRepository.save(nuevoProcesoAtencion);
            cambiosCuadroTurnoService.registrarCambioProcesosAtencion(nuevoProcesoAtencion, "CREACION");
        }
    }

    // Método para obtener vista previa del nombre (opcional, para el frontend)
    public String obtenerVistaPreviaNombre(CuadroTurnoRequest request) {
        // Crear un cuadro temporal solo para generar el nombre
        CuadroTurno cuadroTemporal = new CuadroTurno();

        switch (request.getCategoria().toLowerCase()) {
            case "macroproceso":
                if (request.getIdMacroproceso() != null) {
                    cuadroTemporal.setMacroProcesos(buscarMacroproceso(request.getIdMacroproceso()));
                }
                break;
            case "proceso":
                if (request.getIdProceso() != null) {
                    cuadroTemporal.setProcesos(buscarProceso(request.getIdProceso()));
                }
                break;
            case "servicio":
                if (request.getIdServicio() != null) {
                    cuadroTemporal.setServicios(buscarServicio(request.getIdServicio()));
                }
                break;
            case "seccion":
                if (request.getIdSeccionServicio() != null) {
                    cuadroTemporal.setSeccionesServicios(buscarSeccionServicio(request.getIdSeccionServicio()));
                }
                break;
            case "subseccion":
                if (request.getIdSubseccionServicio() != null) {
                    cuadroTemporal.setSubseccionesServicios(buscarSubseccionServicio(request.getIdSubseccionServicio()));
                }
                break;
            case "multiproceso":
                // Para multiproceso, generar un nombre genérico
                break;
        }

        if (request.getIdEquipo() != null) {
            cuadroTemporal.setEquipos(buscarEquipo(request.getIdEquipo()));
        }

        return generarNombreCuadroTurno(cuadroTemporal);
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

    // Métodos auxiliares
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
        // Para nuevos cuadros, siempre empezar con v1
        if (versionAnterior == null) {
            return baseVersion + "_v1";
        }
        // Si la versión anterior corresponde al mismo periodo, mantenerla
        if (versionAnterior.startsWith(baseVersion)) {
            return versionAnterior;
        }
        // Si es un periodo diferente, empezar con v1
        return baseVersion + "_v1";
    }
}
