package de.device.demo.controllers;

import de.device.demo.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class ErrorController {

    private static final Logger log = LoggerFactory.getLogger(ErrorController.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Map<String, String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        final Map<String, String> errorResponse;

        if (ex.getRequiredType() == UUID.class) {
            errorResponse = Map.of("message", "Invalid UUID format");
        } else {
            errorResponse = Map.of("message", "Invalid request parameter " + ex.getParameter().getParameterName());
        }

        log.error(errorResponse.toString());

        return errorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        (error) -> {
                            switch (error) {
                                case FieldError fe -> errors.put(fe.getField(), error.getDefaultMessage());
                                case MessageSourceResolvable msr -> errors.put("message", msr.getDefaultMessage());
                            }
                        }
                );

        log.error(errors.toString());

        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DeviceNotFoundException.class)
    public Map<String, String> handleDeviceNotFoundException(DeviceNotFoundException e) {
        log.error(e.getMessage());

        return Map.of("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DeviceModificationException.class)
    public Map<String, String> handleDeviceInUseUpdateModificationException(DeviceModificationException e) {
        log.error(e.getMessage());

        return Map.of("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());

        return Map.of("message", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllOtherExceptions(Exception ex) {
        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
                Map.of("error", "An unexpected error occurred."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
