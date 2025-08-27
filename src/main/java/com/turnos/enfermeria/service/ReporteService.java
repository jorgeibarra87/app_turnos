package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.entity.Turnos;
import com.turnos.enfermeria.repository.TurnosRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final TurnosRepository turnoRepository;

    public ReporteService(TurnosRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    public Map<String, Object> generarReporte(int anio, int mes, Long cuadroTurnoId) {
        // Filtrar turnos por año, mes y cuadro de turno
        List<Turnos> turnos = turnoRepository.findAll().stream()
                .filter(t -> t.getFechaInicio().getYear() == anio &&
                        t.getFechaInicio().getMonthValue() == mes &&
                        (cuadroTurnoId == null ||
                                (t.getCuadroTurno() != null && t.getCuadroTurno().getIdCuadroTurno().equals(cuadroTurnoId))))
                .collect(Collectors.toList());

        // Estructura principal del reporte
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("anio", anio);
        reporte.put("mes", mes);
        reporte.put("cuadroTurnoId", cuadroTurnoId);

        // Listado de turnos detallados
        List<Map<String, Object>> detalleTurnos = turnos.stream().map(t -> {
            Map<String, Object> turnoMap = new HashMap<>();
            turnoMap.put("id", t.getIdTurno());
            turnoMap.put("fechaInicio", t.getFechaInicio());
            turnoMap.put("fechaFin", t.getFechaFin());
            turnoMap.put("jornada", t.getJornada());
            turnoMap.put("horas", t.getTotalHoras());
            turnoMap.put("usuario", t.getUsuario() != null ? t.getUsuario().getPersona().getNombreCompleto() : "Sin asignar");
            return turnoMap;
        }).collect(Collectors.toList());

        reporte.put("detalleTurnos", detalleTurnos);

        // Estadísticas: total horas por usuario
        Map<String, Integer> horasPorUsuario = new HashMap<>();
        for (Turnos t : turnos) {
            String usuario = t.getUsuario() != null ? t.getUsuario().getPersona().getNombreCompleto() : "Sin asignar";
            horasPorUsuario.put(usuario,
                    (int) (horasPorUsuario.getOrDefault(usuario, 0) + t.getTotalHoras()));
        }

        reporte.put("horasPorUsuario", horasPorUsuario);

        return reporte;
    }


}
