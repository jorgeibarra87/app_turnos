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
     * Obtiene todos los turnos de un usuario específico.
     */
    List<Turnos> findByUsuario_IdPersona(Long idPersona);

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

    /**
     * Encuentra todos los turnos asociados a un cuadro de turno específico.
     */
    List<Turnos> findByCuadroTurno_IdCuadroTurno(Long idCuadroTurno);

    /**
     * Encuentra turnos por cuadro de turno y estado.
     */
    List<Turnos> findByCuadroTurno_IdCuadroTurnoAndEstadoTurno(Long idCuadroTurno, String estadoTurno);

    /**
     * Encuentra turnos por cuadro de turno en un rango de fechas.
     */
    @Query("SELECT t FROM Turnos t WHERE t.cuadroTurno.idCuadroTurno = :idCuadroTurno " +
            "AND t.fechaInicio >= :fechaDesde AND t.fechaFin <= :fechaHasta")
    List<Turnos> findByCuadroTurnoAndFechaRange(@Param("idCuadroTurno") Long idCuadroTurno,
                                                @Param("fechaDesde") LocalDateTime fechaDesde,
                                                @Param("fechaHasta") LocalDateTime fechaHasta);

    /**
     * Encuentra turnos por cuadro, estado y rango de fechas.
     */
    @Query("SELECT t FROM Turnos t WHERE t.cuadroTurno.idCuadroTurno = :idCuadroTurno " +
            "AND t.estadoTurno = :estado " +
            "AND t.fechaInicio >= :fechaDesde AND t.fechaFin <= :fechaHasta")
    List<Turnos> findByCuadroTurnoAndEstadoAndFechaRange(@Param("idCuadroTurno") Long idCuadroTurno,
                                                         @Param("estado") String estado,
                                                         @Param("fechaDesde") LocalDateTime fechaDesde,
                                                         @Param("fechaHasta") LocalDateTime fechaHasta);



    @Query("SELECT t FROM Turnos t " +
            "JOIN t.cuadroTurno c " +
            "WHERE c.anio = :anio " +
            "AND c.mes = :mes " +
            "AND c.idCuadroTurno = :cuadroId")
    List<Turnos> findByAnioMesAndCuadro(@Param("anio") String anio,
                                        @Param("mes") String mes,
                                        @Param("cuadroId") Long cuadroId);
}
