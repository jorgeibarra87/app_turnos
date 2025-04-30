package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.BloqueServicioDTO;
import com.turnos.enfermeria.model.dto.SubseccionesServicioDTO;
import com.turnos.enfermeria.model.entity.SubseccionesServicio;
import com.turnos.enfermeria.service.SubseccionesServicioService;
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
@RequestMapping("/subseccionesServicio")
//@Tag(name = "Subsecciones de Servicio", description = "Operaciones relacionadas con las subsecciones de los servicios")
public class SubseccionesServicioController {

    @Autowired
    private SubseccionesServicioService subseccionesServicioService;

    @PostMapping
    @Operation(summary = "Crear subsección de servicio", description = "Crea una nueva subsección asociada a un servicio",
            tags={"Servicios"})
    public ResponseEntity<SubseccionesServicioDTO> create(@RequestBody SubseccionesServicioDTO subseccionesServicioDTO){
        SubseccionesServicioDTO nuevoSubseccionesServicioDTO = subseccionesServicioService.create(subseccionesServicioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoSubseccionesServicioDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas las subsecciones", description = "Devuelve todas las subsecciones de servicio registradas",
            tags={"Servicios"})
    public ResponseEntity<List<SubseccionesServicioDTO>> findAll(){
        List<SubseccionesServicioDTO> subseccionesServicioDTO = subseccionesServicioService.findAll();
        return subseccionesServicioDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(subseccionesServicioDTO);
    }

    @GetMapping("/{idSubseccionServicio}")
    @Operation(summary = "Buscar subsección por ID", description = "Devuelve los datos de una subsección específica por su ID",
            tags={"Servicios"})
    public ResponseEntity<SubseccionesServicioDTO> findById(@PathVariable Long idSubseccionServicio){
        return subseccionesServicioService.findById(idSubseccionServicio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idSubseccionServicio}")
    @Operation(summary = "Actualizar subsección", description = "Actualiza los datos de una subsección existente",
            tags={"Servicios"})
    public ResponseEntity<SubseccionesServicioDTO> ***REMOVED***(@RequestBody SubseccionesServicioDTO subseccionesServicioDTO, @PathVariable("idSubseccionServicio") Long idSubseccionServicio){
        return subseccionesServicioService.findById(idSubseccionServicio)
                .map(subseccionesServicioExistente -> ResponseEntity.ok(subseccionesServicioService.***REMOVED***(subseccionesServicioDTO, idSubseccionServicio)))
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{idSubseccionServicio}")
    @Operation(summary = "Eliminar subsección", description = "Elimina una subsección existente por su ID",
            tags={"Servicios"})
    public ResponseEntity<Object> delete(@PathVariable Long idSubseccionServicio){
        return subseccionesServicioService.findById(idSubseccionServicio)
                .map(subseccionesServicioDTO-> {
                    subseccionesServicioService.delete(idSubseccionServicio);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
