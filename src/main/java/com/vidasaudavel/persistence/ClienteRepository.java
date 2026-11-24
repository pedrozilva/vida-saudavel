package com.vidasaudavel.persistence;

import com.google.gson.reflect.TypeToken;
import com.vidasaudavel.model.Cliente;

import java.lang.reflect.Type;

public class ClienteRepository extends Repositorio<Cliente> {

    private static final String ARQUIVO_PADRAO = "data/clientes.json";
    private static final Type TIPO_LISTA = new TypeToken<java.util.List<Cliente>>() {}.getType();

    public ClienteRepository() {
        this(ARQUIVO_PADRAO);
    }

    public ClienteRepository(String caminhoArquivo) {
        super(caminhoArquivo);
    }

    @Override
    protected Type getTipoLista() {
        return TIPO_LISTA;
    }
}
