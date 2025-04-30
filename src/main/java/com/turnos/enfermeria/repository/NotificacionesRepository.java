package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Notificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionesRepository extends JpaRepository<Notificaciones, Long> {
}
