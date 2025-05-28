package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.TitulosFormacionAcademica;
import com.turnos.enfermeria.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long>, JpaSpecificationExecutor<Contrato> {

    @Query("SELECT t FROM Contrato c JOIN c.titulosFormacionAcademica t WHERE c.idContrato = :idContrato")
    Optional<List<TitulosFormacionAcademica>> findTitulosByIdContrato(@Param("idContrato") Long idContrato);



}
