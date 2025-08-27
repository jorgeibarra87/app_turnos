package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.BloqueServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloqueServicioRepository extends JpaRepository<BloqueServicio, Long> {

    // Agregar método con ordenamiento por nombre ascendente
    List<BloqueServicio> findAllByOrderByIdBloqueServicioAsc();

}