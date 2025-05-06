package com.turnos.enfermeria.exception.custom;

import com.turnos.enfermeria.exception.ApiException;
import com.turnos.enfermeria.exception.CodigoError;
import org.springframework.http.HttpStatus;

public class GenericNotFoundException extends ApiException {

    public GenericNotFoundException(CodigoError codigoError, Long id, String metodo, String path) {
        super(
                codigoError,
                "No se encontró " + codigoError.getEntidad() + " con ID: " + id,
                metodo,
                path
        );
    }
}