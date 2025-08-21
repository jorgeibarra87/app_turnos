package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.RolesDTO;
import com.turnos.enfermeria.service.RolesService;
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
@RequestMapping("/roles")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(summary = "Crear rol", description = "Crea un nuevo rol en el sistema",
            tags={"Usuarios"})
    public ResponseEntity<RolesDTO> create(@RequestBody RolesDTO rolesDTO){
        try {
            RolesDTO nuevoRolesDTO = rolesService.create(rolesDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRolesDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.ROL_NO_ENCONTRADO,
                    rolesDTO.getIdRol(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.ROL_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.ROL_ESTADO_INVALIDO,
                    "No se pudo crear rol: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el rol: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping
    @Operation(summary = "Listar roles", description = "Obtiene todos los roles registrados en el sistema",
            tags={"Usuarios"})
    public ResponseEntity<List<RolesDTO>> findAll(){
        List<RolesDTO> rolesDTO = rolesService.findAll();
        return rolesDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(rolesDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID", description = "Devuelve el rol correspondiente al ID especificado",
            tags={"Usuarios"})
    public ResponseEntity<RolesDTO> findById(@PathVariable Long id){
        return rolesService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.ROL_NO_ENCONTRADO,
                        id,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping
    @Operation(summary = "Actualizar rol", description = "Actualiza la información de un rol existente por ID",
            tags={"Usuarios"})
    public ResponseEntity<RolesDTO> update(@RequestBody RolesDTO rolesDTO, @PathVariable Long id){
        return rolesService.findById(id)
                .map(roloExistente -> ResponseEntity.ok(rolesService.update(rolesDTO, id)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.ROL_NO_ENCONTRADO,
                        id,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

//    @DeleteMapping("/{id}")
//    @Operation(summary = "Eliminar rol", description = "Elimina un rol por su ID",
//            tags={"Usuarios"})
//    public ResponseEntity<Object> delete(@PathVariable Long id){
//        return rolesService.findById(id)
//                .map(rolesDTO-> {
//                    rolesService.delete(id);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.ROL_NO_ENCONTRADO,
//                        id,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }
}