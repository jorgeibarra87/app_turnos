package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.ProcesosAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
//JpaSpecificationExecutor<ProcesosAtencion>

public interface ProcesosAtencionRepository extends JpaRepository<ProcesosAtencion, Long> {

    //List<ProcesosAtencion> findByCuadroTurnoId(Long cuadroTurnoId);
}
