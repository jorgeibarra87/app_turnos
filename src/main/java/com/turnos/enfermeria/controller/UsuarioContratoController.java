package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.UsuarioContratoDTO;
import com.turnos.enfermeria.model.dto.UsuarioContratoTotalDTO;
import com.turnos.enfermeria.service.UsuarioContratoService;
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
@RequestMapping("/usuarioContrato")
//@Tag(name = "Usuario de Contrato", description = "Operaciones relacionadas con los Usuarios responsables de los contratos")
public class UsuarioContratoController {
    @Autowired
    private UsuarioContratoService usuarioContratoService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo usuario de contrato",
            description = "Registra un nuevo usuario de contrato con sus datos personales y vinculaciones.",
            tags={"Contratos"}
    )
    public ResponseEntity<UsuarioContratoDTO> create(@RequestBody UsuarioContratoDTO usuarioContratoDTO){
        try {
            UsuarioContratoDTO nuevoUsuarioContratoDTO = usuarioContratoService.create(usuarioContratoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuarioContratoDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.USUARIO_CONTRATO_NO_ENCONTRADO,
                    usuarioContratoDTO.getIdUsuarioContrato(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.USUARIO_CONTRATO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.USUARIO_CONTRATO_ESTADO_INVALIDO,
                    "No se pudo crear usuario contrato: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los usuarios de contrato",
            description = "Devuelve una lista con todos los usuarios de contrato registrados en el sistema.",
            tags={"Contratos"}
    )
    public ResponseEntity<List<UsuarioContratoDTO>> findAll(){
        List<UsuarioContratoDTO> usuarioContratoDTO = usuarioContratoService.findAll();
        return usuarioContratoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(usuarioContratoDTO);
    }

    @GetMapping("/{idUsuarioContrato}")
    @Operation(
            summary = "Buscar usuario de contrato por ID",
            description = "Devuelve la información de un usuario de contrato a partir de su ID.",
            tags={"Contratos"}
    )
    public ResponseEntity<UsuarioContratoDTO> findById(@PathVariable Long idUsuarioContrato){
        return usuarioContratoService.findById(idUsuarioContrato)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.USUARIO_CONTRATO_NO_ENCONTRADO,
                        idUsuarioContrato,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idUsuarioContrato}")
    @Operation(summary = "Actualizar usuario de contrato",description = "Actualiza los datos de un usuario de contrato existente.",
            tags={"Contratos"})
    public ResponseEntity<UsuarioContratoDTO> update(@RequestBody UsuarioContratoDTO usuarioContratoDTO, @PathVariable Long idUsuarioContrato){
        return usuarioContratoService.findById(idUsuarioContrato)
                .map(usuarioContratoExistente -> ResponseEntity.ok(usuarioContratoService.update(usuarioContratoDTO, idUsuarioContrato)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.USUARIO_CONTRATO_NO_ENCONTRADO,
                        idUsuarioContrato,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @Operation(summary = "Consultar usuarios de contrato por documento",description = "Consultar los datos de un usuario de contrato existente, por documento",
            tags={"Contratos"})
    @GetMapping("/info/{documento}")
    public ResponseEntity<UsuarioContratoTotalDTO> obtenerInformacionUsuario(@PathVariable String documento) {
        try {
            UsuarioContratoTotalDTO dto = usuarioContratoService.obtenerInformacionUsuarioCompleta(documento);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
