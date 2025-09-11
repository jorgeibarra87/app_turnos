package com.turnos.enfermeria.exception.custom;

import com.turnos.enfermeria.exception.ApiException;
import com.turnos.enfermeria.exception.CodigoError;
import org.springframework.http.HttpStatus;

/**
 * Excepción específica para validaciones de reglas de negocio de turnos
 * Usa HTTP 422 (Unprocessable Entity) para errores de validación
 */
public class TurnoValidationException extends ApiException {

    // Constructor con mensaje personalizado
    public TurnoValidationException(String mensaje, String metodo, String path) {
        super(
                "TURNOS_VALIDACION",
                "TRN-422",
                "Validación de turno: " + mensaje,
                HttpStatus.UNPROCESSABLE_ENTITY,
                metodo,
                path
        );
    }

    // Constructor usando CodigoError del enum
    public TurnoValidationException(CodigoError codigoError, String detalle, String metodo, String path) {
        super(codigoError, detalle, metodo, path);
    }
}
