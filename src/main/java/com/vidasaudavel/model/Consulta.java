package com.vidasaudavel.model;

import java.time.LocalDateTime;

/**
 * Agendamento de consulta entre um cliente e um profissional (RF07).
 * Referencia cliente e profissional pelo id (e não por objeto) para
 * manter a persistência em JSON simples e sem referências circulares.
 */
public class Consulta {

    private int id;
    private int clienteId;
    private int profissionalId;
    private LocalDateTime dataHora;
    private boolean extra;
    private double valor;

    public Consulta(int id, int clienteId, int profissionalId, LocalDateTime dataHora, boolean extra, double valor) {
        this.id = id;
        this.clienteId = clienteId;
        this.profissionalId = profissionalId;
        this.dataHora = dataHora;
        this.extra = extra;
        this.valor = valor;
    }

    public int getId() {
        return id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public int getProfissionalId() {
        return profissionalId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public boolean isExtra() {
        return extra;
    }

    public double getValor() {
        return valor;
    }

    /** RF07 — duas consultas conflitam se forem do mesmo profissional no mesmo horário. */
    public boolean conflitaCom(Consulta outra) {
        return this.profissionalId == outra.profissionalId && this.dataHora.equals(outra.dataHora);
    }
}
