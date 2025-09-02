package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * $table.getTableComment()
 */
@Data
@Entity
@Table(name = "notificaciones", schema = "public")
public class Notificaciones {
    @Id
    @Column(name = "id_notificacion", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    @Column(name = "correo")
    private String correo;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "estado_notificacion")
    private String estadoNotificacion;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "permanente")
    private Boolean permanente;

    @Column(name = "fecha_envio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEnvio;

    @Column(name = "automatico")
    private Boolean automatico;

}
