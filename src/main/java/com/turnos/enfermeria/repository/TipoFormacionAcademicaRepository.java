package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.TipoFormacionAcademica;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoFormacionAcademicaRepository extends JpaRepository<TipoFormacionAcademica, Long>, JpaSpecificationExecutor<TipoFormacionAcademica> {
    /**
     * Busca un tipo de formación por su nombre
     * @param tipo Nombre del tipo de formación
     * @return Optional con el tipo de formación si existe
     */
    Optional<TipoFormacionAcademica> findByTipo(String tipo);

    /**
     * Busca tipos de formación que contengan una palabra específica
     * @param tipo Parte del tipo a buscar
     * @return Lista de tipos que coinciden
     */
    List<TipoFormacionAcademica> findByTipoContainingIgnoreCase(String tipo);

}
