package com.vidasaudavel.persistence;

import com.google.gson.reflect.TypeToken;
import com.vidasaudavel.model.Profissional;

import java.lang.reflect.Type;

public class ProfissionalRepository extends Repositorio<Profissional> {

    private static final String ARQUIVO_PADRAO = "data/profissionais.json";
    private static final Type TIPO_LISTA = new TypeToken<java.util.List<Profissional>>() {}.getType();

    public ProfissionalRepository() {
        this(ARQUIVO_PADRAO);
    }

    public ProfissionalRepository(String caminhoArquivo) {
        super(caminhoArquivo);
    }

    @Override
    protected Type getTipoLista() {
        return TIPO_LISTA;
    }
}
