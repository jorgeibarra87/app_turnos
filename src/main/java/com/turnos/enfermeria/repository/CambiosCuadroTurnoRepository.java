package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.CambiosCuadroTurno;
import com.turnos.enfermeria.model.entity.CambiosTurno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CambiosCuadroTurnoRepository extends JpaRepository<CambiosCuadroTurno, Long> {


    /**
     * Encuentra el historial de cambios de un cuadro de turno específico.
     */
    List<CambiosCuadroTurno> findByCuadroTurno_IdCuadroTurno(Long idCuadroTurno);
}
