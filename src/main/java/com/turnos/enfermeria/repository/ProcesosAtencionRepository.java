package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.ProcesosAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProcesosAtencionRepository extends JpaRepository<ProcesosAtencion, Long>, JpaSpecificationExecutor<ProcesosAtencion> {
}
