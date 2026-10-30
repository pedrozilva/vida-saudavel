package com.vidasaudavel.model;

import com.vidasaudavel.util.ImcUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente da Vida Saudável. Herda os dados básicos de Pessoa e
 * acrescenta peso/altura, histórico de avaliações, assinatura e
 * plano de treino/nutrição.
 */
public class Cliente extends Pessoa {

    private double peso;
    private double altura;
    private List<AvaliacaoFisica> avaliacoes = new ArrayList<>();
    private Assinatura assinatura;
    private Plano plano;

    public Cliente(int id, String nome, String cpf, String telefone, String email,
                    double peso, double altura) {
        super(id, nome, cpf, telefone, email);
        setPeso(peso);
        setAltura(altura);
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        if (peso <= 0) {
            throw new IllegalArgumentException("Peso deve ser maior que zero.");
        }
        this.peso = peso;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        if (altura <= 0) {
            throw new IllegalArgumentException("Altura deve ser maior que zero.");
        }
        this.altura = altura;
    }

    public List<AvaliacaoFisica> getAvaliacoes() {
        return avaliacoes;
    }

    public Assinatura getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(Assinatura assinatura) {
        this.assinatura = assinatura;
    }

    public Plano getPlano() {
        return plano;
    }

    public void setPlano(Plano plano) {
        this.plano = plano;
    }

    /** RF02 — calcula o IMC atual do cliente. */
    public double calcularImc() {
        return ImcUtil.calcularImc(peso, altura);
    }

    /** RF02 — classifica o IMC atual do cliente. */
    public String classificarImc() {
        return ImcUtil.classificar(calcularImc());
    }

    /** RF03 — registra uma nova avaliação física no histórico do cliente. */
    public AvaliacaoFisica registrarAvaliacao(LocalDate data, double pesoAtual, double alturaAtual) {
        double imc = ImcUtil.calcularImc(pesoAtual, alturaAtual);
        String classificacao = ImcUtil.classificar(imc);
        AvaliacaoFisica avaliacao = new AvaliacaoFisica(data, pesoAtual, alturaAtual, imc, classificacao);
        this.avaliacoes.add(avaliacao);
        // mantém peso/altura atuais do cliente sincronizados com a última avaliação
        setPeso(pesoAtual);
        setAltura(alturaAtual);
        return avaliacao;
    }

    @Override
    public String exibirDados() {
        String tipoAssinatura = assinatura != null ? assinatura.getTipo() : "sem assinatura";
        return "Cliente #" + getId() + " " + getNome() + " — IMC " + String.format("%.1f", calcularImc())
                + " (" + classificarImc() + "), assinatura " + tipoAssinatura;
    }
}
