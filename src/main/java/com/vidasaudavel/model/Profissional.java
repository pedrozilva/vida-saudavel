package com.vidasaudavel.model;

/**
 * Classe abstrata para os profissionais que atendem os clientes.
 * O tipo de atendimento muda conforme a especialidade (polimorfismo
 * em tipoAtendimento()).
 */
public abstract class Profissional extends Pessoa {

    private String especialidade;

    protected Profissional(int id, String nome, String cpf, String telefone, String email, String especialidade) {
        super(id, nome, cpf, telefone, email);
        this.especialidade = especialidade;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    /** Tipo de atendimento prestado (sobrescrito por cada especialidade). */
    public abstract String tipoAtendimento();

    @Override
    public String exibirDados() {
        return "Profissional #" + getId() + " " + getNome() + " — " + tipoAtendimento();
    }

    @Override
    public String toString() {
        return getNome() + " (" + tipoAtendimento() + ")";
    }
}
