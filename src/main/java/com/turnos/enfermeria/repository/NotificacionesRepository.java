package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Notificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionesRepository extends JpaRepository<Notificaciones, Long> {

    // Obtener correos predeterminados activos
    @Query("SELECT DISTINCT n FROM Notificaciones n " +
            "WHERE n.permanente = true AND n.estado = true " +
            "ORDER BY n.correo")
    List<Notificaciones> findCorreosPredeterminadosActivos();

    // Obtener correos seleccionables activos
    @Query("SELECT DISTINCT n FROM Notificaciones n " +
            "WHERE n.permanente = false AND n.estadoNotificacion = 'activo' " +
            "ORDER BY n.correo")
    List<Notificaciones> findCorreosSeleccionablesActivos();

    // Obtener todos los correos activos
    @Query(value = "SELECT DISTINCT * FROM notificaciones n " +
            "WHERE (n.permanente = true AND n.estado = true) " +
            "OR (n.permanente = false AND n.estado_notificacion = 'activo') " +
            "ORDER BY n.permanente DESC, n.correo",
            nativeQuery = true)
    List<Notificaciones> findTodosCorreosActivos();

    // Obtener correos por tipo
    @Query("SELECT DISTINCT n FROM Notificaciones n " +
            "WHERE n.permanente = :permanente " +
            "ORDER BY n.correo")
    List<Notificaciones> findCorreosPorTipo(@Param("permanente") Boolean permanente);

    // Buscar por correo específico
    List<Notificaciones> findByCorreo(String correo);

}