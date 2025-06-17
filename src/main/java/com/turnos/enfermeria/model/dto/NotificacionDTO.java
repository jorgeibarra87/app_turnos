package com.turnos.enfermeria.model.dto;

import lombok.Data;

@Data
public class NotificacionDTO {

    private Long idNotificacion;
    private String correo;
    private String mensaje;
    private String estadoNotificacion;
    private Boolean permanente;
    private Boolean estado = true;

}
