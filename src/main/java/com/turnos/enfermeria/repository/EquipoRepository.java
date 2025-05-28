package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    /**
     * Busca equipos cuyo nombre comience con el prefijo especificado
     * @param prefijo Prefijo del nombre del equipo
     * @return Lista de equipos que coinciden
     */
    List<Equipo> findByNombreStartingWith(String prefijo);

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
}
