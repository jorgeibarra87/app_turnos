package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.CambiosEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CambiosEquipoRepository extends JpaRepository<CambiosEquipo, Long> {

    // Buscar historial por equipo ordenado por fecha descendente
    List<CambiosEquipo> findByEquipo_IdEquipoOrderByFechaCambioDesc(Long idEquipo);

    // Buscar historial por equipo en un rango de fechas
    @Query("SELECT c FROM CambiosEquipo c WHERE c.equipo.idEquipo = :idEquipo " +
            "AND c.fechaCambio BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY c.fechaCambio DESC")
    List<CambiosEquipo> findByEquipoAndFechaRange(@Param("idEquipo") Long idEquipo,
                                                  @Param("fechaInicio") LocalDateTime fechaInicio,
                                                  @Param("fechaFin") LocalDateTime fechaFin);

    // Buscar por tipo de cambio
    List<CambiosEquipo> findByTipoCambioOrderByFechaCambioDesc(String tipoCambio);

    // Buscar cambios de un equipo por tipo
    List<CambiosEquipo> findByEquipo_IdEquipoAndTipoCambioOrderByFechaCambioDesc(Long idEquipo, String tipoCambio);

    // Contar cambios por equipo
    long countByEquipo_IdEquipo(Long idEquipo);

    // Buscar últimos N cambios de un equipo
    @Query("SELECT c FROM CambiosEquipo c WHERE c.equipo.idEquipo = :idEquipo " +
            "ORDER BY c.fechaCambio DESC")
    List<CambiosEquipo> findTopNByEquipo(@Param("idEquipo") Long idEquipo);

    // Buscar cambios por usuario
    List<CambiosEquipo> findByUsuarioCambioOrderByFechaCambioDesc(String usuarioCambio);

    // Buscar cambios recientes (últimas 24 horas)
    @Query("SELECT c FROM CambiosEquipo c WHERE c.fechaCambio >= :fecha24HorasAtras " +
            "ORDER BY c.fechaCambio DESC")
    List<CambiosEquipo> findCambiosRecientes(@Param("fecha24HorasAtras") LocalDateTime fecha24HorasAtras);

    // Buscar todos los cambios ordenados por fecha
    List<CambiosEquipo> findAllByOrderByFechaCambioDesc();

    // Verificar si existe algún cambio para un equipo
    boolean existsByEquipo_IdEquipo(Long idEquipo);
}
