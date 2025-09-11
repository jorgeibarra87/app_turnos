package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.service.ContratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/contrato")
@Tag(name = "Contratos", description = "Operaciones relacionadas con contratos de personal y títulos asociados")
public class ContratoController {

    @Autowired
    private ContratoService contratoService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo contrato",
            description = "Registra un nuevo contrato de personal con la información proporcionada.",
            tags={"Contratos"}
    )
    public ResponseEntity<ContratoDTO> create(@RequestBody ContratoDTO contratoDTO){
        try {
            ContratoDTO nuevoContratoDTO = contratoService.create(contratoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoContratoDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.CONTRATO_NO_ENCONTRADO,
                    contratoDTO.getIdContrato(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.CONTRATO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.CONTRATO_ESTADO_INVALIDO,
                    "No se pudo crear turno: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @GetMapping
    @Operation(
            summary = "Obtener todos los contratos",
            description = "Devuelve la lista completa de contratos registrados en el sistema.",
            tags={"Contratos"}
    )
    public ResponseEntity<List<ContratoDTO>> findAll(){
        List<ContratoDTO> contratoDTO = contratoService.findAll();
        return contratoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(contratoDTO);
    }

    @GetMapping("/{idContrato}")
    @Operation(
            summary = "Buscar contrato por ID",
            description = "Obtiene los detalles de un contrato específico utilizando su identificador único.",
            tags={"Contratos"}
    )
    public ResponseEntity<ContratoDTO> findById(@PathVariable Long idContrato){
        return contratoService.findById(idContrato)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.CONTRATO_NO_ENCONTRADO,
                        idContrato,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idContrato}")
    @Operation(
            summary = "Actualizar contrato existente",
            description = "Modifica los datos de un contrato previamente registrado, identificado por su ID.",
            tags={"Contratos"}
    )
    public ResponseEntity<ContratoDTO> update(@RequestBody ContratoDTO contratoDTO, @PathVariable Long idContrato){
        return contratoService.findById(idContrato)
                .map(contratoExistente -> ResponseEntity.ok(contratoService.update(contratoDTO, idContrato)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.CONTRATO_NO_ENCONTRADO,
                        idContrato,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }


    @PostMapping("{idContrato}/titulo/{idTitulo}")
    @Operation(
            summary = "Agregar título académico a contrato",
            description = "Asocia un título académico específico a un contrato existente.",
            tags={"Contratos"}
    )
    public ResponseEntity<List<TitulosFormacionAcademicaDTO>> agregarTituloAContrato(
            @PathVariable Long idContrato,
            @PathVariable Long idTitulo) {
        try {
            List<TitulosFormacionAcademicaDTO> titulos = contratoService.agregarTituloAContrato(idContrato, idTitulo);
            return ResponseEntity.ok(titulos);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TITULO_CONTRATO_NO_ENCONTRADO,
                    idTitulo,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TITULO_CONTRATO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TITULO_CONTRATO_DATOS_INVALIDOS,
                    "No se pudo crear titulo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @PostMapping("{idContrato}/proceso/{idProceso}")
    @Operation(
            summary = "Agregar proceso a contrato",
            description = "Asocia un proceso específico a un contrato existente.",
            tags={"Contratos"}
    )
    public ResponseEntity<List<ProcesosDTO>> agregarProcesoAContrato(
            @PathVariable Long idContrato,
            @PathVariable Long idProceso) {
        try {
            List<ProcesosDTO> procesos = contratoService.agregarProcesoAContrato(idContrato, idProceso);
            return ResponseEntity.ok(procesos);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.PROCESO_CONTRATO_NO_ENCONTRADO,
                    idProceso,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.PROCESO_CONTRATO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.PROCESO_CONTRATO_DATOS_INVALIDOS,
                    "No se pudo crear proceso: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @GetMapping("/{idContrato}/titulos")
    @Operation(
            summary = "Listar títulos de un contrato",
            description = "Obtiene todos los títulos académicos asociados a un contrato específico.",
            tags={"Contratos"}
    )
    public ResponseEntity<List<TitulosFormacionAcademicaDTO>> obtenerTitulosPorContrato(@PathVariable Long idContrato) {
        try {
            List<TitulosFormacionAcademicaDTO> usuarios = contratoService.obtenerTitulosPorContrato(idContrato);
            return ResponseEntity.ok(usuarios);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TITULO_CONTRATO_NO_ENCONTRADO,
                    idContrato,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TITULO_CONTRATO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TITULO_CONTRATO_ESTADO_INVALIDO,
                    "No se pudo obtener titulo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }


    @GetMapping("/{idContrato}/procesos")
    @Operation(
            summary = "Listar procesos de un contrato",
            description = "Obtiene todos los procesos asociados a un contrato específico.",
            tags={"Contratos"}
    )
    public ResponseEntity<List<ProcesosDTO>> obtenerProcesosPorContrato(@PathVariable Long idContrato) {
        try {
            List<ProcesosDTO> usuarios = contratoService.obtenerProcesosPorContrato(idContrato);
            return ResponseEntity.ok(usuarios);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.PROCESO_CONTRATO_NO_ENCONTRADO,
                    idContrato,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.PROCESO_CONTRATO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.PROCESO_CONTRATO_ESTADO_INVALIDO,
                    "No se pudo obtener proceso: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @PutMapping("titulo/{idTitulo}")
    @Operation(
            summary = "Actualizar título asociado a un contrato",
            description = "Permite reemplazar el título actual de un contrato con uno nuevo (se agrega el id del titulo en la ruta ej: http://localhost:8080/contrato/titulo/1 y el id del contrato en el cuerpo de la peticion entre corchetes ej: [1]).",
            tags={"Contratos"}
    )
    public ResponseEntity<TitulosFormacionAcademicaDTO> actualizarTitulosDeContrato(
            @PathVariable Long idTitulo,
            @RequestBody List<Long> nuevosTitulosIds) {
        try {
            TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO = contratoService.actualizarTitulosDeContrato(idTitulo, nuevosTitulosIds);
            return ResponseEntity.ok(titulosFormacionAcademicaDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TITULO_CONTRATO_NO_ENCONTRADO,
                    idTitulo,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TITULO_CONTRATO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TITULO_CONTRATO_DATOS_INVALIDOS,
                    "No se pudo obtener titulo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }


    @PutMapping("proceso/{idProceso}")
    @Operation(
            summary = "Actualizar proceso asociado a un contrato",
            description = "Permite reemplazar el proceso actual de un contrato con uno nuevo (se agrega el id del proceso en la ruta ej: http://localhost:8080/contrato/proceso/1 y el id del contrato en el cuerpo de la peticion entre corchetes ej: [1]).",
            tags={"Contratos"}
    )
    public ResponseEntity<ProcesosDTO> actualizarProcesosDeContrato(
            @PathVariable Long idProceso,
            @RequestBody List<Long> nuevosProcesosIds) {
        try {
            ProcesosDTO procesosDTO = contratoService.actualizarProcesosDeContrato(idProceso, nuevosProcesosIds);
            return ResponseEntity.ok(procesosDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.PROCESO_CONTRATO_NO_ENCONTRADO,
                    idProceso,
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.PROCESO_CONTRATO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.PROCESO_CONTRATO_DATOS_INVALIDOS,
                    "No se pudo obtener proceso: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }


    /**
     * Endpoint para guardar un nuevo contrato completo
     */
    @PostMapping("/guardar")
    @Operation(
            summary = "Guardar contrato completo",
            description = "Endpoint para guardar un nuevo contrato completo.",
            tags={"Contratos"}
    )
    public ResponseEntity<?> guardarContrato(@RequestBody ContratoTotalDTO contratoDTO) {
        try {
            // Validaciones básicas
            if (contratoDTO.getNumContrato() == null || contratoDTO.getNumContrato().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El número de contrato es obligatorio"));
            }

            if (contratoDTO.getSupervisor() == null || contratoDTO.getSupervisor().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El supervisor es obligatorio"));
            }

            if (contratoDTO.getContratista() == null || contratoDTO.getContratista().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El contratista es obligatorio"));
            }

            if (contratoDTO.getFechaInicio() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La fecha de inicio es obligatoria"));
            }

            if (contratoDTO.getFechaTerminacion() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La fecha de terminación es obligatoria"));
            }

            // Validar que la fecha de inicio sea anterior a la fecha de terminación
            if (contratoDTO.getFechaInicio().isAfter(contratoDTO.getFechaTerminacion())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La fecha de inicio debe ser anterior a la fecha de terminación"));
            }

            Contrato contratoGuardado = contratoService.guardarContratoCompleto(contratoDTO);

            return ResponseEntity.ok(Map.of(
                    "message", "Contrato guardado exitosamente",
                    "contrato", contratoGuardado,
                    "id", contratoGuardado.getIdContrato()
            ));

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("num_contrato")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Ya existe un contrato con este número"));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error de integridad de datos: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para actualizar un contrato existente
     */
    @PutMapping("/actualizar/{id}")
    @Operation(
            summary = "Actualizar contrato completo",
            description = "Endpoint para actualizar un contrato existente completo.",
            tags={"Contratos"}
    )
    public ResponseEntity<?> actualizarContrato(
            @PathVariable Long id,
            @RequestBody ContratoTotalDTO contratoDTO) {
        try {
            Contrato contratoActualizado = contratoService.actualizarContratoCompleto(id, contratoDTO);

            return ResponseEntity.ok(Map.of(
                    "message", "Contrato actualizado exitosamente",
                    "contrato", contratoActualizado
            ));

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Contrato no encontrado"));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @GetMapping("contratoTotal/{idContrato}")
    @Operation(
            summary = "Buscar contrato por ID",
            description = "Obtiene los detalles de un contrato específico utilizando su identificador único.",
            tags={"Contratos"}
    )public ResponseEntity<ContratoTotalDTO> obtenerContratoCompleto(@PathVariable Long idContrato){
        ContratoTotalDTO contratoDTO = contratoService.obtenerContratoCompleto(idContrato);
        return ResponseEntity.ok(contratoDTO);
        //return contratoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(contratoDTO);
    }


    /**
     * Endpoint para verificar si un número de contrato ya existe
     */
    @GetMapping("/verificar-numero/{numContrato}")
    public ResponseEntity<Map<String, Boolean>> verificarNumeroContrato(@PathVariable String numContrato) {
        try {
            boolean existe = contratoService.existeNumeroContrato(numContrato);
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", true));
        }
    }
}
