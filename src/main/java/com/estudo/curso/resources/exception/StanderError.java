package com.estudo.curso.resources.exception;

import java.io.Serializable;
import java.time.Instant;

public class StanderError implements Serializable {
    private static final long serialVersionUID = 1L;
    private Instant timestemp;
    private Integer status;
    private String error;
    private String message;
    private String path;

    public StanderError(){

    }
    public StanderError(Instant timestemp, Integer status, String error, String message, String path) {
        this.timestemp = timestemp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestemp() {
        return timestemp;
    }

    public StanderError setTimestemp(Instant timestemp) {
        this.timestemp = timestemp;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public StanderError setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getError() {
        return error;
    }

    public StanderError setError(String error) {
        this.error = error;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public StanderError setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getPath() {
        return path;
    }

    public StanderError setPath(String path) {
        this.path = path;
        return this;
    }


}
