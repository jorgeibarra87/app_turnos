package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Procesos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcesosRepository extends JpaRepository<Procesos, Long>, JpaSpecificationExecutor<Procesos> {
}
