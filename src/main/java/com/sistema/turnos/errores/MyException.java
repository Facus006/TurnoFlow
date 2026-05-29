package com.sistema.turnos.errores;

import org.springframework.http.HttpStatus;

public class MyException extends RuntimeException {

    private final HttpStatus status;

    public MyException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
