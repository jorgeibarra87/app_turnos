package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.ProcesosAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
//JpaSpecificationExecutor<ProcesosAtencion>
@Repository
public interface ProcesosAtencionRepository extends JpaRepository<ProcesosAtencion, Long> {

    // Método para eliminar procesos por ID de cuadro de turno
    @Modifying
    @Query("DELETE FROM ProcesosAtencion pa WHERE pa.cuadroTurno.id = :cuadroTurnoId")
    void deleteByCuadroTurnoId(@Param("cuadroTurnoId") Long cuadroTurnoId);

    // Método para buscar procesos por ID de cuadro de turno
    @Query("SELECT pa FROM ProcesosAtencion pa WHERE pa.cuadroTurno.id = :cuadroTurnoId")
    List<ProcesosAtencion> findByCuadroTurnoId(@Param("cuadroTurnoId") Long cuadroTurnoId);

    List<ProcesosAtencion> findByCuadroTurno_IdCuadroTurno(Long idCuadroTurno);
}
