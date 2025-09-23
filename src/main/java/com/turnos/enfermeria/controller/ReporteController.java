package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/reportes")
@Tag(name = "Reportes", description = "Operaciones relacionadas con la Generacion de Reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @Operation(
            summary = "Generar reporte de turnos con filtros",
            description = "Genera el reporte de los turnos que tienen las personas en base al mes, el cuadro de turno y la persona.",
            tags={"Reportes"}
    )
    @GetMapping("/{anio}/{mes}/{cuadroId}")
    public ResponseEntity<Map<String, Object>> generarReporte(
            @PathVariable int anio,
            @PathVariable int mes,
            @PathVariable Long cuadroId) {

        Map<String, Object> reporte = reporteService.generarReporte(anio, mes, cuadroId);
        return ResponseEntity.ok(reporte);
    }
}