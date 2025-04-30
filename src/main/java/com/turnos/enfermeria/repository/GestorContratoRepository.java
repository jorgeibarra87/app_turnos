package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.GestorContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GestorContratoRepository extends JpaRepository<GestorContrato, Long> {

    @Query("SELECT g.contrato FROM GestorContrato g WHERE g.usuario.idPersona = :usuarioId")
    Optional<Contrato> findContratoByUsuarioId(@Param("usuarioId") Long usuarioId);

}