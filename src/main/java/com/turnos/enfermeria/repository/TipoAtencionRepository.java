package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.TipoAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoAtencionRepository extends JpaRepository<TipoAtencion, Long>, JpaSpecificationExecutor<TipoAtencion> {
}
