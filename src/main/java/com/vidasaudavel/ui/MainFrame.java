package com.vidasaudavel.ui;

import com.vidasaudavel.model.Cliente;
import com.vidasaudavel.persistence.ProfissionalRepository;
import com.vidasaudavel.service.ClienteService;
import com.vidasaudavel.service.ConsultaService;
import com.vidasaudavel.service.PagamentoService;
import com.vidasaudavel.service.RelatorioService;

import javax.swing.*;
import java.awt.*;

/**
 * Janela principal: menu de navegação à esquerda e um CardLayout que
 * troca entre os quatro painéis (RF documentados no protótipo de
 * interface). Mantém o cliente selecionado como contexto compartilhado
 * entre os painéis.
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel conteudo = new JPanel(cardLayout);
    private final JLabel statusClienteLabel = new JLabel("Nenhum cliente selecionado");

    private Cliente clienteAtual;
    private final ClienteFormPanel clienteFormPanel;
    private final PlanoPanel planoPanel;
    private final AgendaPanel agendaPanel;
    private final RelatorioPanel relatorioPanel;

    public MainFrame(ClienteService clienteService, ConsultaService consultaService,
                      PagamentoService pagamentoService, RelatorioService relatorioService,
                      ProfissionalRepository profissionalRepository) {
        super("Vida Saudável — Gestão de Clientes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 620);
        setLocationRelativeTo(null);

        clienteFormPanel = new ClienteFormPanel(clienteService, this::selecionarCliente);
        planoPanel = new PlanoPanel(clienteService, this::getClienteAtual);
        agendaPanel = new AgendaPanel(consultaService, profissionalRepository, this::getClienteAtual);
        relatorioPanel = new RelatorioPanel(pagamentoService, consultaService, relatorioService, this::getClienteAtual);

        conteudo.add(clienteFormPanel, "cliente");
        conteudo.add(planoPanel, "plano");
        conteudo.add(agendaPanel, "agenda");
        conteudo.add(relatorioPanel, "relatorio");

        setLayout(new BorderLayout());
        add(criarMenuLateral(), BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);
        add(criarBarraStatus(), BorderLayout.SOUTH);
    }

    private JComponent criarMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        menu.setPreferredSize(new Dimension(190, 0));

        JLabel titulo = new JLabel("Vida Saudável");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(titulo);
        menu.add(Box.createVerticalStrut(16));

        menu.add(criarBotaoMenu("Clientes", "cliente"));
        menu.add(criarBotaoMenu("Plano", "plano"));
        menu.add(criarBotaoMenu("Agenda", "agenda"));
        menu.add(criarBotaoMenu("Relatórios", "relatorio"));

        menu.add(Box.createVerticalGlue());
        return menu;
    }

    private JButton criarBotaoMenu(String rotulo, String card) {
        JButton botao = new JButton(rotulo);
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        botao.addActionListener(e -> mostrarCard(card));
        return botao;
    }

    private JComponent criarBarraStatus() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
        status.add(new JLabel("Cliente selecionado: "));
        status.add(statusClienteLabel);
        return status;
    }

    private void mostrarCard(String card) {
        cardLayout.show(conteudo, card);
        if (card.equals("plano")) {
            planoPanel.atualizar();
        } else if (card.equals("agenda")) {
            agendaPanel.atualizar();
        } else if (card.equals("relatorio")) {
            relatorioPanel.atualizar();
        }
    }

    private void selecionarCliente(Cliente cliente) {
        this.clienteAtual = cliente;
        statusClienteLabel.setText(cliente == null ? "Nenhum cliente selecionado" : cliente.getNome());
    }

    public Cliente getClienteAtual() {
        return clienteAtual;
    }
}
