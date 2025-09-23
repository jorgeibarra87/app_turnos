package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.CambiosEquipoDTO;
import com.turnos.enfermeria.model.dto.CambiosPersonaEquipoDTO;
import com.turnos.enfermeria.service.CambiosEquipoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cambios-equipo")
@CrossOrigin("http://localhost:5173/")
@AllArgsConstructor
@Tag(name = "Historial de Equipos", description = "Operaciones relacionadas con el Historial de Equipos")
public class CambiosEquipoController {

    private final CambiosEquipoService cambiosEquipoService;

    @Operation(
            summary = "Obtiene el historial de cambios de un equipo",
            description = "Obtiene el historial de cambios de un equipo en base a su id.",
            tags={"Historial de Equipos"}
    )
    @GetMapping("/historial/{equipoId}")
    public ResponseEntity<List<CambiosEquipoDTO>> obtenerHistorialEquipo(@PathVariable Long equipoId) {
        List<CambiosEquipoDTO> historial = cambiosEquipoService.obtenerHistorialEquipo(equipoId);
        return ResponseEntity.ok(historial);
    }

    @Operation(
            summary = "Obtiene el historial de cambios la persona de un equipo",
            description = "Obtiene el historial de cambios la persona de un equipo en base al id de la persona.",
            tags={"Historial de Equipos"}
    )
    @GetMapping("/historial-persona/{personaId}")
    public ResponseEntity<List<CambiosPersonaEquipoDTO>> obtenerHistorialPersona(@PathVariable Long personaId) {
        List<CambiosPersonaEquipoDTO> historial = cambiosEquipoService.obtenerHistorialPersona(personaId);
        return ResponseEntity.ok(historial);
    }

    @Operation(
            summary = "Obtiene el historial de cambios del cuadro de un equipo",
            description = "Obtiene el historial de cambios del cuadro de un equipo en base al id del cuadro.",
            tags={"Historial de Equipos"}
    )
    @GetMapping("/historial-cuadro/{cuadroId}")
    public ResponseEntity<Map<String, Object>> obtenerHistorialCompleto(@PathVariable Long cuadroId) {
        // Obtener equipos relacionados al cuadro y sus historiales
        Map<String, Object> historialCompleto = cambiosEquipoService.obtenerHistorialPorCuadro(cuadroId);
        return ResponseEntity.ok(historialCompleto);
    }
}
