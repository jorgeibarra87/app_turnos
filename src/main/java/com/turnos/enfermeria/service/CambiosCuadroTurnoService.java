package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.CambiosCuadroTurnoDTO;
import com.turnos.enfermeria.model.dto.TurnoDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

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
    private final SeccionesServicioRepository seccionesServicioRepository;
    private final ProcesosAtencionRepository procesosAtencionRepository;

    public CambiosCuadroTurnoDTO create(CambiosCuadroTurnoDTO cambiosCuadroTurnoDTO) {

        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(cambiosCuadroTurnoDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("Cuadro de turno no encontrado."));

        Macroprocesos macroprocesos = macroprocesosRepository.findById(cambiosCuadroTurnoDTO.getIdMacroproceso())
                .orElseThrow(() -> new RuntimeException("Macroproceso no encontrado."));

        Procesos procesos = procesosRepository.findById(cambiosCuadroTurnoDTO.getIdProcesos())
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        Servicio servicio = servicioRepository.findById(cambiosCuadroTurnoDTO.getIdServicios())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));

        SeccionesServicio seccionesServicio = seccionesServicioRepository.findById(cambiosCuadroTurnoDTO.getIdSeccionesServicios())
                .orElseThrow(() -> new RuntimeException("Seccion Servicio no encontrada."));

        ProcesosAtencion procesosAtencion = procesosAtencionRepository.findById(cambiosCuadroTurnoDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("Proceso Atencion no encontrado."));

        CambiosCuadroTurno cambiosCuadroTurno = modelMapper.map(cambiosCuadroTurnoDTO, CambiosCuadroTurno.class);
        cambiosCuadroTurno.setCuadroTurno(cuadroTurno);
        cambiosCuadroTurno.setMacroProcesos(macroprocesos);
        cambiosCuadroTurno.setProcesos(procesos);
        cambiosCuadroTurno.setServicios(servicio);
        cambiosCuadroTurno.setSeccionesServicios(seccionesServicio);
        //cambiosCuadroTurno.setProcesoAtencion(procesosAtencion);
        cambiosCuadroTurno.setFechaCambio(cambiosCuadroTurnoDTO.getFechaCambio());
        cambiosCuadroTurno.setNombre(cambiosCuadroTurnoDTO.getNombre());
        cambiosCuadroTurno.setMes(cambiosCuadroTurnoDTO.getMes());
        cambiosCuadroTurno.setAnio(cambiosCuadroTurnoDTO.getAnio());
        cambiosCuadroTurno.setEstadoCuadro(cambiosCuadroTurnoDTO.getEstadoCuadro());
        cambiosCuadroTurno.setVersion(cambiosCuadroTurnoDTO.getVersion());

        CambiosCuadroTurno cambiosCuadroTurnoGuardado = cambiosCuadroTurnoRepository.save(cambiosCuadroTurno);

        return modelMapper.map(cambiosCuadroTurnoGuardado, CambiosCuadroTurnoDTO.class);

    }

    public CambiosCuadroTurnoDTO update(CambiosCuadroTurnoDTO cambiosCuadroTurnoDetallesDTO, Long id) {
        CambiosCuadroTurno cambiosCuadroTurnoExistente = cambiosCuadroTurnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("cambio no encontrado"));

        CuadroTurno cuadroTurno = cuadroTurnoRepository.findById(cambiosCuadroTurnoDetallesDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("Cuadro de turno no encontrado."));

        Macroprocesos macroprocesos = macroprocesosRepository.findById(cambiosCuadroTurnoDetallesDTO.getIdMacroproceso())
                .orElseThrow(() -> new RuntimeException("Macroproceso no encontrado."));

        Procesos procesos = procesosRepository.findById(cambiosCuadroTurnoDetallesDTO.getIdProcesos())
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado."));

        Servicio servicio = servicioRepository.findById(cambiosCuadroTurnoDetallesDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));

        SeccionesServicio seccionesServicio = seccionesServicioRepository.findById(cambiosCuadroTurnoDetallesDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("Seccion Servicio no encontrada."));

        ProcesosAtencion procesosAtencion = procesosAtencionRepository.findById(cambiosCuadroTurnoDetallesDTO.getIdCuadroTurno())
                .orElseThrow(() -> new RuntimeException("Proceso Atencion no encontrado."));

        // Guardar el cambio en el historial antes de actualizar
        CambiosCuadroTurnoDTO cambiosCuadroTurnoDTO = modelMapper.map(cambiosCuadroTurnoExistente, CambiosCuadroTurnoDTO.class);

        // Actualizar los campos si no son nulos
        if (cambiosCuadroTurnoDetallesDTO.getIdCambioCuadro() != null) {
            cambiosCuadroTurnoExistente.setIdCambioCuadro(cambiosCuadroTurnoDetallesDTO.getIdCambioCuadro());
        }
        if (cambiosCuadroTurnoDetallesDTO.getIdCuadroTurno() != null) {
            cambiosCuadroTurnoExistente.setCuadroTurno(cuadroTurno);
        }
        if (cambiosCuadroTurnoDetallesDTO.getIdMacroproceso() != null) {
            cambiosCuadroTurnoExistente.setMacroProcesos(macroprocesos);
        }
        if (cambiosCuadroTurnoDetallesDTO.getIdProcesos() != null) {
            cambiosCuadroTurnoExistente.setProcesos(procesos);
        }
        if (cambiosCuadroTurnoDetallesDTO.getIdServicios() != null) {
            cambiosCuadroTurnoExistente.setServicios(servicio);
        }
        if (cambiosCuadroTurnoDetallesDTO.getIdSeccionesServicios() != null) {
            cambiosCuadroTurnoExistente.setSeccionesServicios(seccionesServicio);
        }
