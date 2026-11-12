package com.vidasaudavel.service;

import com.vidasaudavel.exception.ArquivoNaoEncontradoException;
import com.vidasaudavel.exception.ValidacaoException;
import com.vidasaudavel.model.*;
import com.vidasaudavel.persistence.ClienteRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Regras de negócio sobre Cliente: cadastro (RF01), avaliação física
 * (RF03), planos de treino/nutrição (RF04-RF06) e assinatura.
 */
public class ClienteService {

    private final ClienteRepository repository;
    private List<Cliente> clientes;

    public ClienteService(ClienteRepository repository) throws ArquivoNaoEncontradoException {
        this.repository = repository;
        this.clientes = repository.carregarTodos();
    }

    public List<Cliente> listarClientes() {
        return clientes;
    }

    public Optional<Cliente> buscarPorId(int id) {
        return clientes.stream().filter(c -> c.getId() == id).findFirst();
    }

    /** RF01 — cadastra um novo cliente, validando os campos obrigatórios. */
    public Cliente cadastrarCliente(String nome, String cpf, String telefone, String email,
                                     double peso, double altura) throws ValidacaoException, ArquivoNaoEncontradoException {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException("Nome é obrigatório.");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new ValidacaoException("CPF é obrigatório.");
        }
        if (peso <= 0 || altura <= 0) {
            throw new ValidacaoException("Peso e altura devem ser maiores que zero.");
        }
        int novoId = clientes.stream().mapToInt(Cliente::getId).max().orElse(0) + 1;
        Cliente cliente = new Cliente(novoId, nome, cpf, telefone, email, peso, altura);
        clientes.add(cliente);
        repository.salvarTodos(clientes);
        return cliente;
    }

    /** RF01 — remove um cliente cadastrado. */
    public void removerCliente(int id) throws ArquivoNaoEncontradoException {
        clientes.removeIf(c -> c.getId() == id);
        repository.salvarTodos(clientes);
    }

    public void salvar() throws ArquivoNaoEncontradoException {
        repository.salvarTodos(clientes);
    }

    /** RF03 — registra uma nova avaliação física e mantém o histórico do cliente. */
    public AvaliacaoFisica registrarAvaliacao(int clienteId, LocalDate data, double peso, double altura)
            throws ValidacaoException, ArquivoNaoEncontradoException {
        Cliente cliente = buscarPorId(clienteId)
                .orElseThrow(() -> new ValidacaoException("Cliente não encontrado."));
        if (peso <= 0 || altura <= 0) {
            throw new ValidacaoException("Peso e altura devem ser maiores que zero.");
        }
        AvaliacaoFisica avaliacao = cliente.registrarAvaliacao(data, peso, altura);
        repository.salvarTodos(clientes);
        return avaliacao;
    }

    /** Define/atualiza a assinatura do cliente (Básico ou VIP). */
    public void definirAssinatura(int clienteId, String tipo, LocalDate dataInicio)
            throws ValidacaoException, ArquivoNaoEncontradoException {
        Cliente cliente = buscarPorId(clienteId)
                .orElseThrow(() -> new ValidacaoException("Cliente não encontrado."));
        Assinatura assinatura = "VIP".equalsIgnoreCase(tipo)
                ? new AssinaturaVIP(dataInicio)
                : new AssinaturaBasica(dataInicio);
        cliente.setAssinatura(assinatura);
        repository.salvarTodos(clientes);
    }

    /** RF04/RF05 — cria um plano de treino ou nutrição com a meta calórica já calculada. */
    public Plano gerarPlano(int clienteId, String tipoPlano, Objetivo objetivo)
            throws ValidacaoException, ArquivoNaoEncontradoException {
        Cliente cliente = buscarPorId(clienteId)
                .orElseThrow(() -> new ValidacaoException("Cliente não encontrado."));
        Plano plano = "PlanoNutricao".equalsIgnoreCase(tipoPlano)
                ? new PlanoNutricao(objetivo)
                : new PlanoTreino(objetivo);
        cliente.setPlano(plano);
        repository.salvarTodos(clientes);
        return plano;
    }

    /** RF06 — indica se o plano do cliente já deve ser revisado hoje, conforme o tipo de assinatura. */
    public boolean precisaRevisaoPlano(Cliente cliente, LocalDate hoje) {
        if (cliente.getAssinatura() == null || cliente.getPlano() == null) {
            return false;
        }
        long diasDesdeInicio = ChronoUnit.DAYS.between(cliente.getAssinatura().getDataInicio(), hoje);
        int intervalo = cliente.getAssinatura().intervaloRevisaoDias();
        return diasDesdeInicio > 0 && diasDesdeInicio % intervalo == 0;
    }

    /**
     * RF09/RF10 — feedback automático de progresso comparando a
     * primeira e a última avaliação registradas. Precisa de pelo
     * menos duas avaliações para ter o que comparar.
     */
    public String gerarFeedbackProgresso(Cliente cliente) {
        List<AvaliacaoFisica> avaliacoes = cliente.getAvaliacoes();
        if (avaliacoes.size() < 2 || cliente.getPlano() == null) {
            return "Sem dados suficientes para avaliar o progresso.";
        }
        AvaliacaoFisica primeira = avaliacoes.get(0);
        AvaliacaoFisica ultima = avaliacoes.get(avaliacoes.size() - 1);
        double variacaoPercentual = (ultima.getPeso() - primeira.getPeso()) / primeira.getPeso() * 100.0;

        Objetivo objetivo = cliente.getPlano().getObjetivo();
        return switch (objetivo) {
            case EMAGRECIMENTO -> (-variacaoPercentual >= 1.0) ? "Dentro da meta" : "Ajuste recomendado";
            case GANHO_DE_MASSA -> (variacaoPercentual >= 0.5) ? "Dentro da meta" : "Ajuste recomendado";
            case REEDUCACAO_ALIMENTAR -> "Sem critério de peso definido para reeducação alimentar.";
        };
    }
}
