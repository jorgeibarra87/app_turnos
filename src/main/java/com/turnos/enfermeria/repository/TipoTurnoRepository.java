package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.TipoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoTurnoRepository extends JpaRepository<TipoTurno, Long>, JpaSpecificationExecutor<TipoTurno> {
    List<TipoTurno> findByContratosIdContrato(Long contratoId);
}
