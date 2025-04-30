package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.ServicioDTO;
import com.turnos.enfermeria.model.entity.Servicio;
import com.turnos.enfermeria.service.ServicioService;
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
@RequestMapping("/servicio")
@Tag(name = "Servicios", description = "Operaciones relacionadas con los servicios del sistema")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @PostMapping
    @Operation(summary = "Crear servicio", description = "Registra un nuevo servicio en el sistema",
            tags={"Servicios", "Cuadro de Turnos"})
    public ResponseEntity<ServicioDTO> create(@RequestBody ServicioDTO servicioDTO){
        ServicioDTO nuevoServicioDTO = servicioService.create(servicioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicioDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todos los servicios", description = "Devuelve una lista con todos los servicios registrados",
            tags={"Servicios", "Cuadro de Turnos"})
    public ResponseEntity<List<ServicioDTO>> findAll(){
        List<ServicioDTO> servicioDTO = servicioService.findAll();
        return servicioDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(servicioDTO);
    }

    @GetMapping("/{idServicio}")
    @Operation(summary = "Obtener servicio por ID", description = "Devuelve un servicio específico según su identificador",
            tags={"Servicios", "Cuadro de Turnos"})
    public ResponseEntity<ServicioDTO> findById(@PathVariable Long idServicio){
        return servicioService.findById(idServicio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idServicio}")
    @Operation(summary = "Actualizar servicio", description = "Actualiza los datos de un servicio existente",
            tags={"Servicios", "Cuadro de Turnos"})
    public ResponseEntity<ServicioDTO> update(@RequestBody ServicioDTO servicioDTO, @PathVariable("idServicio") Long ideServicio){
        return servicioService.findById(ideServicio)
                .map(servicioExistente -> ResponseEntity.ok(servicioService.update(servicioDTO, ideServicio)))
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{idServicio}")
    @Operation(summary = "Eliminar servicio", description = "Elimina un servicio existente por su ID",
            tags={"Servicios", "Cuadro de Turnos"})
    public ResponseEntity<Object> delete(@PathVariable Long idServicio){
        return servicioService.findById(idServicio)
                .map(servicioDTO-> {
                    servicioService.delete(idServicio);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
