package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.CambiosProcesosAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CambiosProcesosAtencionRepository extends JpaRepository<CambiosProcesosAtencion, Long> {
}
