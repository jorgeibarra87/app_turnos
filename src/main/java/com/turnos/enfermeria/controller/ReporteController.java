package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/{anio}/{mes}/{cuadroId}")
    public ResponseEntity<Map<String, Object>> generarReporte(
            @PathVariable int anio,
            @PathVariable int mes,
            @PathVariable Long cuadroId) {

        Map<String, Object> reporte = reporteService.generarReporte(anio, mes, cuadroId);
        return ResponseEntity.ok(reporte);
    }
}