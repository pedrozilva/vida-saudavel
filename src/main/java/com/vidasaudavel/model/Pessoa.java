package com.vidasaudavel.model;

import java.util.Objects;

/**
 * Classe base abstrata para qualquer pessoa cadastrada no sistema
 * (cliente ou profissional). Concentra os dados comuns e aplica
 * encapsulamento: os atributos são privados e validados nos setters.
 */
public abstract class Pessoa {

    private int id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;

    protected Pessoa(int id, String nome, String cpf, String telefone, String email) {
        this.id = id;
        setNome(nome);
        setCpf(cpf);
        this.telefone = telefone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser vazio.");
        }
        this.cpf = cpf.trim();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Cada subclasse decide como se apresenta (polimorfismo).
     */
    public abstract String exibirDados();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pessoa pessoa)) return false;
        return id == pessoa.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
