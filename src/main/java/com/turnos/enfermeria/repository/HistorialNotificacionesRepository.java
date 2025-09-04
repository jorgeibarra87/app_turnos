package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.EstadoEnvio;
import com.turnos.enfermeria.model.entity.HistorialNotificaciones;
import com.turnos.enfermeria.model.entity.TipoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialNotificacionesRepository extends JpaRepository<HistorialNotificaciones, Long> {

    List<HistorialNotificaciones> findByIdCuadroTurnoOrderByFechaEnvioDesc(Long idCuadroTurno);

    List<HistorialNotificaciones> findTop10ByOrderByFechaEnvioDesc();

    List<HistorialNotificaciones> findByEstadoEnvio(EstadoEnvio estadoEnvio);

    long countByTipoEnvioAndFechaEnvioBetween(TipoEnvio tipoEnvio, LocalDateTime inicio, LocalDateTime fin);
}
