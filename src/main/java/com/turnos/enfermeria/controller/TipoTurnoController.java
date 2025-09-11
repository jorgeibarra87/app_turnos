package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.TipoTurnoDTO;
import com.turnos.enfermeria.service.TipoTurnoService;
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
@RequestMapping("/tipoturno")
//@Tag(name = "Tipo de Turno", description = "Operaciones CRUD sobre los tipos de turno configurables en el sistema de turnos")
public class TipoTurnoController {

    @Autowired
    private TipoTurnoService tipoTurnoService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(summary = "Crear tipo de turno", description = "Crea un nuevo tipo de turno con los datos proporcionados",
            tags={"Contratos"})
    public ResponseEntity<TipoTurnoDTO> create(@RequestBody TipoTurnoDTO tipoTurnoDTO){
        try {
            TipoTurnoDTO nuevoTipoTurnoDTO = tipoTurnoService.create(tipoTurnoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipoTurnoDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.TIPO_TURNO_NO_ENCONTRADO,
                    tipoTurnoDTO.getIdTipoTurno(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.TIPO_TURNO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.TIPO_TURNO_ESTADO_INVALIDO,
                    "No se pudo crear turno: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @GetMapping
    @Operation(summary = "Listar tipos de turno", description = "Devuelve todos los tipos de turno registrados",
            tags={"Contratos"})
    public ResponseEntity<List<TipoTurnoDTO>> findAll(){
        List<TipoTurnoDTO> tipoTurnoDTO = tipoTurnoService.findAll();
        return tipoTurnoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tipoTurnoDTO);
    }

    @GetMapping("/{idTipoTurno}")
    @Operation(summary = "Buscar tipo de turno por ID", description = "Busca un tipo de turno por su identificador",
            tags={"Contratos"})
    public ResponseEntity<TipoTurnoDTO> findById(@PathVariable Long idTipoTurno){
        return tipoTurnoService.findById(idTipoTurno)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TIPO_TURNO_NO_ENCONTRADO,
                        idTipoTurno,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idTipoTurno}")
    @Operation(summary = "Actualizar tipo de turno", description = "Actualiza los datos de un tipo de turno existente",
            tags={"Contratos"})
    public ResponseEntity<TipoTurnoDTO> update(@RequestBody TipoTurnoDTO tipoTurnoDTO, @PathVariable Long idTipoTurno){
        return tipoTurnoService.findById(idTipoTurno)
                .map(tipoTurnoExistente -> ResponseEntity.ok(tipoTurnoService.update(tipoTurnoDTO, idTipoTurno)))
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{idTipoTurno}")
    @Operation(summary = "Eliminar tipo de turno", description = "Elimina un tipo de turno existente por su ID",
            tags={"Contratos"})
    public ResponseEntity<Object> delete(@PathVariable Long idTipoTurno){
        return tipoTurnoService.findById(idTipoTurno)
                .map(tipoTurnoDTO-> {
                    tipoTurnoService.delete(idTipoTurno);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.TIPO_TURNO_NO_ENCONTRADO,
                        idTipoTurno,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }
}
