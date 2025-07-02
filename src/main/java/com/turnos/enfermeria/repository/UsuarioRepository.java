package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
//    @Query("SELECT u FROM Usuario u " +
//            "LEFT JOIN FETCH u.persona " +
//            "LEFT JOIN FETCH u.titulosFormacionAcademica ut " +
//            "LEFT JOIN FETCH ut.tipoFormacionAcademica tfa " +
//            "LEFT JOIN FETCH u.roles r " +
//            "LEFT JOIN FETCH u.servicios s " +
//            "LEFT JOIN FETCH s.procesos p " +
//            "LEFT JOIN FETCH p.macroprocesos " +
//            "WHERE u.idPersona = :idPersona")
//    Optional<Usuario> findUsuarioCompleto(@Param("idPersona") Long idPersona);

    List<Usuario> findUsuariosByEquipos_IdEquipo(Long idEquipo);
    List<Usuario> findUsuariosByRoles_IdRol(Long idRol);
    Optional<Usuario> findByPersona_Documento(String documento);
    //List<Usuario> findUsuariosByContratos_IdContrato(Long idContrato);
}
