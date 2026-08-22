package com.estudo.curso.shared;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StanderError {
    private static final long serialVersionUID = 1L;

    private final List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Instant timestemp, Integer status, String error, String message, String path) {
        super(timestemp, status, error, message, path);
    }

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addError(String fieldName, String message) {
        errors.add(new FieldMessage(fieldName, message));
    }
}
