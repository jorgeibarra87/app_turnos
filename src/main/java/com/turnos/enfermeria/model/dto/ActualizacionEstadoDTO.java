package com.turnos.enfermeria.model.dto;

public class ActualizacionEstadoDTO {
    private Long idNotificacion;
    private Boolean estado;
    private String estadoNotificacion;

    // Constructores
    public ActualizacionEstadoDTO() {}

    public ActualizacionEstadoDTO(Long idNotificacion, Boolean estado, String estadoNotificacion) {
        this.idNotificacion = idNotificacion;
        this.estado = estado;
        this.estadoNotificacion = estadoNotificacion;
    }

    // Getters y Setters
    public Long getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(Long idNotificacion) { this.idNotificacion = idNotificacion; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }


    public String getEstadoNotificacion() { return estadoNotificacion; }
    public void setEstadoNotificacion(String estadoNotificacion) { this.estadoNotificacion = estadoNotificacion; }
}
