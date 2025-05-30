package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import com.turnos.enfermeria.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long>, JpaSpecificationExecutor<Contrato> {

    @Query("SELECT t FROM Contrato c JOIN c.titulosFormacionAcademica t WHERE c.idContrato = :idContrato")
    Optional<List<TitulosFormacionAcademica>> findTitulosByIdContrato(@Param("idContrato") Long idContrato);



    // Métodos para insertar relaciones en tablas intermedias

    @Modifying
    @Query(value = "INSERT INTO tipo_atencion (id_contrato, id_tipo_atencion) VALUES (?1, ?2)", nativeQuery = true)
    void insertContratoTipoAtencion(Long contratoId, Long tipoAtencionId);

    @Modifying
    @Query(value = "INSERT INTO tipo_turno (id_contrato, id_tipo_turno) VALUES (?1, ?2)", nativeQuery = true)
    void insertContratoTipoTurno(Long contratoId, Long tipoTurnoId);

    @Modifying
    @Query(value = "INSERT INTO procesos_contrato (id_contrato, id_proceso) VALUES (?1, ?2)", nativeQuery = true)
    void insertContratoProceso(Long contratoId, Long procesoId);

    // Métodos para eliminar relaciones existentes

    @Modifying
    @Query(value = "DELETE FROM tipo_atencion WHERE id_contrato = ?1", nativeQuery = true)
    void deleteContratoTiposAtencion(Long contratoId);

    @Modifying
    @Query(value = "DELETE FROM tipo_turno WHERE id_contrato = ?1", nativeQuery = true)
    void deleteContratoTiposTurno(Long contratoId);

    @Modifying
    @Query(value = "DELETE FROM procesos_contrato WHERE id_contrato = ?1", nativeQuery = true)
    void deleteContratoProcesos(Long contratoId);

    // Métodos para obtener IDs de relaciones

    @Query(value = "SELECT id_tipo_atencion FROM tipo_atencion WHERE id_contrato = ?1", nativeQuery = true)
    List<Long> findTiposAtencionByContratoId(Long contratoId);

    @Query(value = "SELECT id_tipo_turno FROM tipo_turno WHERE id_contrato = ?1", nativeQuery = true)
    List<Long> findTiposTurnoByContratoId(Long contratoId);

    @Query(value = "SELECT id_proceso FROM procesos_contrato WHERE id_contrato = ?1", nativeQuery = true)
    List<Long> findProcesosByContratoId(Long contratoId);

    // Método para buscar por número de contrato
    Optional<Contrato> findByNumContrato(String numContrato);

    // Método para verificar si existe un contrato con ese número
    boolean existsByNumContrato(String numContrato);

}
