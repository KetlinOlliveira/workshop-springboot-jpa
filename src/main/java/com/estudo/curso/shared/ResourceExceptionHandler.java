package com.estudo.curso.shared;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ResourceExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StanderError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
    String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StanderError err = new StanderError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<StanderError> database(DataBaseException e, HttpServletRequest request){
        String error = "Data base error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StanderError err = new StanderError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ValidationError err = new ValidationError(Instant.now(), status.value(), "Validation error",
                "Um ou mais campos são inválidos", request.getRequestURI());
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StanderError> accessDenied(AccessDeniedException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.FORBIDDEN;
        StanderError err = new StanderError(Instant.now(), status.value(), "Forbidden",
                "Você não tem permissão para acessar este recurso", request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StanderError> authenticationFailed(AuthenticationException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        StanderError err = new StanderError(Instant.now(), status.value(), "Unauthorized",
                "Credenciais inválidas", request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StanderError> genericError(Exception e, HttpServletRequest request){
        log.error("Erro não tratado ao processar {}", request.getRequestURI(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        StanderError err = new StanderError(Instant.now(), status.value(), "Internal server error",
                "Ocorreu um erro inesperado", request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

}
