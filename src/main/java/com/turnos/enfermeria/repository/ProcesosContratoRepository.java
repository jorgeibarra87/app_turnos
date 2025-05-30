package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.ProcesosAtencion;
import com.turnos.enfermeria.model.entity.ProcesosContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcesosContratoRepository extends JpaRepository<ProcesosContrato, Long> {
    List<ProcesosContrato> findByContratoIdContrato(Long contratoId);
}
