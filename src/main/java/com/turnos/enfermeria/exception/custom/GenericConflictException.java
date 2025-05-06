package com.turnos.enfermeria.exception.custom;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.ApiException;
import org.springframework.http.HttpStatus;

public class GenericConflictException extends ApiException {

    // Constructor principal con CodigoError
    public GenericConflictException(CodigoError codigoError, String mensajeDetallado,
                                    String metodo, String path) {
        super(
                codigoError.getEntidad(),
                codigoError.getCodigo(),
                formatMessage(codigoError.getMensaje(), mensajeDetallado),
                codigoError.getStatus(),  // Pasamos el HttpStatus directamente
                metodo,
                path
        );
    }

    // Constructor alternativo para conflictos no mapeados
    public GenericConflictException(String entidad, String mensaje,
                                    String metodo, String path) {
        super(
                entidad,
                "CONF-409",
                "Conflicto en " + entidad + ": " + mensaje,
                HttpStatus.CONFLICT,  // Pasamos el enum HttpStatus.CONFLICT
                metodo,
                path
        );
    }

    private static String formatMessage(String mensajeBase, String detalle) {
        if (detalle == null || detalle.isEmpty()) {
            return mensajeBase;
        }
        return mensajeBase + ": " + detalle;
    }
}