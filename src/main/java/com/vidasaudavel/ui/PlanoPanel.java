package com.vidasaudavel.ui;

import com.vidasaudavel.exception.ArquivoNaoEncontradoException;
import com.vidasaudavel.exception.ValidacaoException;
import com.vidasaudavel.model.Cliente;
import com.vidasaudavel.model.Objetivo;
import com.vidasaudavel.service.ClienteService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.function.Supplier;

/** RF04/RF05/RF06 — planos de treino/nutrição e assinatura do cliente selecionado. */
public class PlanoPanel extends JPanel {

    private final ClienteService service;
    private final Supplier<Cliente> clienteAtual;

    private final JLabel clienteLabel = new JLabel("-");
    private final JLabel assinaturaLabel = new JLabel("-");
    private final JComboBox<Objetivo> objetivoCombo = new JComboBox<>(Objetivo.values());
    private final JComboBox<String> tipoPlanoCombo = new JComboBox<>(new String[]{"PlanoTreino", "PlanoNutricao"});
    private final JComboBox<String> tipoAssinaturaCombo = new JComboBox<>(new String[]{"Básico", "VIP"});
    private final JLabel metaCaloricaLabel = new JLabel("-");
    private final JTextArea progressoArea = new JTextArea(4, 30);

    public PlanoPanel(ClienteService service, Supplier<Cliente> clienteAtual) {
        this.service = service;
        this.clienteAtual = clienteAtual;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topo = new JPanel(new GridLayout(0, 2, 6, 4));
        topo.add(new JLabel("Cliente:"));
        topo.add(clienteLabel);
        topo.add(new JLabel("Assinatura atual:"));
        topo.add(assinaturaLabel);
        topo.add(new JLabel("Nova assinatura:"));
        topo.add(linha(tipoAssinaturaCombo, botao("Definir Assinatura", e -> definirAssinatura())));
        topo.add(new JLabel("Objetivo:"));
        topo.add(objetivoCombo);
        topo.add(new JLabel("Tipo de plano:"));
        topo.add(tipoPlanoCombo);
        topo.add(new JLabel("Meta calórica estimada:"));
        topo.add(metaCaloricaLabel);

        JButton gerarPlanoBtn = new JButton("Gerar Plano");
        gerarPlanoBtn.addActionListener(e -> gerarPlano());
        JButton revisarBtn = new JButton("Revisar Plano / Ver Progresso");
        revisarBtn.addActionListener(e -> revisarPlano());

        objetivoCombo.addActionListener(e -> atualizarMetaEstimativa());
        tipoPlanoCombo.addActionListener(e -> atualizarMetaEstimativa());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(gerarPlanoBtn);
        botoes.add(revisarBtn);

        progressoArea.setEditable(false);
        progressoArea.setLineWrap(true);

        add(new JLabel("Plano de Treino / Nutrição"), BorderLayout.NORTH);
        add(topo, BorderLayout.CENTER);
        JPanel sul = new JPanel(new BorderLayout());
        sul.add(botoes, BorderLayout.NORTH);
        sul.add(new JScrollPane(progressoArea), BorderLayout.CENTER);
        add(sul, BorderLayout.SOUTH);
    }

    private JComponent linha(JComponent... componentes) {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        for (JComponent c : componentes) {
            painel.add(c);
        }
        return painel;
    }

    private JButton botao(String rotulo, java.awt.event.ActionListener acao) {
        JButton b = new JButton(rotulo);
        b.addActionListener(acao);
        return b;
    }

    /** Chamado pelo MainFrame sempre que este painel é exibido, para refletir o cliente selecionado. */
    public void atualizar() {
        Cliente cliente = clienteAtual.get();
        if (cliente == null) {
            clienteLabel.setText("Nenhum cliente selecionado");
            assinaturaLabel.setText("-");
            metaCaloricaLabel.setText("-");
            progressoArea.setText("");
            return;
        }
        clienteLabel.setText(cliente.getNome());
        assinaturaLabel.setText(cliente.getAssinatura() != null ? cliente.getAssinatura().getTipo() : "Nenhuma");
        atualizarMetaEstimativa();
    }

    private void atualizarMetaEstimativa() {
        Cliente cliente = clienteAtual.get();
        if (cliente == null) {
            return;
        }
        Objetivo objetivo = (Objetivo) objetivoCombo.getSelectedItem();
        double gasto = cliente.getPeso() * 30.0;
        double ajuste = switch (objetivo) {
            case EMAGRECIMENTO -> -500.0;
            case GANHO_DE_MASSA -> 300.0;
            case REEDUCACAO_ALIMENTAR -> 0.0;
        };
        metaCaloricaLabel.setText(String.format("%.0f kcal", gasto + ajuste));
    }

    private void definirAssinatura() {
        Cliente cliente = clienteAtual.get();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tela de Clientes.");
            return;
        }
        try {
            String tipo = (String) tipoAssinaturaCombo.getSelectedItem();
            service.definirAssinatura(cliente.getId(), "VIP".equals(tipo) ? "VIP" : "Basico", LocalDate.now());
            assinaturaLabel.setText(cliente.getAssinatura().getTipo());
            JOptionPane.showMessageDialog(this, "Assinatura definida com sucesso.");
        } catch (ValidacaoException | ArquivoNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarPlano() {
        Cliente cliente = clienteAtual.get();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tela de Clientes.");
            return;
        }
        try {
            Objetivo objetivo = (Objetivo) objetivoCombo.getSelectedItem();
            String tipoPlano = (String) tipoPlanoCombo.getSelectedItem();
            service.gerarPlano(cliente.getId(), tipoPlano, objetivo);
            JOptionPane.showMessageDialog(this, "Plano gerado. Meta calórica: " + metaCaloricaLabel.getText());
        } catch (ValidacaoException | ArquivoNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void revisarPlano() {
        Cliente cliente = clienteAtual.get();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tela de Clientes.");
            return;
        }
        boolean precisaRevisao = service.precisaRevisaoPlano(cliente, LocalDate.now());
        String feedback = service.gerarFeedbackProgresso(cliente);
        progressoArea.setText(
                "Revisão hoje: " + (precisaRevisao ? "sim" : "não") + "\n" +
                "Feedback de progresso: " + feedback);
    }
}
