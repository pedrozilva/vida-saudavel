package com.vidasaudavel.model;

/**
 * Classe abstrata para os planos de treino/nutrição.
 * O cálculo da meta calórica muda conforme o objetivo e o tipo de
 * plano (polimorfismo): cada subclasse aplica o ajuste correto sobre
 * o gasto estimado.
 */
public abstract class Plano {

    private Objetivo objetivo;

    protected Plano(Objetivo objetivo) {
        this.objetivo = objetivo;
    }

    public Objetivo getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(Objetivo objetivo) {
        this.objetivo = objetivo;
    }

    /**
     * Meta calórica do plano (RF05): gasto estimado = peso × 30 kcal,
     * mais o ajuste do objetivo (−500 emagrecimento, +300 ganho de
     * massa, 0 reeducação alimentar).
     */
    public double calcularMetaCalorica(double pesoKg) {
        double gastoEstimado = pesoKg * 30.0;
        return gastoEstimado + ajusteObjetivo();
    }

    private double ajusteObjetivo() {
        return switch (objetivo) {
            case EMAGRECIMENTO -> -500.0;
            case GANHO_DE_MASSA -> 300.0;
            case REEDUCACAO_ALIMENTAR -> 0.0;
        };
    }

    public abstract String getTipo();
}
