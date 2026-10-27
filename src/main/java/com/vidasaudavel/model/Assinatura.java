package com.vidasaudavel.model;

import java.time.LocalDate;

/**
 * Classe abstrata para os planos de assinatura (Básico / VIP).
 * Cada subclasse sobrescreve o comportamento que varia entre elas
 * (polimorfismo): intervalo de revisão, cota de consultas e preço
 * da consulta extra.
 */
public abstract class Assinatura {

    private double valor;
    private LocalDate dataInicio;
    private int mensalidadesAtrasadas;

    protected Assinatura(double valor, LocalDate dataInicio) {
        this.valor = valor;
        this.dataInicio = dataInicio;
        this.mensalidadesAtrasadas = 0;
    }

    public double getValor() {
        return valor;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public int getMensalidadesAtrasadas() {
        return mensalidadesAtrasadas;
    }

    public void registrarAtraso() {
        this.mensalidadesAtrasadas++;
    }

    public void registrarPagamentoEmDia() {
        this.mensalidadesAtrasadas = 0;
    }

    /** RF12 — bloqueia agendamento com 2+ mensalidades atrasadas. */
    public boolean podeAgendarConsulta() {
        return mensalidadesAtrasadas < 2;
    }

    /** RF11 — multa de 2% por atraso. */
    public double calcularMulta() {
        return mensalidadesAtrasadas > 0 ? valor * 0.02 : 0.0;
    }

    /** Dias entre revisões do plano (RF06): 30 no Básico, 7 no VIP. */
    public abstract int intervaloRevisaoDias();

    /** Quantas consultas a assinatura inclui a cada intervaloRevisaoDias() (RF08):
     *  1 a cada 30 dias no Básico, 1 a cada 7 dias (semanal) no VIP. */
    public abstract int consultasInclusas();

    /** Valor cobrado por uma consulta além da cota (RF08). */
    public abstract double valorConsultaExtra();

    public abstract String getTipo();
}
