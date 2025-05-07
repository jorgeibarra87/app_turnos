package com.turnos.enfermeria.exception.custom;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.ApiException;
import org.springframework.http.HttpStatus;

public class GenericBadRequestException extends ApiException {

    // Constructor con CodigoError + mensaje detallado
    public GenericBadRequestException(CodigoError codigoError, String mensajeDetallado,
                                      String metodo, String path) {
        super(
                codigoError.getEntidad(),
                codigoError.getCodigo(),
                codigoError.getMensaje(),
                codigoError.getStatus(),  // HttpStatus en lugar de .value()
                metodo,
                path
        );
    }

    // Constructor simplificado para validaciones básicas
    public GenericBadRequestException(String mensaje, String metodo, String path) {
        super(
                "VALIDACION",
                "VAL-400",
                "Error de validación: " + mensaje,
                HttpStatus.BAD_REQUEST,  // HttpStatus enum directamente
                metodo,
                path
        );
    }
}