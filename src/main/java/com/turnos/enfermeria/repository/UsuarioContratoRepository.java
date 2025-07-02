package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.UsuarioContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioContratoRepository extends JpaRepository<UsuarioContrato, Long> {

    @Query("SELECT g.contrato FROM UsuarioContrato g WHERE g.usuario.idPersona = :usuarioId")
    Optional<Contrato> findContratoByUsuarioId(@Param("usuarioId") Long usuarioId);

}