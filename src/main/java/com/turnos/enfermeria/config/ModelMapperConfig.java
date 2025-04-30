package com.turnos.enfermeria.config;

import com.turnos.enfermeria.model.dto.TurnoDTO;
import com.turnos.enfermeria.model.entity.Turnos;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Desactiva la coincidencia ambigua
        modelMapper.getConfiguration()
                .setAmbiguityIgnored(true); // Evita excepciones si hay ambigüedad

        // Mapeo explícito del id de cuadroTurno al DTO
        modelMapper.typeMap(Turnos.class, TurnoDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getCuadroTurno().getIdCuadroTurno(), TurnoDTO::setIdCuadroTurno);
        });

        return modelMapper;
    }
}