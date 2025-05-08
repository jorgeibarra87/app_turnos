package com.turnos.enfermeria.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler  {

    // Maneja errores de deserialización JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex, WebRequest request) {
        String errorMessage = "Error en formato JSON";

        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            errorMessage = String.format("Valor inválido para el campo '%s': Se esperaba %s",
                    ife.getPath().get(ife.getPath().size()-1).getFieldName(),
                    ife.getTargetType().getSimpleName());
        }

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Sistema",
                "SYS-400",
                errorMessage,
                HttpStatus.BAD_REQUEST.value(),
                ((ServletWebRequest) request).getRequest().getMethod(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, WebRequest request) {

        // Si es una excepción de NO CONTENT (204)
        if (ex.getCodigoHttp() == HttpStatus.NO_CONTENT.value()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
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