package com.vidasaudavel.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.vidasaudavel.exception.ArquivoNaoEncontradoException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório genérico de persistência em arquivo JSON (RNF02).
 * Cada subclasse só precisa dizer qual arquivo usa e qual o tipo de
 * lista — toda a leitura/gravação e o tratamento de erro de arquivo
 * ausente (RNF03 / TC10) ficam concentrados aqui.
 */
public abstract class Repositorio<T> {

    protected final Gson gson = GsonFactory.criar();
    private final String caminhoArquivo;

    /** @param caminhoArquivo caminho do arquivo JSON; as subclasses usam o padrão em data/,
     *  mas os testes podem passar um arquivo temporário para não tocar os dados reais. */
    protected Repositorio(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    protected String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    protected abstract Type getTipoLista();

    /**
     * Carrega todos os registros do arquivo. Se o arquivo ainda não
     * existir (primeira execução), devolve uma lista vazia em vez de
     * lançar exceção (RNF03). Um arquivo presente, porém corrompido,
     * é reportado via ArquivoNaoEncontradoException para a camada
     * acima decidir como avisar o usuário.
     */
    public List<T> carregarTodos() throws ArquivoNaoEncontradoException {
        Path caminho = Path.of(getCaminhoArquivo());
        if (!Files.exists(caminho)) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(caminho.toFile())) {
            List<T> lista = gson.fromJson(reader, getTipoLista());
            return lista != null ? lista : new ArrayList<>();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (JsonSyntaxException | IOException e) {
            throw new ArquivoNaoEncontradoException(
                    "Não foi possível ler " + getCaminhoArquivo() + " — arquivo pode estar corrompido.", e);
        }
    }

    public void salvarTodos(List<T> lista) throws ArquivoNaoEncontradoException {
        Path caminho = Path.of(getCaminhoArquivo());
        try {
            if (caminho.getParent() != null) {
                Files.createDirectories(caminho.getParent());
            }
            try (FileWriter writer = new FileWriter(caminho.toFile())) {
                gson.toJson(lista, getTipoLista(), writer);
            }
        } catch (IOException e) {
            throw new ArquivoNaoEncontradoException(
                    "Não foi possível gravar " + getCaminhoArquivo() + ".", e);
        }
    }

    /** Gera o próximo id disponível, assumindo uma lista já carregada. */
    protected int proximoId(List<Integer> idsExistentes) {
        return idsExistentes.stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }
}
