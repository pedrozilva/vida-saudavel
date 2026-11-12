package com.vidasaudavel.service;

/** RF13 — resumo de receita por serviço, usado pelo RelatorioPanel. */
public record RelatorioFinanceiro(
        double receitaMensalidades,
        double receitaMultas,
        double receitaConsultasExtras,
        long pagamentosEmAtraso
) {
    public double receitaTotal() {
        return receitaMensalidades + receitaMultas + receitaConsultasExtras;
    }
}
