package com.vidasaudavel.model;

public class PersonalTrainer extends Profissional {

    private String modalidade; // ex.: musculação, funcional

    public PersonalTrainer(int id, String nome, String cpf, String telefone, String email, String modalidade) {
        super(id, nome, cpf, telefone, email, "Personal Training");
        this.modalidade = modalidade;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    @Override
    public String tipoAtendimento() {
        return "Treino (" + (modalidade == null || modalidade.isBlank() ? "geral" : modalidade) + ")";
    }
}
