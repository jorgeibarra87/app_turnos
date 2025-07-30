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
//@Tag(name = "Equipo", description = "Gestión de equipos de trabajo en el sistema de turnos")
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
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.EQUIPO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.EQUIPO_ESTADO_INVALIDO,
                    "No se pudo crear equipo: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (Exception e) {
            throw new GenericBadRequestException(
                    CodigoError.ERROR_PROCESAMIENTO,
                    "Error al crear el equipo: " + e.getMessage(),
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


    /**
     * Obtiene las opciones de subcategoría según la categoría seleccionada
     */
    @GetMapping("/subcategorias/{categoria}")
    public ResponseEntity<List<Map<String, Object>>> getSubcategorias(@PathVariable String categoria) {
        List<Map<String, Object>> subcategorias = new ArrayList<>();

        switch (categoria.toUpperCase()) {
            case "SERVICIO":
                servicioRepository.findAll().forEach(servicio -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", servicio.getIdServicio());
                    item.put("nombre", servicio.getNombre());
                    subcategorias.add(item);
                });
                break;

            case "MACROPROCESO":
                macroprocesoRepository.findAll().forEach(macroproceso -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", macroproceso.getIdMacroproceso());
                    item.put("nombre", macroproceso.getNombre());
                    subcategorias.add(item);
                });
                break;

            case "PROCESO":
                procesoRepository.findAll().forEach(proceso -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", proceso.getIdProceso());
                    item.put("nombre", proceso.getNombre());
                    subcategorias.add(item);
                });
                break;

            case "SECCION":
                seccionRepository.findAll().forEach(seccion -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", seccion.getIdSeccionServicio());
                    item.put("nombre", seccion.getNombre());
                    subcategorias.add(item);
                });
                break;

            case "SUBSECCION":
                subseccionRepository.findAll().forEach(subseccion -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", subseccion.getIdSubseccionServicio());
                    item.put("nombre", subseccion.getNombre());
                    subcategorias.add(item);
                });
                break;

            case "MULTIPROCESO":
                Map<String, Object> item = new HashMap<>();
                item.put("id", "MULTIPROCESO");
                item.put("nombre", "Multiproceso");
                subcategorias.add(item);
                break;
        }

        return ResponseEntity.ok(subcategorias);
    }

    /**
     * Genera un nombre de equipo basado en la selección
     */
    @PostMapping("/generate-name")
    public ResponseEntity<String> generateEquipoName(@RequestBody EquipoSelectionDTO selection) {
        try {
            String generatedName = equipoService.generateEquipoName(selection);
            return ResponseEntity.ok(generatedName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generando nombre: " + e.getMessage());
        }
    }

    /**
     * Crea un nuevo equipo con nombre generado
     */
    @PostMapping("/equipoNombre")
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
            summary = "Actualizar nombre del equipo con generación automática",
            description = "Genera y asigna un nuevo nombre al equipo usando su categoría y subcategoría.",
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
    public ResponseEntity<List<MiembroPerfilDTO>> obtenerMiembrosConPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(equipoService.obtenerMiembrosConPerfil(id));
    }

//    /**
//     * Crea un equipo basado en un perfil específico
//     */
//    // OPCIÓN 1: Usando @RequestParam (más simple, para pocos usuarios)
//    @PostMapping("/crear-por-perfil/{idTitulo}")
//    @Operation(
//            summary = "crear por perfil",
//            description = "Crea un equipo basado en un perfil específico con usuarios seleccionados.",
//            tags={"Cuadro de Turnos"}
//    )
//    public ResponseEntity<EquipoDTO> crearEquipoPorPerfil(
//            @PathVariable Long idTitulo,
//            @RequestBody(required = false)  List<Long> idsUsuarios) {
//        try {
//            EquipoDTO equipoCreado = equipoService.createEquipoByPerfil(idTitulo, idsUsuarios);
//            return ResponseEntity.ok(equipoCreado);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    /**
//     * Obtiene todos los equipos de un perfil específico
//     */
//    @GetMapping("/por-perfil/{idTitulo}")
//    @Operation(
//            summary = "equipos por perfil",
//            description = "Obtiene todos los equipos de un perfil específico.",
//            tags={"Cuadro de Turnos"}
//    )
//    public ResponseEntity<List<EquipoDTO>> obtenerEquiposPorPerfil(@PathVariable Long idTitulo) {
//        try {
//            List<EquipoDTO> equipos = equipoService.findEquiposByPerfil(idTitulo);
//            return ResponseEntity.ok(equipos);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    /**
//     * Cuenta los equipos de un perfil específico
//     */
//    @GetMapping("/contar-por-perfil/{idTitulo}")
//    @Operation(
//            summary = "cuenta equipos por perfil",
//            description = "Cuenta los equipos de un perfil específico.",
//            tags={"Cuadro de Turnos"}
//    )
//    public ResponseEntity<Long> contarEquiposPorPerfil(@PathVariable Long idTitulo) {
//        try {
//            long cantidad = equipoService.contarEquiposByPerfil(idTitulo);
//            return ResponseEntity.ok(cantidad);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//
//    public class CrearEquipoRequest {
//        private Long idTitulo;
//        private List<Long> idsUsuarios;
//
//        // Constructores
//        public CrearEquipoRequest() {}
//
//        // Getters y Setters
//        public Long getIdTitulo() { return idTitulo; }
//        public void setIdTitulo(Long idTitulo) { this.idTitulo = idTitulo; }
//
//        public List<Long> getIdsUsuarios() { return idsUsuarios; }
//        public void setIdsUsuarios(List<Long> idsUsuarios) { this.idsUsuarios = idsUsuarios; }
//    }
}
