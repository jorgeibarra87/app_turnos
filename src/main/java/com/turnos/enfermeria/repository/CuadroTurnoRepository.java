package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.CuadroTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CuadroTurnoRepository extends JpaRepository<CuadroTurno, Long> {

    /**
     * Encuentra la última versión de un cuadro de turno según el año y mes.
     */
    @Query("SELECT c FROM CuadroTurno c WHERE c.anio = ?1 AND c.mes = ?2 " +
            "ORDER BY CAST(SUBSTRING(c.version, POSITION('_v' IN c.version) + 2, LENGTH(c.version)) AS integer) DESC LIMIT 1")
    Optional<CuadroTurno> findLastVersionByAnioAndMes(String anio, String mes);

    /**
     * Encuentra cuadros de turno por versión.
     */
    List<CuadroTurno> findByVersion(String version);

    /**
     * Cuenta cuadros de turno por año, mes y categoría
     */
    @Query("SELECT COUNT(ct) FROM CuadroTurno ct WHERE " +
            "ct.anio = :anio AND ct.mes = :mes AND " +
            "CASE " +
            "    WHEN ct.seccionesServicios IS NOT NULL THEN 'Seccion' " +
            "    WHEN ct.servicios IS NOT NULL THEN 'Servicio' " +
            "    WHEN ct.procesos IS NOT NULL THEN 'Proceso' " +
            "    WHEN ct.macroProcesos IS NOT NULL THEN 'Macroproceso' " +
            "    ELSE 'General' " +
            "END = :categoria")
    Long countByAnioAndMesAndCategoria(@Param("anio") String anio,
                                       @Param("mes") String mes,
                                       @Param("categoria") String categoria);


    /**
     * Verifica si existe un cuadro de turno con configuración similar
     */
    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END FROM CuadroTurno ct WHERE " +
            "(:idMacroproceso IS NULL OR ct.macroProcesos.idMacroproceso = :idMacroproceso) AND " +
            "(:idProceso IS NULL OR ct.procesos.idProceso = :idProceso) AND " +
            "(:idServicio IS NULL OR ct.servicios.idServicio = :idServicio) AND " +
            "(:idSeccionServicio IS NULL OR ct.seccionesServicios.idSeccionServicio = :idSeccionServicio) AND " +
            "(:idEquipo IS NULL OR ct.equipos.idEquipo = :idEquipo) AND " +
            "ct.anio = :anio AND ct.mes = :mes")

    boolean existsBySimilarConfiguration(@Param("idMacroproceso") Long idMacroproceso,
                                         @Param("idProceso") Long idProceso,
                                         @Param("idServicio") Long idServicio,
                                         @Param("idSeccionServicio") Long idSeccionServicio,
                                         @Param("idEquipo") Long idEquipo,
                                         @Param("anio") String anio,
                                         @Param("mes") String mes);

    /**
     * Busca cuadros de turno por estado
     */
    List<CuadroTurno> findByEstadoCuadro(String estadoCuadro);

    /**
     * Busca cuadros de turno por año y mes
     */
    List<CuadroTurno> findByAnioAndMes(String anio, String mes);

    /**
     * Busca cuadros de turno por equipo
     */
    List<CuadroTurno> findByEquipos_IdEquipo(Long idEquipo);

    /**
     * Busca cuadros de turno que contengan un texto en el nombre
     */
    List<CuadroTurno> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Obtiene el último cuadro creado para un equipo en un período específico
     */
    @Query("SELECT ct FROM CuadroTurno ct WHERE " +
            "ct.equipos.idEquipo = :idEquipo AND " +
            "ct.anio = :anio AND ct.mes = :mes " +
            "ORDER BY ct.idCuadroTurno DESC")
    Optional<CuadroTurno> findLastByEquipoAndPeriodo(@Param("idEquipo") Long idEquipo,
                                                     @Param("anio") String anio,
                                                     @Param("mes") String mes);

    /**
     * Verifica si existe un cuadro de turno con configuración similar
     * Considerando múltiples procesos de atención
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CuadroTurno c " +
            "WHERE c.anio = :anio AND c.mes = :mes AND " +
            "(:idMacroproceso IS NULL OR c.macroProcesos.id = :idMacroproceso) AND " +
            "(:idProceso IS NULL OR c.procesos.id = :idProceso) AND " +
            "(:idServicio IS NULL OR c.servicios.id = :idServicio) AND " +
            "(:idSeccionServicio IS NULL OR c.seccionesServicios.id = :idSeccionServicio) AND " +
            "(:idEquipo IS NULL OR c.equipos.id = :idEquipo)")
    boolean existsBySimilarConfigurationWithMultipleProcesses(
            @Param("idMacroproceso") Long idMacroproceso,
            @Param("idProceso") Long idProceso,
            @Param("idServicio") Long idServicio,
            @Param("idSeccionServicio") Long idSeccionServicio,
            @Param("idSubseccionServicio") Long idSubseccionServicio,
            @Param("idEquipo") Long idEquipo,
            @Param("anio") String anio,
            @Param("mes") String mes
    );

//    boolean existsBySimilarConfigurationWithMultipleProcesses(
//            Long idMacroproceso, Long idProceso, Long idServicio,
//            Long idSeccionServicio, List<Long> idsProcesosAtencion,
//            Long idEquipo, String anio, String mes);


    // Para múltiples procesos de atención
//    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END " +
//            "FROM CuadroTurno ct " +
//            "JOIN ct.procesosAtencion pa " +
//            "WHERE ct.anio = :anio AND ct.mes = :mes AND pa.idProcesoAtencion IN :idsProcesosAtencion")
//    boolean existsByAnioAndMesAndProcesosAtencionIn(
//            @Param("anio") String anio,
//            @Param("mes") String mes,
//            @Param("idsProcesosAtencion") List<Long> idsProcesosAtencion
//    );

    // Para sección de servicio
    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END " +
            "FROM CuadroTurno ct " +
            "WHERE ct.anio = :anio AND ct.mes = :mes AND ct.seccionesServicios.idSeccionServicio = :idSeccionServicio")
    boolean existsByAnioAndMesAndSeccionServicio(
            @Param("anio") String anio,
            @Param("mes") String mes,
            @Param("idSeccionServicio") Long idSeccionServicio
    );

    // Para servicio
    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END " +
            "FROM CuadroTurno ct " +
            "WHERE ct.anio = :anio AND ct.mes = :mes AND ct.servicios.idServicio = :idServicio")
    boolean existsByAnioAndMesAndServicio(
            @Param("anio") String anio,
            @Param("mes") String mes,
            @Param("idServicio") Long idServicio
    );

    // Para proceso
    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END " +
            "FROM CuadroTurno ct " +
            "WHERE ct.anio = :anio AND ct.mes = :mes AND ct.procesos.idProceso = :idProceso")
    boolean existsByAnioAndMesAndProceso(
            @Param("anio") String anio,
            @Param("mes") String mes,
            @Param("idProceso") Long idProceso
    );

    // Para macroproceso
    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END " +
            "FROM CuadroTurno ct " +
            "WHERE ct.anio = :anio AND ct.mes = :mes AND ct.macroProcesos.idMacroproceso = :idMacroproceso")
    boolean existsByAnioAndMesAndMacroproceso(
            @Param("anio") String anio,
            @Param("mes") String mes,
            @Param("idMacroproceso") Long idMacroproceso
    );

    // Para equipo
    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END " +
            "FROM CuadroTurno ct " +
            "WHERE ct.anio = :anio AND ct.mes = :mes AND ct.equipos.idEquipo = :idEquipo")
    boolean existsByAnioAndMesAndEquipo(
            @Param("anio") String anio,
            @Param("mes") String mes,
            @Param("idEquipo") Long idEquipo
    );

    // Método para buscar con filtros (ya existente, adaptado)
    @Query("SELECT ct FROM CuadroTurno ct " +
            "WHERE (:anio IS NULL OR ct.anio = :anio) " +
            "AND (:mes IS NULL OR ct.mes = :mes) " +
            "AND (:categoria IS NULL OR " +
            "      (ct.seccionesServicios IS NOT NULL AND :categoria = 'Seccion') OR " +
            "      (ct.servicios IS NOT NULL AND :categoria = 'Servicio') OR " +
            "      (ct.procesos IS NOT NULL AND :categoria = 'Proceso') OR " +
            "      (ct.macroProcesos IS NOT NULL AND :categoria = 'Macroproceso'))")
    List<CuadroTurno> findByFilters(
            @Param("anio") String anio,
            @Param("mes") String mes,
            @Param("categoria") String categoria
    );


    @Query("SELECT c.nombre FROM CuadroTurno c WHERE c.nombre LIKE CONCAT('%', :nombreBase)")
    List<String> findNombresByBase(@Param("nombreBase") String nombreBase);
}
