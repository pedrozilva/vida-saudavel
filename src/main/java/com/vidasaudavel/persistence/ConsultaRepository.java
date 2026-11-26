package com.vidasaudavel.persistence;

import com.google.gson.reflect.TypeToken;
import com.vidasaudavel.model.Consulta;

import java.lang.reflect.Type;

public class ConsultaRepository extends Repositorio<Consulta> {

    private static final String ARQUIVO_PADRAO = "data/consultas.json";
    private static final Type TIPO_LISTA = new TypeToken<java.util.List<Consulta>>() {}.getType();

    public ConsultaRepository() {
        this(ARQUIVO_PADRAO);
    }

    public ConsultaRepository(String caminhoArquivo) {
        super(caminhoArquivo);
    }

    @Override
    protected Type getTipoLista() {
        return TIPO_LISTA;
    }
}