//        if (cambiosCuadroTurnoDetallesDTO.getIdProcesoAtencion()!= null) {
//            cambiosCuadroTurnoExistente.setProcesoAtencion(procesosAtencion);
//        }

        if (cambiosCuadroTurnoDetallesDTO.getFechaCambio() != null) {
            cambiosCuadroTurnoExistente.setFechaCambio(cambiosCuadroTurnoDetallesDTO.getFechaCambio());
        }

        if (cambiosCuadroTurnoDetallesDTO.getNombre() != null) {
            cambiosCuadroTurnoExistente.setNombre(cambiosCuadroTurnoDetallesDTO.getNombre());
        }

        if (cambiosCuadroTurnoDetallesDTO.getMes() != null) {
            cambiosCuadroTurnoExistente.setMes(cambiosCuadroTurnoDetallesDTO.getMes());
        }
        if (cambiosCuadroTurnoDetallesDTO.getAnio() != null) {
            cambiosCuadroTurnoExistente.setAnio(cambiosCuadroTurnoDetallesDTO.getAnio());
        }
        if (cambiosCuadroTurnoDetallesDTO.getEstadoCuadro() != null) {
            cambiosCuadroTurnoExistente.setEstadoCuadro(cambiosCuadroTurnoDetallesDTO.getEstadoCuadro());
        }
        if (cambiosCuadroTurnoDetallesDTO.getVersion() != null) {
            cambiosCuadroTurnoExistente.setVersion(cambiosCuadroTurnoDetallesDTO.getVersion());
        }

        // Guardar en la base de datos
        CambiosCuadroTurno cambiosCuadroTurnoActualizado = cambiosCuadroTurnoRepository.save(cambiosCuadroTurnoExistente);

        // Convertir a DTO antes de retornar
        return modelMapper.map(cambiosCuadroTurnoActualizado, CambiosCuadroTurnoDTO.class);
    }

    public Optional<CambiosCuadroTurnoDTO> findById(Long idCambioCuadro) {
        return cambiosCuadroTurnoRepository.findById(idCambioCuadro)
                .map(cambiosCuadroTurno -> modelMapper.map(cambiosCuadroTurno, CambiosCuadroTurnoDTO.class)); // Convertir a DTO
    }

    public List<CambiosCuadroTurnoDTO> findAll() {
        return cambiosCuadroTurnoRepository.findAll()
                .stream()
                .map(cambiosCuadroTurno -> modelMapper.map(cambiosCuadroTurno, CambiosCuadroTurnoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(@PathVariable Long idCambioCuadro) {
        // Buscar el bloque en la base de datos
        CambiosCuadroTurno cambiosCuadroTurnoEliminar = cambiosCuadroTurnoRepository.findById(idCambioCuadro)
                .orElseThrow(() -> new EntityNotFoundException("Bloque no encontrado"));

        // Convertir la entidad a DTO
        CambiosCuadroTurnoDTO cambiosCuadroTurnoDTO = modelMapper.map(cambiosCuadroTurnoEliminar, CambiosCuadroTurnoDTO.class);

        cambiosCuadroTurnoRepository.deleteById(idCambioCuadro);
    }

    /**
     * Registra un cambio en la tabla cambios_cuadro_turno
     * @param cuadroTurno Cuadro de turno original
     * @param tipoCambio Tipo de cambio: CREACION, MODIFICACION, ELIMINACION
     */
    @Transactional
    public void registrarCambioCuadroTurno(CuadroTurno cuadroTurno, String tipoCambio) {
        CambiosCuadroTurno cambio = new CambiosCuadroTurno();

        cambio.setCuadroTurno(cuadroTurno);
        cambio.setFechaCambio(LocalDateTime.now());
        cambio.setNombre(cuadroTurno.getNombre());
        cambio.setMes(cuadroTurno.getMes());
        cambio.setAnio(cuadroTurno.getAnio());
        cambio.setEstadoCuadro(cuadroTurno.getEstadoCuadro());
        cambio.setVersion(cuadroTurno.getVersion());
        cambio.setMacroProcesos(cuadroTurno.getMacroProcesos());
        cambio.setProcesos(cuadroTurno.getProcesos());
        cambio.setServicios(cuadroTurno.getServicios());
        //cambio.setProcesoAtencion(cuadroTurno.getProcesosAtencion());
        cambio.setSeccionesServicios(cuadroTurno.getSeccionesServicios());

        // Guardar el cambio en la BD
        cambiosCuadroTurnoRepository.save(cambio);
    }

    /**
     * Registra un cambio en la tabla cambios_procesos_atencion
     * @param procesosAtencion proceso del Cuadro de turno original
     * @param tipoCambio Tipo de cambio: CREACION, MODIFICACION, ELIMINACION
     */
    @Transactional
    public void registrarCambioProcesosAtencion(ProcesosAtencion procesosAtencion, String tipoCambio) {
        CambiosProcesosAtencion cambio = new CambiosProcesosAtencion();

        cambio.setCuadroTurno(procesosAtencion.getCuadroTurno());
        cambio.setProcesos(procesosAtencion.getProcesos());
        cambio.setEstado(procesosAtencion.getEstado());
        cambio.setDetalle(procesosAtencion.getDetalle());
        cambio.setFechaCambio(LocalDateTime.now());

        // Guardar el cambio en la BD
        cambiosProcesosAtencionRepository.save(cambio);
    }
}