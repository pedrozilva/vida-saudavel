package com.vidasaudavel.model;

import java.time.LocalDate;

/**
 * Registro histórico de uma avaliação física do cliente (RF03).
 * Guarda o IMC já calculado no momento da avaliação para preservar
 * o histórico mesmo que a fórmula de classificação mude no futuro.
 */
public class AvaliacaoFisica {

    private LocalDate data;
    private double peso;
    private double altura;
    private double imc;
    private String classificacaoImc;

    public AvaliacaoFisica(LocalDate data, double peso, double altura, double imc, String classificacaoImc) {
        this.data = data;
        this.peso = peso;
        this.altura = altura;
        this.imc = imc;
        this.classificacaoImc = classificacaoImc;
    }

    public LocalDate getData() {
        return data;
    }

    public double getPeso() {
        return peso;
    }

    public double getAltura() {
        return altura;
    }

    public double getImc() {
        return imc;
    }

    public String getClassificacaoImc() {
        return classificacaoImc;
    }

    @Override
    public String toString() {
        return data + " — peso " + peso + "kg, IMC " + String.format("%.1f", imc) + " (" + classificacaoImc + ")";
    }
}
