package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.TipoFormacionAcademica;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TitulosFormacionAcademicaRepository extends JpaRepository<TitulosFormacionAcademica, Long>, JpaSpecificationExecutor<TitulosFormacionAcademica> {

    /**
     * Busca títulos por tipo específico usando la relación con TipoFormacionAcademica
     * @param tipoFormacionAcademica Entidad TipoFormacionAcademica
     * @return Lista de títulos del tipo especificado
     */
    List<TitulosFormacionAcademica> findByTipoFormacionAcademica(TipoFormacionAcademica tipoFormacionAcademica);

    /**
     * Busca títulos por ID del tipo de formación académica usando consulta personalizada
     * @param idTipoFormacionAcademica ID del tipo de formación académica
     * @return Lista de títulos del tipo especificado
     */
    @Query("SELECT t FROM TitulosFormacionAcademica t WHERE t.tipoFormacionAcademica.idTipoFormacionAcademica = :idTipoFormacionAcademica")
    List<TitulosFormacionAcademica> findByTipoFormacionAcademicaId(@Param("idTipoFormacionAcademica") Long idTipoFormacionAcademica);

    /**
     * Busca títulos que contengan una palabra específica en el título
     * @param titulo Parte del título a buscar
     * @return Lista de títulos que coinciden
     */
    List<TitulosFormacionAcademica> findByTituloContainingIgnoreCase(String titulo);

    //boolean existsByUsuario_IdPersonaAndTitulo(Long idPersona, String titulo);

    //void deleteByUsuario_IdPersona(Long idPersona);

}
