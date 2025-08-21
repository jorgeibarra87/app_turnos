package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.SubseccionesServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubseccionesServicioRepository extends JpaRepository<SubseccionesServicio, Long>, JpaSpecificationExecutor<SubseccionesServicio> {

}