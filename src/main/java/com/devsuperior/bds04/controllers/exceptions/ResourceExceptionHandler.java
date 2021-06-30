package com.devsuperior.bds04.controllers.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResourceExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError standardError = new ValidationError();
        standardError.setTimestamp(Instant.now());
        standardError.setStatus(status.value());
        standardError.setError("Validation exception");
        standardError.setMessage(e.getMessage());
        standardError.setPath(request.getRequestURI());

        for (FieldError f : e.getBindingResult().getFieldErrors()){
            standardError.addError(f.getField(), f.getDefaultMessage());
        }


        return ResponseEntity.status(status).body(standardError);

    }
}