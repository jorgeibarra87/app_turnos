//package com.turnos.enfermeria.exception.custom;
//
//import com.turnos.enfermeria.exception.ApiException;
//import org.springframework.http.HttpStatus;
//
//public class GenericBadRequestException extends ApiException {
//
//    public GenericBadRequestException(String entityName, String message) {
//        super(HttpStatus.BAD_REQUEST,
//                entityName.toUpperCase() + "_BAD_REQUEST",
//                "Validación fallida para " + entityName + ": " + message);
//    }
//
//    public GenericBadRequestException(String message) {
//        super(HttpStatus.BAD_REQUEST,
//                "INVALID_REQUEST",
//                message);
//    }
//}
