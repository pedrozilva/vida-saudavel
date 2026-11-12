package com.vidasaudavel.model;

public class Nutricionista extends Profissional {

    private String registroProfissional; // ex.: CRN

    public Nutricionista(int id, String nome, String cpf, String telefone, String email, String registroProfissional) {
        super(id, nome, cpf, telefone, email, "Consultoria Nutricional");
        this.registroProfissional = registroProfissional;
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public void setRegistroProfissional(String registroProfissional) {
        this.registroProfissional = registroProfissional;
    }

    @Override
    public String tipoAtendimento() {
        return "Nutrição (" + registroProfissional + ")";
    }
}
