package com.vidasaudavel.exception;

/** RF07 — lançada ao tentar agendar uma consulta em horário já ocupado pelo profissional. */
public class ConflitoHorarioException extends Exception {
    public ConflitoHorarioException(String message) {
        super(message);
    }
}
