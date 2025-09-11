package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.EquipoDTO;
import com.turnos.enfermeria.model.dto.EquipoSelectionDTO;
import com.turnos.enfermeria.model.dto.MiembroPerfilDTO;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.repository.*;
import com.turnos.enfermeria.service.EquipoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/equipo")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private MacroprocesosRepository macroprocesoRepository;
    @Autowired
    private ProcesosRepository procesoRepository;
    @Autowired
    private SeccionesServicioRepository seccionRepository;
    @Autowired
    private SubseccionesServicioRepository subseccionRepository;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo equipo",
            description = "Registra un nuevo equipo con su nombre, tipo y miembros asociados.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> create(@RequestBody EquipoDTO equipoDTO){
        try {
            EquipoDTO nuevoEquipoDTO = equipoService.create(equipoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEquipoDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.EQUIPO_NO_ENCONTRADO,
                    equipoDTO.getIdEquipo(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }  catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.EQUIPO_ESTADO_INVALIDO,
                    "No se pudo crear equipo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los equipos",
            description = "Devuelve una lista con todos los equipos registrados en el sistema.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<EquipoDTO>> findAll(){
        List<EquipoDTO> equipoDTO = equipoService.findAll();
        return equipoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(equipoDTO);
    }

    @GetMapping("/{idEquipo}")
    @Operation(
            summary = "Buscar equipo por ID",
            description = "Devuelve los datos de un equipo específico usando su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> findById(@PathVariable Long idEquipo){
        return equipoService.findById(idEquipo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.EQUIPO_NO_ENCONTRADO,
                        idEquipo,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idEquipo}")
    @Operation(
            summary = "Actualizar equipo",
            description = "Modifica los datos de un equipo existente, identificado por su ID.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> update(@RequestBody EquipoDTO equipoDTO, @PathVariable Long idEquipo){
        return equipoService.findById(idEquipo)
                .map(equipoExistente -> ResponseEntity.ok(equipoService.update(equipoDTO, idEquipo)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.EQUIPO_NO_ENCONTRADO,
                        idEquipo,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PostMapping("/with-generated-name")
    @Operation(
            summary = "Crear equipo con nombre generado automáticamente",
            description = "Crea un nuevo equipo generando automáticamente el nombre basado en categoría y subcategoría.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> createWithGeneratedName(@RequestBody EquipoSelectionDTO selection) {
        try {
            EquipoDTO equipoCreado = equipoService.createWithGeneratedName(selection);
            return ResponseEntity.status(HttpStatus.CREATED).body(equipoCreado);
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.EQUIPO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @PutMapping("/{id}/with-generated-name")
    @Operation(
            summary = "Actualizar equipo con nombre generado automáticamente",
            description = "Actualiza un equipo existente generando automáticamente un nuevo nombre basado en categoría y subcategoría.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> updateWithGeneratedName(
            @PathVariable Long id,
            @RequestBody EquipoSelectionDTO selection) {
        try {
            EquipoDTO equipoActualizado = equipoService.updateWithGeneratedName(id, selection);
            return ResponseEntity.ok(equipoActualizado);
        } catch (RuntimeException e) {
            throw new GenericNotFoundException(
                    CodigoError.EQUIPO_NO_ENCONTRADO,
                    id,
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping("/subcategorias/{categoria}")
    @Operation(
            summary = "Obtener subcategorías por categoría",
            description = "Devuelve las opciones de subcategoría disponibles para una categoría específica.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<Map<String, Object>>> getSubcategorias(@PathVariable String categoria) {
        List<Map<String, Object>> subcategorias = new ArrayList<>();

        // REVERTIR: Mantener formato original - categorías con primera letra mayúscula
        switch (categoria) {
            case "Servicio":
                servicioRepository.findAll().forEach(servicio -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("idServicio", servicio.getIdServicio());
                    item.put("nombre", servicio.getNombre());
                    subcategorias.add(item);
                });
                break;

            case "Macroproceso":
                macroprocesoRepository.findAll().forEach(macroproceso -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("idMacroproceso", macroproceso.getIdMacroproceso());
                    item.put("nombre", macroproceso.getNombre());
                    subcategorias.add(item);
                });
                break;

            case "Proceso":
                procesoRepository.findAll().forEach(proceso -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("idProceso", proceso.getIdProceso());
                    item.put("nombre", proceso.getNombre());
                    subcategorias.add(item);
                });
                break;

            case "Seccion":
                seccionRepository.findAll().forEach(seccion -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("idSeccionServicio", seccion.getIdSeccionServicio());
                    item.put("nombre", seccion.getNombre());
                    subcategorias.add(item);
                });
                break;

            case "Subseccion":
                subseccionRepository.findAll().forEach(subseccion -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("idSubseccionServicio", subseccion.getIdSubseccionServicio());
                    item.put("nombre", subseccion.getNombre());
                    subcategorias.add(item);
                });
                break;

            default:
                throw new GenericBadRequestException(
                        CodigoError.EQUIPO_DATOS_INVALIDOS,
                        "Categoría no válida: " + categoria,
                        request.getMethod(),
                        request.getRequestURI()
                );
        }

        return ResponseEntity.ok(subcategorias);
    }

    @PostMapping("/generate-name")
    @Operation(
            summary = "Generar nombre de equipo",
            description = "Genera un nombre único para un equipo basado en la categoría y subcategoría proporcionadas.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<String> generateEquipoName(@RequestBody EquipoSelectionDTO selection) {
        try {
            String generatedName = equipoService.generateEquipoName(selection);
            return ResponseEntity.ok(generatedName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generando nombre: " + e.getMessage());
        }
    }

    @PostMapping("/equipoNombre")
    @Operation(
            summary = "Crear equipo con nombre generado (Legacy)",
            description = "Método legacy para crear equipos con nombre generado. Usar /with-generated-name en su lugar.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<Equipo> createEquipoWithGeneratedName(@RequestBody EquipoSelectionDTO selection) {
        try {
            Equipo equipo = equipoService.createEquipoWithGeneratedName(selection);
            return ResponseEntity.ok(equipo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{idEquipo}/actualizar-nombre")
    @Operation(
            summary = "Actualizar nombre del equipo (Legacy)",
            description = "Método legacy para actualizar nombres. Usar /{id}/with-generated-name en su lugar.",
            tags = {"Cuadro de Turnos"}
    )
    public ResponseEntity<EquipoDTO> updateNombreGenerado(
            @PathVariable Long idEquipo,
            @RequestBody EquipoSelectionDTO selection
    ) {
        try {
            EquipoDTO actualizado = equipoService.updateWithGeneratedName(idEquipo, selection);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            throw new GenericNotFoundException(
                    CodigoError.EQUIPO_NO_ENCONTRADO,
                    idEquipo,
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
    }

    @GetMapping("/{id}/miembros-perfil")
    @Operation(
            summary = "Obtener miembros del equipo con perfil",
            description = "Devuelve la lista de miembros del equipo junto con su información de perfil.",
            tags={"Cuadro de Turnos"}
    )
    public ResponseEntity<List<MiembroPerfilDTO>> obtenerMiembrosConPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(equipoService.obtenerMiembrosConPerfil(id));
    }
}
