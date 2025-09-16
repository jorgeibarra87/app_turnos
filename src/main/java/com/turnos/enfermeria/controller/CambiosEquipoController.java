package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.CambiosEquipoDTO;
import com.turnos.enfermeria.model.dto.CambiosPersonaEquipoDTO;
import com.turnos.enfermeria.service.CambiosEquipoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cambios-equipo")
@CrossOrigin("http://localhost:5173/")
@AllArgsConstructor
public class CambiosEquipoController {

    private final CambiosEquipoService cambiosEquipoService;

    @GetMapping("/historial/{equipoId}")
    public ResponseEntity<List<CambiosEquipoDTO>> obtenerHistorialEquipo(@PathVariable Long equipoId) {
        List<CambiosEquipoDTO> historial = cambiosEquipoService.obtenerHistorialEquipo(equipoId);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/historial-persona/{personaId}")
    public ResponseEntity<List<CambiosPersonaEquipoDTO>> obtenerHistorialPersona(@PathVariable Long personaId) {
        List<CambiosPersonaEquipoDTO> historial = cambiosEquipoService.obtenerHistorialPersona(personaId);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/historial-cuadro/{cuadroId}")
    public ResponseEntity<Map<String, Object>> obtenerHistorialCompleto(@PathVariable Long cuadroId) {
        // Obtener equipos relacionados al cuadro y sus historiales
        Map<String, Object> historialCompleto = cambiosEquipoService.obtenerHistorialPorCuadro(cuadroId);
        return ResponseEntity.ok(historialCompleto);
    }
}
