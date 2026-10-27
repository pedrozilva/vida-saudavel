package com.vidasaudavel.model;

import java.time.LocalDate;

public class AssinaturaBasica extends Assinatura {

    public static final double VALOR_MENSAL = 150.0;
    private static final double VALOR_CONSULTA_EXTRA = 90.0;

    public AssinaturaBasica(LocalDate dataInicio) {
        super(VALOR_MENSAL, dataInicio);
    }

    @Override
    public int intervaloRevisaoDias() {
        return 30;
    }

    @Override
    public int consultasInclusas() {
        return 1; // 1 por mês
    }

    @Override
    public double valorConsultaExtra() {
        return VALOR_CONSULTA_EXTRA;
    }

    @Override
    public String getTipo() {
        return "Básico";
    }
}
