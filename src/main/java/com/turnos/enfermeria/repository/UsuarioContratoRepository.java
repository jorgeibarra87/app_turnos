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


//    @Query("""
//        SELECT new com.turnos.enfermeria.model.dto.UsuarioContratoTotalDTO(
//            p.documento,
//            p.nombre,
//            p.telefono,
//            p.email,
//            COALESCE(tf.titulo, 'Sin profesión'),
//            COALESCE(c.numContrato, 'Sin contrato'),
//            COALESCE(r.descripcion, 'Sin rol')
//        )
//        FROM Persona p
//        LEFT JOIN UsuariosTitulos ut ON p.idPersona = ut.idPersona
//        LEFT JOIN TitulosFormacionAcademica tf ON ut.idTitulo = tf.idTitulo
//        LEFT JOIN UsuarioContrato uc ON p.idPersona = uc.idPersona
//        LEFT JOIN Contrato c ON uc.idContrato = c.idContrato
//        LEFT JOIN UsuariosRoles ur ON p.idPersona = ur.idPersona
//        LEFT JOIN Roles r ON ur.idRol = r.idRol
//        WHERE p.documento = :documento
//        """)
//    UsuarioContratoTotalDTO findUsuarioInfoByDocumento(@Param("documento") String documento);

//    @Query("""
//        SELECT new com.turnos.enfermeria.model.dto.UsuarioContratoTotalDTO(
//    p.documento,
//    p.nombre,
//    p.telefono,
//    p.email,
//    tf.titulo,
//    c.numContrato,
//    r.descripcion
//        )
//    FROM Persona p
//    LEFT JOIN UsuariosTitulos ut ON p.idPersona = ut.idPersona
//    LEFT JOIN TitulosFormacionAcademica tf ON ut.idTitulo = tf.idTitulo
//    LEFT JOIN UsuarioContrato uc ON p.idPersona = uc.idPersona
//    LEFT JOIN Contrato c ON uc.idContrato = c.idContrato
//    LEFT JOIN UsuariosRoles ur ON p.idPersona = ur.idPersona
//    LEFT JOIN Roles r ON ur.idRol = r.idRol
//    WHERE p.documento = :documento
//        """)
//    List<UsuarioContratoTotalDTO> findAllUsuarioInfoByDocumento(@Param("documento") String documento);

//    @Query("""
//        SELECT new com.turnos.enfermeria.model.dto.UsuarioContratoTotalDTO(
//            p.documento,
//            p.nombre,
//            p.telefono,
//            p.email,
//            COALESCE(tf.titulo, 'Sin profesión'),
//            COALESCE(c.numContrato, 'Sin contrato'),
//            COALESCE(r.descripcion, 'Sin rol')
//        )
//        FROM Persona p
//        LEFT JOIN UsuariosTitulos ut ON p.idPersona = ut.idPersona
//        LEFT JOIN TitulosFormacionAcademica tf ON ut.idTitulo = tf.idTitulo
//        LEFT JOIN UsuarioContrato uc ON p.idPersona = uc.idPersona
//        LEFT JOIN Contrato c ON uc.idContrato = c.idContrato
//        LEFT JOIN UsuariosRoles ur ON p.idPersona = ur.idPersona
//        LEFT JOIN Roles r ON ur.idRol = r.idRol
//        WHERE p.documento = :documento
//    """)
//    List<UsuarioContratoTotalDTO> findAllUsuarioInfoByDocumento(@Param("documento") String documento);

    @Query("""
    SELECT new com.turnos.enfermeria.model.dto.UsuarioContratoTotalDTO(
                    p.documento,
                    p.nombreCompleto,
                    p.telefono,
                    p.email,
                    COALESCE(t.titulo, 'Sin profesión'),
                    COALESCE(c.numContrato, 'Sin contrato'),
                    COALESCE(r.descripcion, 'Sin rol')
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

}