package de.device.demo.controllers;

import de.device.demo.errors.DeviceInUseModificationException;
import de.device.demo.errors.DeviceNotFoundException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorController {

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

        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DeviceNotFoundException.class)
    public Map<String, String> handleDeviceNotFoundException(DeviceNotFoundException e) {
        return Map.of("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DeviceInUseModificationException.class)
    public Map<String, String> handleDeviceInUseModificationException(DeviceInUseModificationException e) {
        return Map.of("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException e) {
        return Map.of("message", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllOtherExceptions(Exception ex) {
        return new ResponseEntity<>(
                Map.of("error", "An unexpected error occurred."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
