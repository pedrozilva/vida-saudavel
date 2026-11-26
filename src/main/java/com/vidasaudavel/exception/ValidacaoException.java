package com.vidasaudavel.exception;

/** RNF03 — entrada inválida vinda da interface ou de dados persistidos. */
public class ValidacaoException extends Exception {
    public ValidacaoException(String message) {
        super(message);
    }
}
