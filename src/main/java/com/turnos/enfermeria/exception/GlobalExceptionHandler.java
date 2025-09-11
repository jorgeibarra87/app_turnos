package com.turnos.enfermeria.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.turnos.enfermeria.exception.custom.TurnoValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores de deserialización JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex, WebRequest request) {
        String errorMessage = "Error en formato JSON";

        if (ex.getCause() instanceof InvalidFormatException ife) { // Java 17+ Pattern Matching
            errorMessage = String.format("Valor inválido para el campo '%s': Se esperaba %s",
                    ife.getPath().get(ife.getPath().size() - 1).getFieldName(),
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

    // Manejo de ApiException personalizada
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, WebRequest request) {
        System.out.println("✅ MANEJANDO ApiException: " + ex.getMensaje());

        // Si es una excepción de NO CONTENT (204), no hay cuerpo en la respuesta
        if (ex.getCodigoHttp() == HttpStatus.NO_CONTENT.value()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getEntidad(),
                ex.getCodigo(),
                ex.getMensaje(),
                ex.getCodigoHttp(),  // Ya es un int
                ex.getMetodo() != null ? ex.getMetodo() : ((ServletWebRequest) request).getRequest().getMethod(),
                ex.getPath() != null ? ex.getPath() : ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return ResponseEntity.status(ex.getCodigoHttp()).body(error);
    }

    // Manejo genérico de excepciones no controladas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, WebRequest request) {

        System.err.println("🔴 EXCEPCIÓN NO CONTROLADA:");
        ex.printStackTrace(); // Esto imprimirá el stacktrace completo en consola
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Sistema",
                "SYS-500",
                ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ((ServletWebRequest) request).getRequest().getMethod(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    @ExceptionHandler(TurnoValidationException.class)
    public ResponseEntity<ErrorResponse> handleTurnoValidationException(TurnoValidationException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getEntidad(),
                ex.getCodigo(),
                ex.getMensaje(),
                ex.getCodigoHttp(),
                ex.getMetodo() != null ? ex.getMetodo() : ((ServletWebRequest) request).getRequest().getMethod(),
                ex.getPath() != null ? ex.getPath() : ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    // DTO interno para la respuesta de error
    @Getter
    @AllArgsConstructor
    private static class ErrorResponse {
        private LocalDateTime timestamp;
        private String entidad;
        private String codigo;
        private String mensaje;
        private int codigoHttp;
        private String metodo;
        private String path;
    }
}
