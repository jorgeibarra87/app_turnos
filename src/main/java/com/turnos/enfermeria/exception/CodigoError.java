package com.turnos.enfermeria.exception;

import org.springframework.http.HttpStatus;

public enum CodigoError {
    BLOQUE_SERVICIO_NO_ENCONTRADO("BLOQUE_SERVICIO", "BS-404", "Bloque de servicio no encontrado", HttpStatus.NOT_FOUND),
    BLOQUE_SERVICIO_DATOS_INVALIDOS("BLOQUE_SERVICIO", "BS-400", "Datos inválidos para bloque de servicio", HttpStatus.BAD_REQUEST),
    BLOQUE_SERVICIO_CONFLICTO("BLOQUE_SERVICIO", "BS-409", "Conflicto con bloque de servicio", HttpStatus.CONFLICT),
    BLOQUE_SERVICIO_SIN_CONTENIDO("BLOQUE_SERVICIO", "BS-204", "No hay bloques de servicio registrados", HttpStatus.NO_CONTENT),
    CUADRO_TURNO_NO_ENCONTRADO("CUADRO_TURNO", "CT-404", "Cuadro de turno no encontrado", HttpStatus.NOT_FOUND),
    CUADRO_TURNO_DATOS_INVALIDOS("CUADRO_TURNO", "CT-400", "Datos inválidos para cuadro de turno", HttpStatus.BAD_REQUEST),
    CUADRO_TURNOS_ESTADO_INVALIDO("CuadroTurnos", "CT-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    CUADRO_TURNO_CONFLICTO("CUADRO_TURNO", "CT-409", "Conflicto con cuadro de turno", HttpStatus.CONFLICT),
    CUADRO_TURNO_SIN_CONTENIDO("CUADRO_TURNO", "CT-204", "No hay cuadros de turno registrados", HttpStatus.NO_CONTENT),
    HISTORIAL_CUADRO_TURNO_NO_ENCONTRADO("CUADRO_TURNO", "CT-404", "Historial de cuadro de turno no encontrado", HttpStatus.NOT_FOUND),
    ERROR_PROCESAMIENTO("Sistema", "SYS-500", "Error durante el procesamiento", HttpStatus.INTERNAL_SERVER_ERROR),
    MACROPROCESO_NO_ENCONTRADO("MACROPROCESO", "MP-404", "Macroproceso no encontrado", HttpStatus.NOT_FOUND),
    PROCESO_NO_ENCONTRADO("PROCESO", "PR-404", "proceso no encontrado", HttpStatus.NOT_FOUND),
    SECCION_SERVICIO_NO_ENCONTRADA("SECCION_SERVICIO", "SSER-404", "seccion de servicio no encontrada", HttpStatus.NOT_FOUND),
    PROCESO_ATENCION_NO_ENCONTRADO("PROCESO_ATENCION", "PRA-404", "proceso de atención no encontrado", HttpStatus.NOT_FOUND),
    SERVICIO_NO_ENCONTRADO("SERVICIO", "SER-404", "servicio no encontrado", HttpStatus.NOT_FOUND),
    MACROPROCESO_DATOS_INVALIDOS("CUADRO_TURNO", "MP-400", "Datos inválidos para macroproceso", HttpStatus.BAD_REQUEST),
    PROCESO_DATOS_INVALIDOS("CUADRO_TURNO", "PR-400", "Datos inválidos para proceso", HttpStatus.BAD_REQUEST),

    ;

    private final String entidad;
    private final String codigo;
    private final String mensaje;
    private final HttpStatus status;

    CodigoError(String entidad, String codigo, String mensaje, HttpStatus status) {
        this.entidad = entidad;
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.status = status;
    }

    // Getters
    public String getEntidad() {
        return entidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public HttpStatus getStatus() {
        return status;
    }
}