package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.CuadroTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CuadroTurnoRepository extends JpaRepository<CuadroTurno, Long> {

    /**
     * Encuentra la última versión de un cuadro de turno según el año y mes.
     */
    @Query("SELECT c FROM CuadroTurno c WHERE c.anio = ?1 AND c.mes = ?2 " +
            "ORDER BY CAST(SUBSTRING(c.version, POSITION('_v' IN c.version) + 2, LENGTH(c.version)) AS integer) DESC LIMIT 1")
    Optional<CuadroTurno> findLastVersionByAnioAndMes(String anio, String mes);

    /**
     * Encuentra cuadros de turno por estado (abierto/cerrado).
     */
    List<CuadroTurno> findByEstadoCuadro(String estadoCuadro);

    /**
     * Encuentra cuadros de turno por versión.
     */
    List<CuadroTurno> findByVersion(String version);
}
