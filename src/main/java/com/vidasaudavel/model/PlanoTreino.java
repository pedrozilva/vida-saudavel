package com.vidasaudavel.model;

import java.util.ArrayList;
import java.util.List;

public class PlanoTreino extends Plano {

    private List<String> exercicios = new ArrayList<>();

    public PlanoTreino(Objetivo objetivo) {
        super(objetivo);
    }

    public List<String> getExercicios() {
        return exercicios;
    }

    public void adicionarExercicio(String exercicio) {
        if (exercicio != null && !exercicio.isBlank()) {
            exercicios.add(exercicio.trim());
        }
    }

    @Override
    public String getTipo() {
        return "PlanoTreino";
    }
}
