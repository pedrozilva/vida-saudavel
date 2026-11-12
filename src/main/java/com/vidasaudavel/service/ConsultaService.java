package com.vidasaudavel.service;

import com.vidasaudavel.exception.ArquivoNaoEncontradoException;
import com.vidasaudavel.exception.BloqueioFinanceiroException;
import com.vidasaudavel.exception.ConflitoHorarioException;
import com.vidasaudavel.exception.ValidacaoException;
import com.vidasaudavel.model.Cliente;
import com.vidasaudavel.model.Consulta;
import com.vidasaudavel.persistence.ConsultaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/** Regras de agendamento de consultas (RF07-RF09, RF12). */
public class ConsultaService {

    private final ConsultaRepository repository;
    private List<Consulta> consultas;

    public ConsultaService(ConsultaRepository repository) throws ArquivoNaoEncontradoException {
        this.repository = repository;
        this.consultas = repository.carregarTodos();
    }

    public List<Consulta> listarConsultas() {
        return consultas;
    }

    public List<Consulta> listarPorCliente(int clienteId) {
        return consultas.stream().filter(c -> c.getClienteId() == clienteId).collect(Collectors.toList());
    }

    /**
     * RF07/RF08/RF12 — agenda uma consulta: bloqueia conflito de
     * horário do profissional, bloqueia clientes com 2+ mensalidades
     * atrasadas e calcula se a consulta é extra (cobrada à parte).
     */
    public Consulta agendarConsulta(Cliente cliente, int profissionalId, LocalDateTime dataHora)
            throws ConflitoHorarioException, BloqueioFinanceiroException, ValidacaoException, ArquivoNaoEncontradoException {

        if (cliente.getAssinatura() == null) {
            throw new ValidacaoException("Cliente não possui assinatura ativa.");
        }
        if (!cliente.getAssinatura().podeAgendarConsulta()) {
            throw new BloqueioFinanceiroException(
                    "Cliente com 2 ou mais mensalidades atrasadas não pode agendar consultas.");
        }

        for (Consulta existente : consultas) {
            if (existente.getProfissionalId() == profissionalId && existente.getDataHora().equals(dataHora)) {
                throw new ConflitoHorarioException("Profissional já tem consulta marcada neste horário.");
            }
        }

        boolean extra = contarConsultasNoPeriodoAtual(cliente) >= cliente.getAssinatura().consultasInclusas();
        double valor = extra ? cliente.getAssinatura().valorConsultaExtra() : 0.0;

        int novoId = consultas.stream().mapToInt(Consulta::getId).max().orElse(0) + 1;
        Consulta consulta = new Consulta(novoId, cliente.getId(), profissionalId, dataHora, extra, valor);
        consultas.add(consulta);
        repository.salvarTodos(consultas);
        return consulta;
    }

    public void cancelarConsulta(int consultaId) throws ArquivoNaoEncontradoException {
        consultas.removeIf(c -> c.getId() == consultaId);
        repository.salvarTodos(consultas);
    }

    /** Conta quantas consultas o cliente já tem dentro do intervalo de revisão atual da assinatura. */
    private long contarConsultasNoPeriodoAtual(Cliente cliente) {
        int intervaloDias = cliente.getAssinatura().intervaloRevisaoDias();
        LocalDateTime limite = LocalDateTime.now().minusDays(intervaloDias);
        return consultas.stream()
                .filter(c -> c.getClienteId() == cliente.getId())
                .filter(c -> !c.isExtra())
                .filter(c -> c.getDataHora().isAfter(limite))
                .count();
    }
}
