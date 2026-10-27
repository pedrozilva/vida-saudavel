package com.vidasaudavel.model;

import java.time.LocalDate;

public class AssinaturaVIP extends Assinatura {

    public static final double VALOR_MENSAL = 300.0;
    private static final double VALOR_CONSULTA_EXTRA = 45.0; // metade do Básico

    public AssinaturaVIP(LocalDate dataInicio) {
        super(VALOR_MENSAL, dataInicio);
    }

    @Override
    public int intervaloRevisaoDias() {
        return 7;
    }

    @Override
    public int consultasInclusas() {
        return 1; // 1 por semana
    }

    @Override
    public double valorConsultaExtra() {
        return VALOR_CONSULTA_EXTRA;
    }

    @Override
    public String getTipo() {
        return "VIP";
    }
}
