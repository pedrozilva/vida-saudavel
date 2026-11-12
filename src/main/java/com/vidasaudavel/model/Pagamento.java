package com.vidasaudavel.model;

import java.time.LocalDate;

/**
 * Pagamento de uma mensalidade (RF11). O controle de atraso e multa
 * fica no service, que consulta estes dados e os de Assinatura.
 */
public class Pagamento {

    private int id;
    private int clienteId;
    private double valorMensalidade;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento; // null = ainda não pago
    private double multaAplicada;

    public Pagamento(int id, int clienteId, double valorMensalidade, LocalDate dataVencimento) {
        this.id = id;
        this.clienteId = clienteId;
        this.valorMensalidade = valorMensalidade;
        this.dataVencimento = dataVencimento;
        this.dataPagamento = null;
        this.multaAplicada = 0.0;
    }

    public int getId() {
        return id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public double getValorMensalidade() {
        return valorMensalidade;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public double getMultaAplicada() {
        return multaAplicada;
    }

    public boolean isPago() {
        return dataPagamento != null;
    }

    /** RF11 — true se o vencimento já passou e o pagamento ainda não ocorreu. */
    public boolean estaAtrasado(LocalDate hoje) {
        return !isPago() && hoje.isAfter(dataVencimento);
    }

    public void registrarPagamento(LocalDate dataPagamento, double multaAplicada) {
        this.dataPagamento = dataPagamento;
        this.multaAplicada = multaAplicada;
    }

    public double getValorTotal() {
        return valorMensalidade + multaAplicada;
    }
}
