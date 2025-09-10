package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.BloqueServicio;
import com.turnos.enfermeria.model.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    // Agregar método con ordenamiento por nombre ascendente
    List<Equipo> findAllByOrderByIdEquipoAsc();


    /**
     * Cuenta equipos cuyo nombre comience con el prefijo especificado
     * @param prefijo Prefijo del nombre del equipo
     * @return Número de equipos que coinciden
     */
    long countByNombreStartingWith(String prefijo);

    /**
     * Busca equipos ordenados por nombre (útil para mantener orden secuencial)
     * @param prefijo Prefijo del nombre del equipo
     * @return Lista de equipos ordenados por nombre
     */
    @Query("SELECT e FROM Equipo e WHERE e.nombre LIKE CONCAT(:prefijo, '%') ORDER BY e.nombre")
    List<Equipo> findByNombreStartingWithOrderByNombre(@Param("prefijo") String prefijo);

    @Query("SELECT e FROM Equipo e WHERE e.nombre LIKE :prefix%")
    List<Equipo> findByNombreStartingWith(@Param("prefix") String prefix);

    @Query("SELECT COUNT(e) FROM Equipo e WHERE e.nombre LIKE :pattern")
    long countByNombrePattern(@Param("pattern") String pattern);

    boolean existsByNombre(String nombre);

    @Query(value = """
        SELECT p.id_persona, p.nombre_completo, tfa.titulo, p.documento 
        FROM usuarios_equipo ue 
        JOIN usuario u ON ue.id_persona = u.id_persona 
        JOIN persona p ON u.id_persona = p.id_persona 
        LEFT JOIN usuarios_titulos ut ON ut.id_persona = p.id_persona 
        LEFT JOIN titulos_formacion_academica tfa ON ut.id_titulo = tfa.id_titulo 
        WHERE ue.id_equipo = :equipoId
        """, nativeQuery = true)
    List<Object[]> findMiembrosConPerfilRaw(@Param("equipoId") Long equipoId);
}
