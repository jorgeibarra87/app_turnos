package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.ProcesosDTO;
import com.turnos.enfermeria.model.dto.TitulosFormacionAcademicaDTO;
import com.turnos.enfermeria.model.entity.Procesos;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcesosContratoMapper {
    public ProcesosDTO toDTO(Procesos procesos) {
        ProcesosDTO dto = new ProcesosDTO();
        dto.setIdProceso(procesos.getIdProceso());
        //dto.setProceso(titulosFormacionAcademica.getTitulo());
        dto.setNombre(procesos.getNombre());
        return dto;
    }

    public List<ProcesosDTO> toDTOList(List<Procesos> procesos) {
        return procesos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
