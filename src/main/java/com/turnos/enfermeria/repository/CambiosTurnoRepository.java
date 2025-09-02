package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.dto.CambiosTurnoDTO;
import com.turnos.enfermeria.model.entity.CambiosCuadroTurno;
import com.turnos.enfermeria.model.entity.CambiosTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CambiosTurnoRepository extends JpaRepository<CambiosTurno, Long> {

    /**
     * Encuentra el historial de cambios de un turno específico.
     */
    List<CambiosTurno> findByTurno_IdTurno(Long idTurno);

    /**
     * Encuentra el historial de cambios de turno con un  cuadro de turno específico.
     */
    List<CambiosTurno>findByCuadroTurno_IdCuadroTurno(Long idCuadroTurno);

    // Buscar todos los cambios de turno por versión
    List<CambiosTurno> findByVersion(String version);

    /**
     * Devuelve una lista de cambios de turno en formato DTO.
     */
    @Query("""
    SELECT new com.turnos.enfermeria.model.dto.CambiosTurnoDTO(
        ct.idCambio,
        ct.turno.idTurno,
        ct.turno.cuadroTurno.idCuadroTurno,
        ct.usuario.idPersona,
        ct.fechaCambio,
        ct.fechaInicio,
        ct.fechaFin,
        ct.totalHoras,
        ct.estadoTurno,
        ct.jornada,
        ct.tipoTurno,
        ct.version,
        ct.comentarios,
        ct.estado
    )
    FROM CambiosTurno ct
    WHERE ct.turno.idTurno = :idTurno
""")
    List<CambiosTurnoDTO> findCambiosTurnoDTOByTurno(@Param("idTurno") Long idTurno);

}