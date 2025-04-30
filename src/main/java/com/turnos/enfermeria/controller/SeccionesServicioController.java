package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.SeccionesServicioDTO;
import com.turnos.enfermeria.model.entity.SeccionesServicio;
import com.turnos.enfermeria.service.SeccionesServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/seccionesServicio")
//@Tag(name = "Secciones de Servicio", description = "Gestión de secciones dentro de un servicio")
public class SeccionesServicioController {

    @Autowired
    private SeccionesServicioService seccionesServicioService;

    @PostMapping
    @Operation(summary = "Crear sección de servicio", description = "Registra una nueva sección dentro de un servicio",
            tags={"Servicios"})
    public ResponseEntity<SeccionesServicioDTO> create(@RequestBody SeccionesServicioDTO seccionesServicioDTO){
        SeccionesServicioDTO nuevoSeccionesServicioDTO = seccionesServicioService.create(seccionesServicioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoSeccionesServicioDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas las secciones", description = "Devuelve una lista de todas las secciones de servicio registradas",
            tags={"Servicios"})
    public ResponseEntity<List<SeccionesServicioDTO>> findAll(){
        List<SeccionesServicioDTO> seccionesServicioDTO = seccionesServicioService.findAll();
        return seccionesServicioDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(seccionesServicioDTO);
    }

    @GetMapping("/{idSeccionServicio}")
    @Operation(summary = "Obtener sección por ID", description = "Devuelve una sección de servicio por su ID",
            tags={"Servicios"})
    public ResponseEntity<SeccionesServicioDTO> findById(@PathVariable Long idSeccionServicio){
        return seccionesServicioService.findById(idSeccionServicio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idSeccionServicio}")
    @Operation(summary = "Actualizar sección", description = "Actualiza los datos de una sección de servicio existente",
            tags={"Servicios"})
    public ResponseEntity<SeccionesServicioDTO> ***REMOVED***(@RequestBody SeccionesServicioDTO seccionesServicioDTO, @PathVariable Long idSeccionServicio){
        return seccionesServicioService.findById(idSeccionServicio)
                .map(seccionesServicioExistente -> ResponseEntity.ok(seccionesServicioService.***REMOVED***(seccionesServicioDTO, idSeccionServicio)))
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{idSeccionServicio}")
    @Operation(summary = "Eliminar sección", description = "Elimina una sección de servicio por su ID",
            tags={"Servicios"})
    public ResponseEntity<Object> delete(@PathVariable Long idSeccionServicio){
        return seccionesServicioService.findById(idSeccionServicio)
                .map(seccionesServicioDTO-> {
                    seccionesServicioService.delete(idSeccionServicio);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
