package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.TipoFormacionAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoFormacionAcademicaRepository extends JpaRepository<TipoFormacionAcademica, Long>, JpaSpecificationExecutor<TipoFormacionAcademica> {

}
