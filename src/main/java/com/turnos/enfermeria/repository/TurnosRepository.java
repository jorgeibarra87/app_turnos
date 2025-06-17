package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Turnos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TurnosRepository extends JpaRepository<Turnos, Long>, JpaSpecificationExecutor<Turnos> {

    @Query("SELECT t FROM Turnos t WHERE t.usuario.idPersona = :idPersona AND " +
            "((t.fechaInicio < :fechaFin AND t.fechaFin > :fechaInicio) OR " +
            "(t.fechaInicio <= :fechaInicio AND t.fechaFin >= :fechaFin))")
    List<Turnos> findTurnosSolapados(@Param("idPersona") Long idPersona,
                                     @Param("fechaInicio") LocalDateTime fechaInicio,
                                     @Param("fechaFin") LocalDateTime fechaFin);


    /**
     * Obtiene todos los turnos de un cuadro de turno específico.
     */
    List<Turnos> findByCuadroTurno_IdCuadroTurno(Long idCuadroTurno);

    /**
     * Obtiene todos los turnos de un usuario específico.
     */
    List<Turnos> findByUsuario_IdPersona(Long idPersona);

//    @Query("SELECT c FROM Contrato c " +
//            "JOIN c.gestorContrato g " +
//            "WHERE g.usuario.idPersona = :usuarioId")
//    Optional<Contrato> findByGestorUsuarioId(@Param("usuarioId") Long usuarioId);

    List<Turnos> findByEstadoTurno(String estadoTurno);

    @Query("SELECT COALESCE(SUM(t.totalHoras), 0) FROM Turnos t " +
            "WHERE t.usuario.idPersona = :usuarioId " +
            "AND MONTH(t.fechaInicio) = CAST(:mes AS int) " +
            "AND YEAR(t.fechaInicio) = CAST(:anio AS int)")
    Integer obtenerHorasMensuales(@Param("usuarioId") Long usuarioId,
                                  @Param("mes") String mes,
                                  @Param("anio") String anio);

    @Query("SELECT t FROM Turnos t WHERE t.usuario.idPersona = :usuarioId AND DATE(t.fechaInicio) = :fecha")
    List<Turnos> obtenerTurnosPorFecha(@Param("usuarioId") Long usuarioId, @Param("fecha") LocalDate fecha);

    List<Turnos> findByCuadroTurnoIdCuadroTurnoIn(List<Long> idsCuadros);

}
