package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Macroprocesos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MacroprocesosRepository extends JpaRepository<Macroprocesos, Long>, JpaSpecificationExecutor<Macroprocesos> {
}
