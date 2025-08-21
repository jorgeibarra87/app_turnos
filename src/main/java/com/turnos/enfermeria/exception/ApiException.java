package com.turnos.enfermeria.exception;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiException extends RuntimeException {
    private final String entidad;
    private final String codigo;
    private final String mensaje;

    @JsonValue  // Esto asegura que se serialice como número
    private final int codigoHttp;

    private final String metodo;
    private final String path;

    // Constructor principal mejorado
    public ApiException(String entidad, String codigo, String mensaje,
                        HttpStatus codigoHttp, String metodo, String path) {
        super(mensaje);
        this.entidad = entidad;
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.codigoHttp = codigoHttp.value();  // Convertimos a número aquí
        this.metodo = metodo;
        this.path = path;
    }

    // Constructor para CodigoError mejorado
    public ApiException(CodigoError codigoError, String mensajeDetallado,
                        String metodo, String path) {
        this(
                codigoError.getEntidad(),
                codigoError.getCodigo(),
                // Mensaje más limpio sin duplicación
                mensajeDetallado != null && !mensajeDetallado.isEmpty() ?
                        codigoError.getMensaje() + ": " + mensajeDetallado :
                        codigoError.getMensaje(),
                codigoError.getStatus(),
                metodo,
                path
        );
    }

    // Constructor simplificado para cuando no necesitas mensaje detallado
    public ApiException(CodigoError codigoError, String metodo, String path) {
        this(codigoError, "", metodo, path);
    }
}