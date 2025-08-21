package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.dto.UsuarioContratoTotalDTO;
import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.UsuarioContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioContratoRepository extends JpaRepository<UsuarioContrato, Long> {

    @Query("SELECT g.contrato FROM UsuarioContrato g WHERE g.usuario.idPersona = :usuarioId")
    Optional<Contrato> findContratoByUsuarioId(@Param("usuarioId") Long usuarioId);


    @Query("""
    SELECT new com.turnos.enfermeria.model.dto.UsuarioContratoTotalDTO(
                    p.documento,
                    p.nombreCompleto,
                    p.telefono,
                    p.email,
                    COALESCE(t.titulo, 'Sin profesión'),
                    COALESCE(c.numContrato, 'Sin contrato'),
                    COALESCE(r.rol, 'Sin rol')
                )
                FROM Usuario u
                JOIN u.persona p
                LEFT JOIN u.titulosFormacionAcademica t
                LEFT JOIN UsuarioContrato uc ON uc.usuario = u
                LEFT JOIN Contrato c ON uc.contrato = c
                LEFT JOIN u.roles r
                WHERE p.documento = :documento
""")
    List<UsuarioContratoTotalDTO> findAllUsuarioInfoByDocumento(@Param("documento") String documento);

    boolean existsByUsuario_IdPersonaAndContrato_IdContrato(Long idPersona, Long idContrato);

}