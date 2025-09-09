package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "historial_notificaciones")
public class HistorialNotificaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    @Column(name = "correo", nullable = false)
    private String correo;

    @Column(name = "asunto", nullable = false)
    private String asunto;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "tipo_envio", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoEnvio tipoEnvio;

    @Column(name = "estado_envio", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estadoEnvio;

    @CreationTimestamp
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "id_cuadro_turno")
    private Long idCuadroTurno;

    @Column(name = "error_mensaje", columnDefinition = "TEXT")
    private String errorMensaje;

    @Column(name = "intentos")
    private Integer intentos = 1;
}
