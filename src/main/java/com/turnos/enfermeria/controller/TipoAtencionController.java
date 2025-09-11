package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.TipoAtencionDTO;
import com.turnos.enfermeria.service.TipoAtencionService;
import io.swagger.v3.oas.annotations.Operation;
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
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/tipoatencion")
//@Tag(name = "Tipo de Atención", description = "Operaciones CRUD sobre los distintos tipos de atención")
public class TipoAtencionController {

    @Autowired
    private TipoAtencionService tipoAtencionService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(summary = "Crear tipo de atención", description = "Crea un nuevo tipo de atención médica",
            tags={"Contratos"})
    public ResponseEntity<TipoAtencionDTO> create(@RequestBody TipoAtencionDTO tipoAtencionDTO){
        try {
            TipoAtencionDTO nuevoTipoAtencionDTO = tipoAtencionService.create(tipoAtencionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipoAtencionDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TIPO_ATENCION_NO_ENCONTRADA,
                    tipoAtencionDTO.getIdTipoAtencion(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TIPO_ATENCION_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TIPO_ATENCION_ESTADO_INVALIDO,
                    "No se pudo crear turno: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @GetMapping
    @Operation(summary = "Listar tipos de atención", description = "Obtiene una lista de todos los tipos de atención registrados",
            tags={"Contratos"})
    public ResponseEntity<List<TipoAtencionDTO>> findAll(){
        List<TipoAtencionDTO> tipoAtencionDTO = tipoAtencionService.findAll();
        return tipoAtencionDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tipoAtencionDTO);
    }

    @GetMapping("/{idTipoAtencion}")
    @Operation(summary = "Obtener tipo de atención por ID", description = "Busca un tipo de atención específico por su ID",
            tags={"Contratos"})
    public ResponseEntity<TipoAtencionDTO> findById(@PathVariable Long idTipoAtencion){
        return tipoAtencionService.findById(idTipoAtencion)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TIPO_ATENCION_NO_ENCONTRADA,
                        idTipoAtencion,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idTipoAtencion}")
    @Operation(summary = "Actualizar tipo de atención", description = "Actualiza la información de un tipo de atención existente",
            tags={"Contratos"})
    public ResponseEntity<TipoAtencionDTO> update(@RequestBody TipoAtencionDTO tipoAtencionDTO, @PathVariable("idTipoAtencion") Long idTipoAtencion){
        return tipoAtencionService.findById(idTipoAtencion)
                .map(tipoAtencionoExistente -> ResponseEntity.ok(tipoAtencionService.update(tipoAtencionDTO, idTipoAtencion)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TIPO_ATENCION_NO_ENCONTRADA,
                        idTipoAtencion,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

//    @DeleteMapping("/{idTipoAtencion}")
//    @Operation(summary = "Eliminar tipo de atención", description = "Elimina un tipo de atención por su ID",
//            tags={"Contratos"})
//    public ResponseEntity<Object> delete(@PathVariable Long idTipoAtencion){
//        return tipoAtencionService.findById(idTipoAtencion)
//                .map(tipoAtencionDTO-> {
//                    tipoAtencionService.delete(idTipoAtencion);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.TIPO_ATENCION_NO_ENCONTRADA,
//                        idTipoAtencion,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }
}
