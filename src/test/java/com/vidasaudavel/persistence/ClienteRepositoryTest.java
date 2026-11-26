package com.vidasaudavel.persistence;

import com.vidasaudavel.exception.ArquivoNaoEncontradoException;
import com.vidasaudavel.model.Cliente;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** TC10 — arquivo ausente não quebra o sistema; arquivo corrompido é sinalizado. */
class ClienteRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void tc10_arquivoAusenteDevolveListaVazia() throws ArquivoNaoEncontradoException {
        ClienteRepository repo = new ClienteRepository(tempDir.resolve("nao_existe.json").toString());
        assertTrue(repo.carregarTodos().isEmpty());
    }

    @Test
    void salvarEDepoisCarregarMantemOsDados() throws ArquivoNaoEncontradoException {
        ClienteRepository repo = new ClienteRepository(tempDir.resolve("clientes.json").toString());
        Cliente cliente = new Cliente(1, "Fernanda Alves", "666.666.666-66", "86999990005",
                "fernanda@email.com", 60, 1.65);

        repo.salvarTodos(List.of(cliente));
        List<Cliente> carregados = repo.carregarTodos();

        assertEquals(1, carregados.size());
        assertEquals("Fernanda Alves", carregados.get(0).getNome());
    }

    @Test
    void arquivoCorrompidoLancaArquivoNaoEncontradoException() throws Exception {
        Path arquivo = tempDir.resolve("corrompido.json");
        Files.writeString(arquivo, "{ isso não é uma lista JSON válida ]");

        ClienteRepository repo = new ClienteRepository(arquivo.toString());
        assertThrows(ArquivoNaoEncontradoException.class, repo::carregarTodos);
    }
}
