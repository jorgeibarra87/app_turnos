package com.turnos.enfermeria.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turnos.enfermeria.exception.CodigoError;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@EqualsAndHashCode(callSuper = true) // ✅ indica que extiende de RuntimeException
@JsonInclude(JsonInclude.Include.NON_NULL) // ✅ evita serializar campos nulos
public class ApiException extends RuntimeException {

    private final String entidad;
    private final String codigo;
    private final String mensaje;
    private final int codigoHttp; // ✅ se serializa normal, sin @JsonValue
    private final String metodo;
    private final String path;

    // Constructor principal
    public ApiException(String entidad, String codigo, String mensaje,
                        HttpStatus codigoHttp, String metodo, String path) {
        super(mensaje);
        this.entidad = entidad;
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.codigoHttp = codigoHttp.value();
        this.metodo = metodo;
        this.path = path;
    }

    // Constructor con CodigoError + mensaje detallado
    public ApiException(CodigoError codigoError, String mensajeDetallado,
                        String metodo, String path) {
        this(
                codigoError.getEntidad(),
                codigoError.getCodigo(),
                (mensajeDetallado != null && !mensajeDetallado.isEmpty())
                        ? codigoError.getMensaje() + ": " + mensajeDetallado
                        : codigoError.getMensaje(),
                codigoError.getStatus(),
                metodo,
                path
        );
    }

    // Constructor simplificado
    public ApiException(CodigoError codigoError, String metodo, String path) {
        this(codigoError, "", metodo, path);
    }
}
