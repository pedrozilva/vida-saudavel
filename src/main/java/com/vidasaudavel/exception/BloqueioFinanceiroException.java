package com.vidasaudavel.exception;

/** RF12 — lançada ao tentar agendar consulta para cliente com 2+ mensalidades atrasadas. */
public class BloqueioFinanceiroException extends Exception {
    public BloqueioFinanceiroException(String message) {
        super(message);
    }
}
