package com.turnos.enfermeria.exception.custom;

import com.turnos.enfermeria.exception.ApiException;
import com.turnos.enfermeria.exception.CodigoError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class GenericNoContentException extends ApiException {

    public GenericNoContentException(CodigoError codigoError, String mensajeDetallado,
                                     String metodo, String path) {
        super(
                codigoError.getEntidad(),
                codigoError.getCodigo(),
                mensajeDetallado != null && !mensajeDetallado.isEmpty()
                        ? mensajeDetallado
                        : codigoError.getMensaje(),
                HttpStatus.NO_CONTENT,  // Status HTTP 204
                metodo,
                path
        );
    }

    // Versión simplificada sin mensaje detallado
    public GenericNoContentException(CodigoError codigoError,
                                     String metodo, String path) {
        this(codigoError, null, metodo, path);
    }
}
