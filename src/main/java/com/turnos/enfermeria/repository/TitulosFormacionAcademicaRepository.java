package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TitulosFormacionAcademicaRepository extends JpaRepository<TitulosFormacionAcademica, Long>, JpaSpecificationExecutor<TitulosFormacionAcademica> {

}
