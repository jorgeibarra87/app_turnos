package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.CambiosCuadroTurnoDTO;
import com.turnos.enfermeria.model.dto.CuadroTurnoDTO;
import com.turnos.enfermeria.model.dto.CuadroTurnoRequest;
import com.turnos.enfermeria.model.dto.TurnoDTO;
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
    private final ProcesosAtencionRepository procesosAtencionRepository;
    private final CambiosCuadroTurnoRepository cambiosCuadroTurnoRepository;
    private final CambiosCuadroTurnoService cambiosCuadroTurnoService;
    //private final CuadroTurnoRequest cuadroTurnoRequest;
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
            }
        }

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
//        ProcesosAtencion procesosAtencion = null;
//        if (cuadroTurnoDTO.getIdProcesosAtencion() != null) {
//            procesosAtencion = procesosAtencionRepository.findById(cuadroTurnoDTO.getIdProcesosAtencion())
//                    .orElseThrow(() -> new RuntimeException("Proceso Atencion no encontrado."));
//        }
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
//        ProcesosAtencion procesosAtencion = null;
//        if (cuadroTurnoDTO.getIdProcesosAtencion() != null) {
//            procesosAtencion = procesosAtencionRepository.findById(cuadroTurnoDTO.getIdProcesosAtencion())
//                    .orElseThrow(() -> new RuntimeException("Proceso Atencion no encontrado."));
//        }

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
        //cambio.setProcesoAtencion(procesosAtencion);
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




    /**
     * Crea un nuevo cuadro de turno con nombre generado automáticamente
     * Ahora soporta múltiples procesos de atención
     */
    public CuadroTurno crearCuadroTurnoTotal(CuadroTurnoRequest request) {
        CuadroTurno cuadroTurno = new CuadroTurno();

        // Establecer las relaciones basadas en la selección del usuario
        if (request.getIdMacroproceso() != null) {
            Macroprocesos macroproceso = macroprocesosRepository.findById(request.getIdMacroproceso())
                    .orElseThrow(() -> new EntityNotFoundException("Macroproceso no encontrado"));
            cuadroTurno.setMacroProcesos(macroproceso);
        }

        if (request.getIdProceso() != null) {
            Procesos proceso = procesosRepository.findById(request.getIdProceso())
                    .orElseThrow(() -> new EntityNotFoundException("Proceso no encontrado"));
            cuadroTurno.setProcesos(proceso);
        }

        if (request.getIdServicio() != null) {
            Servicio servicio = servicioRepository.findById(request.getIdServicio())
                    .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));
            cuadroTurno.setServicios(servicio);
        }

        if (request.getIdSeccionServicio() != null) {
            SeccionesServicio seccionServicio = seccionesServicioRepository.findById(request.getIdSeccionServicio())
                    .orElseThrow(() -> new EntityNotFoundException("Sección de servicio no encontrada"));
            cuadroTurno.setSeccionesServicios(seccionServicio);
        }

        // NUEVO: Manejar múltiples procesos de atención
        if (request.getIdsProcesosAtencion() != null && !request.getIdsProcesosAtencion().isEmpty()) {
            List<ProcesosAtencion> procesosAtencion = new ArrayList<>();

            for (Long idProcesoAtencion : request.getIdsProcesosAtencion()) {
                ProcesosAtencion procesoAtencion = procesosAtencionRepository.findById(idProcesoAtencion)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Proceso de atención no encontrado con ID: " + idProcesoAtencion));
                procesosAtencion.add(procesoAtencion);
            }

            // Asumir que CuadroTurno tiene una relación @OneToMany o @ManyToMany con ProcesosAtencion
            cuadroTurno.setProcesosAtencion(procesosAtencion);
        }

        if (request.getIdEquipo() != null) {
            Equipo equipo = equipoRepository.findById(request.getIdEquipo())
                    .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));
            cuadroTurno.setEquipos(equipo);
        }

        // Establecer año y mes
        cuadroTurno.setAnio(request.getAnio());
        cuadroTurno.setMes(request.getMes());

        // Generar el nombre automáticamente
        String nombreGenerado = generarNombreCuadroTurno(cuadroTurno);
        cuadroTurno.setNombre(nombreGenerado);

        // Generar versión
        String version = generarVersionCuadroTurno(cuadroTurno);
        cuadroTurno.setVersion(version);

        // Establecer valores por defecto
        cuadroTurno.setEstadoCuadro("abierto");
        cuadroTurno.setTurnoExcepcion(false);

        return cuadroTurnoRepository.save(cuadroTurno);
    }

    /**
     * Genera el nombre del cuadro de turno basado en la selección del usuario
     * Formato: CT_{Categoria}_{Identificador}_{Año}_{Mes}_{Equipo}
     * ACTUALIZADO: Maneja múltiples procesos de atención
     */
    private String generarNombreCuadroTurno(CuadroTurno cuadroTurno) {
        StringBuilder nombreBuilder = new StringBuilder("CT_");

        // Determinar la categoría principal seleccionada
        String categoria = determinarCategoriaPrincipal(cuadroTurno);
        nombreBuilder.append(categoria).append("_");

        // Agregar identificador específico
        String identificador = obtenerIdentificadorEspecifico(cuadroTurno, categoria);
        nombreBuilder.append(identificador).append("_");

        // Agregar año y mes
        nombreBuilder.append(cuadroTurno.getAnio()).append("_")
                .append(String.format("%02d", Integer.parseInt(cuadroTurno.getMes())));

        // Agregar equipo si está disponible
        if (cuadroTurno.getEquipos() != null) {
            String equipoNombre = limpiarNombreParaId(cuadroTurno.getEquipos().getNombre());
            nombreBuilder.append("_").append(equipoNombre);
        }

        return nombreBuilder.toString();
    }

    /**
     * Determina la categoría principal basada en la jerarquía de selección
     * ACTUALIZADO: Considera múltiples procesos de atención
     */
    private String determinarCategoriaPrincipal(CuadroTurno cuadroTurno) {
        if (cuadroTurno.getProcesosAtencion() != null && !cuadroTurno.getProcesosAtencion().isEmpty()) {
            return "ProcesoAtencion";
        } else if (cuadroTurno.getSeccionesServicios() != null) {
            return "Seccion";
        } else if (cuadroTurno.getServicios() != null) {
            return "Servicio";
        } else if (cuadroTurno.getProcesos() != null) {
            return "Proceso";
        } else if (cuadroTurno.getMacroProcesos() != null) {
            return "Macroproceso";
        } else {
            return "General";
        }
    }

    /**
     * Obtiene el identificador específico basado en la categoría
     * ACTUALIZADO: Maneja múltiples procesos de atención
     */
    private String obtenerIdentificadorEspecifico(CuadroTurno cuadroTurno, String categoria) {
        switch (categoria) {
            case "ProcesoAtencion":
                // Para múltiples procesos, crear un identificador compuesto
                if (cuadroTurno.getProcesosAtencion() != null && !cuadroTurno.getProcesosAtencion().isEmpty()) {
                    if (cuadroTurno.getProcesosAtencion().size() == 1) {
                        // Un solo proceso
                        return limpiarNombreParaId(cuadroTurno.getProcesosAtencion().get(0).getDetalle());
                    } else {
                        // Múltiples procesos: usar un identificador genérico más contador
                        return "MULTIPROCESO_" + cuadroTurno.getProcesosAtencion().size();
                    }
                }
                return "PROCESO_ATENCION";
            case "Seccion":
                return limpiarNombreParaId(cuadroTurno.getSeccionesServicios().getNombre());
            case "Servicio":
                return limpiarNombreParaId(cuadroTurno.getServicios().getNombre());
            case "Proceso":
                return limpiarNombreParaId(cuadroTurno.getProcesos().getNombre());
            case "Macroproceso":
                return limpiarNombreParaId(cuadroTurno.getMacroProcesos().getNombre());
            default:
                return "GENERAL";
        }
    }

    /**
     * Limpia el nombre para usar como identificador (sin espacios, caracteres especiales)
     */
    private String limpiarNombreParaId(String nombre) {
        if (nombre == null) return "UNKNOWN";

        return nombre.replaceAll("[^a-zA-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "")
                .toUpperCase();
    }

    /**
     * Genera la versión del cuadro de turno
     * ACTUALIZADO: Considera múltiples procesos de atención en el conteo
     */
    private String generarVersionCuadroTurno(CuadroTurno cuadroTurno) {
        // Contar cuántos cuadros similares existen para este período
        Long count = cuadroTurnoRepository.countByAnioAndMesAndCategoria(
                cuadroTurno.getAnio(),
                cuadroTurno.getMes(),
                determinarCategoriaPrincipal(cuadroTurno)
        );

        return String.format("v%02d_%s%s",
                count + 1,
                cuadroTurno.getMes().length() == 1 ? "0" + cuadroTurno.getMes() : cuadroTurno.getMes(),
                cuadroTurno.getAnio().substring(2)
        );
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
                request.getIdsProcesosAtencion(), // Ahora es una lista
                request.getIdEquipo(),
                request.getAnio(),
                request.getMes()
        );
    }


}