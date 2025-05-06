package com.turnos.enfermeria.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getEntidad(),
                ex.getCodigo(),
                ex.getMensaje(),
                ex.getCodigoHttp(),  // Ya es un int
                ex.getMetodo(),
                ex.getPath()  // Usamos el path que ya viene en la excepción
        );
        return ResponseEntity.status(ex.getCodigoHttp()).body(error);
    }

    // Puedes añadir más manejadores para otros tipos de excepciones aquí
    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<ErrorResponse> handleGeneralException(...)

    // Clase interna para la estructura de respuesta
    @Getter
    @AllArgsConstructor
    private static class ErrorResponse {
        private LocalDateTime timestamp;
        private String entidad;
        private String codigo;
        private String mensaje;
        private int codigoHttp;  // Cambiado a int
        private String metodo;
        private String path;
    }
}