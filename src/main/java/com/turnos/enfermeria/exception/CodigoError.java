package com.turnos.enfermeria.exception;

import org.springframework.http.HttpStatus;

public enum CodigoError {
    BLOQUE_SERVICIO_NO_ENCONTRADO("BLOQUE_SERVICIO", "BS-404", "Bloque de servicio no encontrado", HttpStatus.NOT_FOUND),
    BLOQUE_SERVICIO_DATOS_INVALIDOS("BLOQUE_SERVICIO", "BS-400", "Datos inválidos para bloque de servicio", HttpStatus.BAD_REQUEST),
    BLOQUE_SERVICIO_CONFLICTO("BLOQUE_SERVICIO", "BS-409", "Conflicto con bloque de servicio", HttpStatus.CONFLICT),
    BLOQUE_SERVICIO_SIN_CONTENIDO("BLOQUE_SERVICIO", "BS-204", "No hay bloques de servicio registrados", HttpStatus.NO_CONTENT);

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