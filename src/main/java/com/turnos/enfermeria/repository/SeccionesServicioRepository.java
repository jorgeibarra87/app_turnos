package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.SeccionesServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SeccionesServicioRepository extends JpaRepository<SeccionesServicio, Long>, JpaSpecificationExecutor<SeccionesServicio> {

}
