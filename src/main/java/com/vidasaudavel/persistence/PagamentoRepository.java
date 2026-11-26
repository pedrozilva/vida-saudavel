package com.vidasaudavel.persistence;

import com.google.gson.reflect.TypeToken;
import com.vidasaudavel.model.Pagamento;

import java.lang.reflect.Type;

public class PagamentoRepository extends Repositorio<Pagamento> {

    private static final String ARQUIVO_PADRAO = "data/pagamentos.json";
    private static final Type TIPO_LISTA = new TypeToken<java.util.List<Pagamento>>() {}.getType();

    public PagamentoRepository() {
        this(ARQUIVO_PADRAO);
    }

    public PagamentoRepository(String caminhoArquivo) {
        super(caminhoArquivo);
    }

    @Override
    protected Type getTipoLista() {
        return TIPO_LISTA;
    }
}
