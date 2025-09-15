package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.CambiosPersonaEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CambiosPersonaEquipoRepository extends JpaRepository<CambiosPersonaEquipo, Long> {

    // Buscar historial por persona ordenado por fecha descendente
    List<CambiosPersonaEquipo> findByPersona_IdPersonaOrderByFechaCambioDesc(Long idPersona);

    // Buscar historial por equipo ordenado por fecha descendente
    List<CambiosPersonaEquipo> findByEquipo_IdEquipoOrderByFechaCambioDesc(Long idEquipo);

    // Buscar historial de una persona en un equipo específico
    List<CambiosPersonaEquipo> findByPersona_IdPersonaAndEquipo_IdEquipoOrderByFechaCambioDesc(Long idPersona, Long idEquipo);

    // Buscar por tipo de cambio
    List<CambiosPersonaEquipo> findByTipoCambioOrderByFechaCambioDesc(String tipoCambio);

    // Buscar cambios en un rango de fechas
    @Query("SELECT c FROM CambiosPersonaEquipo c WHERE c.fechaCambio BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY c.fechaCambio DESC")
    List<CambiosPersonaEquipo> findByFechaRange(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin);

    // Buscar movimientos entre equipos específicos
    @Query("SELECT c FROM CambiosPersonaEquipo c WHERE c.equipoAnterior.idEquipo = :equipoAnteriorId " +
            "AND c.equipoNuevo.idEquipo = :equipoNuevoId ORDER BY c.fechaCambio DESC")
    List<CambiosPersonaEquipo> findMovimientosEntreEquipos(@Param("equipoAnteriorId") Long equipoAnteriorId,
                                                           @Param("equipoNuevoId") Long equipoNuevoId);

    // Buscar asignaciones/desvinculaciones de un equipo
    @Query("SELECT c FROM CambiosPersonaEquipo c WHERE " +
            "(c.equipoAnterior.idEquipo = :idEquipo OR c.equipoNuevo.idEquipo = :idEquipo) " +
            "ORDER BY c.fechaCambio DESC")
    List<CambiosPersonaEquipo> findCambiosDeEquipo(@Param("idEquipo") Long idEquipo);

    // Contar cambios por persona
    long countByPersona_IdPersona(Long idPersona);

    // Contar cambios por equipo
    long countByEquipo_IdEquipo(Long idEquipo);

    // Buscar últimos cambios de una persona
    @Query("SELECT c FROM CambiosPersonaEquipo c WHERE c.persona.idPersona = :idPersona " +
            "ORDER BY c.fechaCambio DESC")
    List<CambiosPersonaEquipo> findUltimosCambiosPersona(@Param("idPersona") Long idPersona);

    // Buscar cambios por usuario que los realizó
    List<CambiosPersonaEquipo> findByUsuarioCambioOrderByFechaCambioDesc(String usuarioCambio);

    // Buscar cambios recientes (últimas 24 horas)
    @Query("SELECT c FROM CambiosPersonaEquipo c WHERE c.fechaCambio >= :fecha24HorasAtras " +
            "ORDER BY c.fechaCambio DESC")
    List<CambiosPersonaEquipo> findCambiosRecientes(@Param("fecha24HorasAtras") LocalDateTime fecha24HorasAtras);

    // Verificar si una persona ha tenido cambios
    boolean existsByPersona_IdPersona(Long idPersona);

    // Obtener el último cambio de una persona
    @Query("SELECT c FROM CambiosPersonaEquipo c WHERE c.persona.idPersona = :idPersona " +
            "ORDER BY c.fechaCambio DESC LIMIT 1")
    CambiosPersonaEquipo findUltimoCambioPersona(@Param("idPersona") Long idPersona);

    // Buscar reasignaciones específicamente
    @Query("SELECT c FROM CambiosPersonaEquipo c WHERE c.tipoCambio = 'REASIGNACION' " +
            "ORDER BY c.fechaCambio DESC")
    List<CambiosPersonaEquipo> findReasignaciones();

    // Buscar todos los cambios ordenados por fecha
    List<CambiosPersonaEquipo> findAllByOrderByFechaCambioDesc();
}
