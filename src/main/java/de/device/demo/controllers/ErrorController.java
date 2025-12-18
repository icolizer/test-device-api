package de.device.demo.controllers;

import de.device.demo.errors.DeviceInUseDeleteException;
import de.device.demo.errors.DeviceInUseUpdateModificationException;
import de.device.demo.errors.DeviceNotFoundException;
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

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorController {

    private static final Logger log = LoggerFactory.getLogger(ErrorController.class);

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
    @ExceptionHandler(DeviceInUseUpdateModificationException.class)
    public Map<String, String> handleDeviceInUseUpdateModificationException(DeviceInUseUpdateModificationException e) {
        log.error(e.getMessage());

        return Map.of("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DeviceInUseDeleteException.class)
    public Map<String, String> handleDeviceInUseDeleteException(DeviceInUseDeleteException e) {
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
