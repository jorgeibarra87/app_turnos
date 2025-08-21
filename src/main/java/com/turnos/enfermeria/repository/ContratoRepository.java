package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.Procesos;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long>, JpaSpecificationExecutor<Contrato> {

    @Query("SELECT t FROM Contrato c JOIN c.titulosFormacionAcademica t WHERE c.idContrato = :idContrato")
    Optional<List<TitulosFormacionAcademica>> findTitulosByIdContrato(@Param("idContrato") Long idContrato);

    // Métodos para obtener IDs de relaciones

    @Query(value = "SELECT id_tipo_atencion FROM tipo_atencion WHERE id_contrato = ?1", nativeQuery = true)
    List<Long> findTiposAtencionByContratoId(Long contratoId);

    @Query(value = "SELECT id_tipo_turno FROM tipo_turno WHERE id_contrato = ?1", nativeQuery = true)
    List<Long> findTiposTurnoByContratoId(Long contratoId);

    @Query(value = "SELECT id_proceso FROM procesos WHERE id_contrato = ?1", nativeQuery = true)
    List<Long> findProcesosByContratoId(Long contratoId);

    // Método para buscar por número de contrato
    Optional<Contrato> findByNumContrato(String numContrato);

    // Método para verificar si existe un contrato con ese número
    boolean existsByNumContrato(String numContrato);

    @Query("SELECT t FROM Contrato c JOIN c.procesos t WHERE c.idContrato = :idContrato")
    Optional<List<Procesos>> findProcesosByIdContrato(@Param("idContrato") Long idContrato);
}
