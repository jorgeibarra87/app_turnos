package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.TitulosFormacionAcademicaDTO;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TitulosContratoMapper {
    public TitulosFormacionAcademicaDTO toDTO(TitulosFormacionAcademica titulosFormacionAcademica) {
        TitulosFormacionAcademicaDTO dto = new TitulosFormacionAcademicaDTO();
        dto.setIdTitulo(titulosFormacionAcademica.getIdTitulo());
        dto.setTitulo(titulosFormacionAcademica.getTitulo());
        dto.setIdTipoFormacionAcademica(titulosFormacionAcademica.getIdTipoFormacionAcademica());
        return dto;
    }

    public List<TitulosFormacionAcademicaDTO> toDTOList(List<TitulosFormacionAcademica> titulos) {
        return titulos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
