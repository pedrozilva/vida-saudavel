package com.vidasaudavel.ui;

import com.vidasaudavel.exception.ArquivoNaoEncontradoException;
import com.vidasaudavel.exception.ValidacaoException;
import com.vidasaudavel.model.Cliente;
import com.vidasaudavel.model.Pagamento;
import com.vidasaudavel.service.ConsultaService;
import com.vidasaudavel.service.PagamentoService;
import com.vidasaudavel.service.RelatorioFinanceiro;
import com.vidasaudavel.service.RelatorioService;
import com.vidasaudavel.util.DataUtil;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

/** RF11/RF13/RF14 — pagamentos do cliente e relatório financeiro geral. */
public class RelatorioPanel extends JPanel {

    private final PagamentoService pagamentoService;
    private final ConsultaService consultaService;
    private final RelatorioService relatorioService;
    private final java.util.function.Supplier<Cliente> clienteAtual;

    private final JLabel clienteLabel = new JLabel("-");
    private final JTextArea resumoClienteArea = new JTextArea(6, 40);
    private final JTextArea relatorioFinanceiroArea = new JTextArea(6, 40);

    public RelatorioPanel(PagamentoService pagamentoService, ConsultaService consultaService,
                           RelatorioService relatorioService, java.util.function.Supplier<Cliente> clienteAtual) {
        this.pagamentoService = pagamentoService;
        this.consultaService = consultaService;
        this.relatorioService = relatorioService;
        this.clienteAtual = clienteAtual;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topo = new JPanel(new GridLayout(0, 2, 6, 4));
        topo.add(new JLabel("Cliente:"));
        topo.add(clienteLabel);

        JButton gerarMensalidadeBtn = new JButton("Gerar Mensalidade (hoje +30d)");
        gerarMensalidadeBtn.addActionListener(e -> gerarMensalidade());
        JButton pagarBtn = new JButton("Registrar Pagamento Pendente (hoje)");
        pagarBtn.addActionListener(e -> registrarPagamento());
        JButton relatorioBtn = new JButton("Gerar Relatório Financeiro");
        relatorioBtn.addActionListener(e -> gerarRelatorioFinanceiro());
        JButton exportarBtn = new JButton("Exportar (.txt)");
        exportarBtn.addActionListener(e -> exportar());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(gerarMensalidadeBtn);
        botoes.add(pagarBtn);
        botoes.add(relatorioBtn);
        botoes.add(exportarBtn);

        resumoClienteArea.setEditable(false);
        resumoClienteArea.setLineWrap(true);
        relatorioFinanceiroArea.setEditable(false);
        relatorioFinanceiroArea.setLineWrap(true);

        JPanel areas = new JPanel(new GridLayout(1, 2, 12, 0));
        areas.add(criarAreaComTitulo("Resumo do Cliente (RF14)", resumoClienteArea));
        areas.add(criarAreaComTitulo("Relatório Financeiro (RF13)", relatorioFinanceiroArea));

        add(new JLabel("Relatórios e Pagamentos"), BorderLayout.NORTH);
        JPanel centro = new JPanel(new BorderLayout(6, 6));
        centro.add(topo, BorderLayout.NORTH);
        centro.add(botoes, BorderLayout.CENTER);
        add(centro, BorderLayout.PAGE_START);
        add(areas, BorderLayout.CENTER);
    }

    private JComponent criarAreaComTitulo(String titulo, JTextArea area) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(new JLabel(titulo), BorderLayout.NORTH);
        painel.add(new JScrollPane(area), BorderLayout.CENTER);
        return painel;
    }

    /** Chamado pelo MainFrame sempre que este painel é exibido. */
    public void atualizar() {
        Cliente cliente = clienteAtual.get();
        clienteLabel.setText(cliente == null ? "Nenhum cliente selecionado" : cliente.getNome());
        if (cliente != null) {
            atualizarResumoCliente(cliente);
        } else {
            resumoClienteArea.setText("");
        }
    }

    private void atualizarResumoCliente(Cliente cliente) {
        resumoClienteArea.setText(relatorioService.gerarResumoCliente(cliente, feedbackOuPlaceholder(cliente)));
    }

    private String feedbackOuPlaceholder(Cliente cliente) {
        return cliente.getAvaliacoes().size() >= 2 ? "(ver aba Plano para o feedback detalhado)" : "sem avaliações suficientes";
    }

    private void gerarMensalidade() {
        Cliente cliente = clienteAtual.get();
        if (cliente == null || cliente.getAssinatura() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente com assinatura ativa.");
            return;
        }
        try {
            Pagamento pagamento = pagamentoService.gerarMensalidade(cliente, LocalDate.now().plusDays(30));
            JOptionPane.showMessageDialog(this, "Mensalidade gerada: " + DataUtil.formatarMoeda(pagamento.getValorMensalidade())
                    + " — vencimento " + DataUtil.formatarData(pagamento.getDataVencimento()));
        } catch (ArquivoNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarPagamento() {
        Cliente cliente = clienteAtual.get();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tela de Clientes.");
            return;
        }
        var pendentes = pagamentoService.listarPorCliente(cliente.getId()).stream()
                .filter(p -> !p.isPago()).toList();
        if (pendentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma mensalidade pendente para este cliente.");
            return;
        }
        try {
            pagamentoService.registrarPagamento(cliente, pendentes.get(0).getId(), LocalDate.now());
            JOptionPane.showMessageDialog(this, "Pagamento registrado.");
            atualizarResumoCliente(cliente);
        } catch (ValidacaoException | ArquivoNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarRelatorioFinanceiro() {
        RelatorioFinanceiro relatorio = relatorioService.gerarRelatorioFinanceiro(
                pagamentoService.listarTodos(), consultaService.listarConsultas(), LocalDate.now());

        relatorioFinanceiroArea.setText(
                "Receita de mensalidades: " + DataUtil.formatarMoeda(relatorio.receitaMensalidades()) + "\n" +
                "Receita de multas: " + DataUtil.formatarMoeda(relatorio.receitaMultas()) + "\n" +
                "Receita de consultas extras: " + DataUtil.formatarMoeda(relatorio.receitaConsultasExtras()) + "\n" +
                "Receita total: " + DataUtil.formatarMoeda(relatorio.receitaTotal()) + "\n" +
                "Pagamentos em atraso: " + relatorio.pagamentosEmAtraso());
    }

    private void exportar() {
        if (relatorioFinanceiroArea.getText().isBlank()) {
            gerarRelatorioFinanceiro();
        }
        try (FileWriter writer = new FileWriter("data/relatorio_financeiro.txt")) {
            writer.write("Relatório Financeiro — Vida Saudável\n");
            writer.write("Gerado em " + DataUtil.formatarData(LocalDate.now()) + "\n\n");
            writer.write(relatorioFinanceiroArea.getText());
            JOptionPane.showMessageDialog(this, "Relatório exportado para data/relatorio_financeiro.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível exportar: " + e.getMessage(),
                    "Erro ao exportar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
