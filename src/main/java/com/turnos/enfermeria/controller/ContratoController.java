package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.*;
import com.turnos.enfermeria.service.ContratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RestController
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

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el turno: " + e.getMessage(),
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
    public ResponseEntity<ContratoDTO> ***REMOVED***(@RequestBody ContratoDTO contratoDTO, @PathVariable Long idContrato){
        return contratoService.findById(idContrato)
                .map(contratoExistente -> ResponseEntity.ok(contratoService.***REMOVED***(contratoDTO, idContrato)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.CONTRATO_NO_ENCONTRADO,
                        idContrato,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

//    @DeleteMapping("/{idContrato}")
//    @Operation(
//            summary = "Eliminar contrato",
//            description = "Elimina un contrato del sistema usando su identificador.",
//            tags={"Contratos"}
//    )
//    public ResponseEntity<Object> delete(@PathVariable Long idContrato){
//        return contratoService.findById(idContrato)
//                .map(bloqueServicioDTO-> {
//                    contratoService.delete(idContrato);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.CONTRATO_NO_ENCONTRADO,
//                        idContrato,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }

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

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el titulo de contrato: " + e.getMessage(),
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

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al obtener el titulo de contrato: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @PutMapping("titulo/{idTitulo}")
    @Operation(
            summary = "Actualizar título asociado a un contrato",
            description = "Permite reemplazar el título actual de un contrato con uno nuevo (se agrega el id del titulo en la ruta ej: http://localhost:***REMOVED***/contrato/titulo/1 y el id del contrato en el cuerpo de la peticion entre corchetes ej: [1]).",
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

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al obtener el titulo de contrato: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }
}
