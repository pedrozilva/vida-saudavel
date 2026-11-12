package com.vidasaudavel.service;

import com.vidasaudavel.exception.ArquivoNaoEncontradoException;
import com.vidasaudavel.exception.ValidacaoException;
import com.vidasaudavel.model.Cliente;
import com.vidasaudavel.model.Pagamento;
import com.vidasaudavel.persistence.PagamentoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/** Regras de cobrança e inadimplência (RF11/RF12). */
public class PagamentoService {

    private final PagamentoRepository repository;
    private List<Pagamento> pagamentos;

    public PagamentoService(PagamentoRepository repository) throws ArquivoNaoEncontradoException {
        this.repository = repository;
        this.pagamentos = repository.carregarTodos();
    }

    public List<Pagamento> listarPorCliente(int clienteId) {
        return pagamentos.stream().filter(p -> p.getClienteId() == clienteId).collect(Collectors.toList());
    }

    public List<Pagamento> listarTodos() {
        return pagamentos;
    }

    /** Gera a cobrança da mensalidade do mês para o cliente. */
    public Pagamento gerarMensalidade(Cliente cliente, LocalDate vencimento) throws ArquivoNaoEncontradoException {
        int novoId = pagamentos.stream().mapToInt(Pagamento::getId).max().orElse(0) + 1;
        Pagamento pagamento = new Pagamento(novoId, cliente.getId(), cliente.getAssinatura().getValor(), vencimento);
        pagamentos.add(pagamento);
        repository.salvarTodos(pagamentos);
        return pagamento;
    }

    /** RF11 — registra o pagamento, aplicando multa de 2% se estiver atrasado. */
    public void registrarPagamento(Cliente cliente, int pagamentoId, LocalDate dataPagamento)
            throws ValidacaoException, ArquivoNaoEncontradoException {
        Pagamento pagamento = pagamentos.stream()
                .filter(p -> p.getId() == pagamentoId)
                .findFirst()
                .orElseThrow(() -> new ValidacaoException("Pagamento não encontrado."));

        double multa = pagamento.estaAtrasado(dataPagamento) ? pagamento.getValorMensalidade() * 0.02 : 0.0;
        pagamento.registrarPagamento(dataPagamento, multa);

        if (multa > 0) {
            cliente.getAssinatura().registrarAtraso();
        } else {
            cliente.getAssinatura().registrarPagamentoEmDia();
        }
        repository.salvarTodos(pagamentos);
    }

    /** RF12 — atualiza o contador de mensalidades atrasadas do cliente conforme os pagamentos pendentes. */
    public void atualizarInadimplencia(Cliente cliente, LocalDate hoje) {
        long atrasados = listarPorCliente(cliente.getId()).stream()
                .filter(p -> p.estaAtrasado(hoje))
                .count();
        if (cliente.getAssinatura() == null) {
            return;
        }
        if (atrasados == 0) {
            cliente.getAssinatura().registrarPagamentoEmDia();
        } else {
            for (long i = cliente.getAssinatura().getMensalidadesAtrasadas(); i < atrasados; i++) {
                cliente.getAssinatura().registrarAtraso();
            }
        }
    }
}
