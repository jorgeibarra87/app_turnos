package com.turnos.enfermeria.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NotificacionDTO {

    private Long idNotificacion;
    private String correo;
    private String mensaje;
    private String asunto;
    private String estadoNotificacion;
    private Boolean permanente;
    private Boolean estado = true;
    private Boolean automatico;
    private Date fechaEnvio;
    private Long idCuadroTurno;
}

