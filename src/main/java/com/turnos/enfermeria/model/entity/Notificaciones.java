package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;

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

}
