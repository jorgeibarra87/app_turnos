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
    TURNO_NO_ENCONTRADO("TURNOS", "TR-404", "turno no encontrado", HttpStatus.NOT_FOUND),
    TURNO_DATOS_INVALIDOS("TURNOS", "TR-400", "Datos inválidos para turno", HttpStatus.BAD_REQUEST),
    TURNOS_ESTADO_INVALIDO("TURNOS", "TR-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    TURNO_CONFLICTO("TURNOS", "TR-409", "Conflicto con turno", HttpStatus.CONFLICT),
    TURNO_SIN_CONTENIDO("TURNOS", "TR-204", "No hay turnos registrados", HttpStatus.NO_CONTENT),
    HISTORIAL_TURNO_NO_CONTENIDO("TURNOS", "H_TR-404", "historial de turno no encontrado", HttpStatus.NOT_FOUND),
    RESTAURAR_TURNO_NO_ENCONTRADO("TURNOS", "R_TR-404", "No se encuentra historial de turno", HttpStatus.NOT_FOUND),
    RESTAURAR_TURNO_DATOS_INVALIDOS("TURNOS", "R_TR-400", "Datos inválidos para restaurar el turno", HttpStatus.BAD_REQUEST),
    RESTAURAR_TURNOS_ESTADO_INVALIDO("TURNOS", "R_TR-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    CAMBIAR_ESTADO_DATOS_INVALIDOS("TURNOS", "CE_TR-400", "Datos inválidos para cambio de estado de turno", HttpStatus.BAD_REQUEST),
    CAMBIAR_ESTADO_ESTADO_INVALIDO("TURNOS", "CE_TR-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    CAMBIOS_CUADRO_NO_ENCONTRADO("CAMBIOS_CUADRO_TURNO", "CCT-404", "Cambio de cuadro de turno no encontrado", HttpStatus.NOT_FOUND),
    CAMBIOS_CUADRO_DATOS_INVALIDOS("CAMBIOS_CUADRO_TURNO", "CCT-400", "Datos inválidos para cuadro de turno", HttpStatus.BAD_REQUEST),
    CAMBIOS_CUADRO_ESTADO_INVALIDO("CAMBIOS_CUADRO_TURNO", "CCT-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    CAMBIOS_TURNO_NO_ENCONTRADO("CAMBIOS_TURNO", "CBT-404", "Cambio de turno no encontrado", HttpStatus.NOT_FOUND),
    CAMBIOS_TURNO_DATOS_INVALIDOS("CAMBIOS_TURNO", "CBT-400", "Datos inválidos para cambio de turno", HttpStatus.BAD_REQUEST),
    CAMBIOS_TURNO_ESTADO_INVALIDO("CAMBIOS_TURNO", "CBT-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    CONTRATO_NO_ENCONTRADO("CONTRATO", "CONT-404", "Contrato no encontrado", HttpStatus.NOT_FOUND),
    CONTRATO_DATOS_INVALIDOS("CONTRATO", "CONT-400", "Datos inválidos para contrato", HttpStatus.BAD_REQUEST),
    CONTRATO_ESTADO_INVALIDO("CONTRATO", "CONT-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    TITULO_CONTRATO_NO_ENCONTRADO("TITULOS_CONTRATO", "CONT-404", "titulo de contrato no encontrado", HttpStatus.NOT_FOUND),
    TITULO_CONTRATO_DATOS_INVALIDOS("TITULOS_CONTRATO", "T_CONT-400", "Datos inválidos para titulo de contrato", HttpStatus.BAD_REQUEST),
    TITULO_CONTRATO_ESTADO_INVALIDO("TITULOS_CONTRATO", "T_CONT-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    EQUIPO_NO_ENCONTRADO("EQUIPOS", "EQ-404", "equipo no encontrado", HttpStatus.NOT_FOUND),
    EQUIPO_DATOS_INVALIDOS("EQUIPOS", "EQ-400", "Datos inválidos para equipo", HttpStatus.BAD_REQUEST),
    EQUIPO_ESTADO_INVALIDO("EQUIPOS", "EQ-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    USUARIO_CONTRATO_NO_ENCONTRADO("GESTOR_CONTRATO", "GC-404", "gestor no encontrado", HttpStatus.NOT_FOUND),
    USUARIO_CONTRATO_DATOS_INVALIDOS("GESTOR_CONTRATO", "GC-400", "Datos inválidos para gestor", HttpStatus.BAD_REQUEST),
    USUARIO_CONTRATO_ESTADO_INVALIDO("GESTOR_CONTRATO", "GC-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    MACROPROCESO_NO_ENCONTRADO("MACROPROCESO", "MP-404", "macroproceso no encontrado", HttpStatus.NOT_FOUND),
    MACROPROCESO_DATOS_INVALIDOS("MACROPROCESO", "MP-400", "Datos inválidos para macroproceso", HttpStatus.BAD_REQUEST),
    MACROPROCESO_ESTADO_INVALIDO("MACROPROCESO", "MP-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    NOTIFICACION_NO_ENCONTRADA("NOTIFICACION", "NOT-404", "notificacion no encontrada", HttpStatus.NOT_FOUND),
    NOTIFICACION_DATOS_INVALIDOS("NOTIFICACION", "NOT-400", "Datos inválidos para notificacion", HttpStatus.BAD_REQUEST),
    NOTIFICACION_ESTADO_INVALIDO("NOTIFICACION", "NOT-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    PERSONA_NO_ENCONTRADA("PERSONA", "PER-404", "persona no encontrada", HttpStatus.NOT_FOUND),
    PERSONA_DATOS_INVALIDOS("PERSONA", "PER-400", "Datos inválidos para persona", HttpStatus.BAD_REQUEST),
    PERSONA_ESTADO_INVALIDO("PERSONA", "PER-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    PROCESO_ATENCION_NO_ENCONTRADO("PROCESO_ATENCION", "PAT-404", "persona no encontrada", HttpStatus.NOT_FOUND),
    PROCESO_ATENCION_DATOS_INVALIDOS("PROCESO_ATENCION", "PAT-400", "Datos inválidos para persona", HttpStatus.BAD_REQUEST),
    PROCESO_ATENCION_ESTADO_INVALIDO("PROCESO_ATENCION", "PAT-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    PROCESO_CONTRATO_NO_ENCONTRADO("PROCESO_CONTRATO", "PCON-404", "proceso de contrato no encontrada", HttpStatus.NOT_FOUND),
    PROCESO_CONTRATO_DATOS_INVALIDOS("PROCESO_CONTRATO", "PCON-400", "Datos inválidos para proceso de contrato", HttpStatus.BAD_REQUEST),
    PROCESO_CONTRATO_ESTADO_INVALIDO("PROCESO_CONTRATO", "PCON-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    PROCESO_NO_ENCONTRADO("PROCESO", "PROC-404", "proceso no encontrado", HttpStatus.NOT_FOUND),
    PROCESO_DATOS_INVALIDOS("PROCESO", "PROC-400", "Datos inválidos para proceso", HttpStatus.BAD_REQUEST),
    PROCESO_ESTADO_INVALIDO("PROCESO", "PROC-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    ROL_NO_ENCONTRADO("ROL", "ROL-404", "rol no encontrado", HttpStatus.NOT_FOUND),
    ROL_DATOS_INVALIDOS("ROL", "ROL-400", "Datos inválidos para rol", HttpStatus.BAD_REQUEST),
    ROL_ESTADO_INVALIDO("ROL", "ROL-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    SECCION_SERVICIO_NO_ENCONTRADA("SECCION_SERVICIO", "SSER-404", "seccion no encontrada", HttpStatus.NOT_FOUND),
    SECCION_SERVICIO_DATOS_INVALIDOS("SECCION_SERVICIO", "SSER-400", "Datos inválidos para seccion", HttpStatus.BAD_REQUEST),
    SECCION_SERVICIO_ESTADO_INVALIDO("SECCION_SERVICIO", "SSER-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    SERVICIO_NO_ENCONTRADO("SERVICIO", "SER-404", "servicio no encontrado", HttpStatus.NOT_FOUND),
    SERVICIO_DATOS_INVALIDOS("SERVICIO", "SER-400", "Datos inválidos para servicio", HttpStatus.BAD_REQUEST),
    SERVICIO_ESTADO_INVALIDO("SERVICIO", "SER-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    SUBSECCION_SERVICIO_NO_ENCONTRADO("SUBSECCION_SERVICIO", "ROL-404", "subseccion no encontrada", HttpStatus.NOT_FOUND),
    SUBSECCION_SERVICIO_DATOS_INVALIDOS("SUBSECCION_SERVICIO", "ROL-400", "Datos inválidos para subseccion", HttpStatus.BAD_REQUEST),
    SUBSECCION_SERVICIO_ESTADO_INVALIDO("SUBSECCION_SERVICIO", "ROL-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    TIPO_ATENCION_NO_ENCONTRADA("TIPO_ATENCION_", "TAT-404", "tipo atencion no encontrado", HttpStatus.NOT_FOUND),
    TIPO_ATENCION_DATOS_INVALIDOS("TIPO_ATENCION_", "TAT-400", "Datos inválidos para tipo atencion", HttpStatus.BAD_REQUEST),
    TIPO_ATENCION_ESTADO_INVALIDO("TIPO_ATENCION_", "TAT-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    TIPO_FORMACION_NO_ENCONTRADA("TIPO_ATENCION_", "TAT-404", "tipo atencion no encontrado", HttpStatus.NOT_FOUND),
    TIPO_FORMACION_DATOS_INVALIDOS("TIPO_ATENCION_", "TAT-400", "Datos inválidos para tipo atencion", HttpStatus.BAD_REQUEST),
    TIPO_FORMACION_ESTADO_INVALIDO("TIPO_ATENCION_", "TAT-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    TIPO_TURNO_NO_ENCONTRADO(" TIPO_TURNO", "TTUR-404", "tipo turno no encontrado", HttpStatus.NOT_FOUND),
    TIPO_TURNO_DATOS_INVALIDOS(" TIPO_TURNO", "TTUR-400", "Datos inválidos para tipo turno", HttpStatus.BAD_REQUEST),
    TIPO_TURNO_ESTADO_INVALIDO(" TIPO_TURNO", "TTUR-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    TITULOS_FORMACION_NO_ENCONTRADO("TITULOS_FORMACION", "TITF-404", "titulo de formación no encontrado", HttpStatus.NOT_FOUND),
    TITULOS_FORMACION_DATOS_INVALIDOS("TITULOS_FORMACION", "TITF-400", "Datos inválidos para titulo de formación", HttpStatus.BAD_REQUEST),
    TITULOS_FORMACION_ESTADO_INVALIDO("TITULOS_FORMACION", "TITF-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    USUARIO_NO_ENCONTRADO("TITULOS_FORMACION", "TITF-404", "titulo de formación no encontrado", HttpStatus.NOT_FOUND),
    USUARIO_DATOS_INVALIDOS("TITULOS_FORMACION", "TITF-400", "Datos inválidos para titulo de formación", HttpStatus.BAD_REQUEST),
    USUARIO_ESTADO_INVALIDO("TITULOS_FORMACION", "TITF-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    TITULO_USUARIO_NO_ENCONTRADO("TITULOS_USUARIO", "TUSU-404", "titulo de contrato no encontrado", HttpStatus.NOT_FOUND),
    TITULO_USUARIO_DATOS_INVALIDOS("TITULOS_USUARIO", "TUSU-400", "Datos inválidos para titulo de contrato", HttpStatus.BAD_REQUEST),
    TITULO_USUARIO_ESTADO_INVALIDO("TITULOS_USUARIO", "TUSU-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    ROL_USUARIO_NO_ENCONTRADO("TITULOS_USUARIO", "TUSU-404", "titulo de contrato no encontrado", HttpStatus.NOT_FOUND),
    ROL_USUARIO_DATOS_INVALIDOS("TITULOS_USUARIO", "TUSU-400", "Datos inválidos para titulo de contrato", HttpStatus.BAD_REQUEST),
    ROL_USUARIO_ESTADO_INVALIDO("TITULOS_USUARIO", "TUSU-409", "Estado inválido para la operación", HttpStatus.CONFLICT),
    TURNO_HORAS_EXCESIVAS("TURNOS_VALIDACION", "TRN-422-01", "Exceso de horas en turno", HttpStatus.UNPROCESSABLE_ENTITY),
    TURNO_HORAS_DIARIAS_EXCESIVAS("TURNOS_VALIDACION", "TRN-422-02", "Exceso de horas diarias", HttpStatus.UNPROCESSABLE_ENTITY),
    TURNO_HORAS_MENSUALES_EXCESIVAS("TURNOS_VALIDACION", "TRN-422-03", "Exceso de horas mensuales", HttpStatus.UNPROCESSABLE_ENTITY),
    TURNO_24H_SIN_EXCEPCION("TURNOS_VALIDACION", "TRN-422-04", "Turno de 24 horas sin excepción", HttpStatus.UNPROCESSABLE_ENTITY),
    TURNO_SOLAPAMIENTO("TURNOS_VALIDACION", "TRN-422-05", "Solapamiento de turnos", HttpStatus.UNPROCESSABLE_ENTITY),
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