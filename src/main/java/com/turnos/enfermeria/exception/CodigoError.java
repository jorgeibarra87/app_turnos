package com.turnos.enfermeria.exception;

import org.springframework.http.HttpStatus;

public enum CodigoError {
    BLOQUE_SERVICIO_NO_ENCONTRADO(
            "BLOQUE_SERVICIO",
            "BS-404",
            "No se encontró el bloque de servicio",
            HttpStatus.NOT_FOUND),

    BLOQUE_SERVICIO_CONFLICTO(
            "BLOQUE_SERVICIO",
            "BS-409",
            "Conflicto con el bloque de servicio",
            HttpStatus.CONFLICT),

    // Agrega otros códigos según necesites
    ;

    private final String entidad;
    private final String codigo;
    private final String mensaje;
    private final HttpStatus status;

    CodigoError(String entidad, String codigo, String mensaje, HttpStatus status) {
        this.entidad = entidad;
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.status = status;
    }

    // Getters
    public String getEntidad() {
        return entidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public HttpStatus getStatus() {
        return status;
    }
}