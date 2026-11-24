package com.vidasaudavel.ui;

import com.vidasaudavel.exception.ArquivoNaoEncontradoException;
import com.vidasaudavel.exception.ValidacaoException;
import com.vidasaudavel.model.AvaliacaoFisica;
import com.vidasaudavel.model.Cliente;
import com.vidasaudavel.service.ClienteService;
import com.vidasaudavel.util.DataUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.function.Consumer;

/** RF01/RF03 — cadastro de clientes e histórico de avaliações físicas. */
public class ClienteFormPanel extends JPanel {

    private final ClienteService service;
    private final Consumer<Cliente> onSelecionar;

    private final JTextField nomeField = new JTextField();
    private final JTextField cpfField = new JTextField();
    private final JTextField telefoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField pesoField = new JTextField();
    private final JTextField alturaField = new JTextField();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "IMC", "Classificação"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable tabela = new JTable(tableModel);

    private Integer idSelecionado; // null = novo cliente

    public ClienteFormPanel(ClienteService service, Consumer<Cliente> onSelecionar) {
        this.service = service;
        this.onSelecionar = onSelecionar;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(new JLabel("Cadastro de Clientes"), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(criarFormulario(), BorderLayout.SOUTH);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecao();
            }
        });

        atualizarTabela();
    }

    private JComponent criarFormulario() {
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 4));
        form.add(new JLabel("Nome:"));
        form.add(nomeField);
        form.add(new JLabel("CPF:"));
        form.add(cpfField);
        form.add(new JLabel("Telefone:"));
        form.add(telefoneField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Peso (kg):"));
        form.add(pesoField);
        form.add(new JLabel("Altura (m):"));
        form.add(alturaField);

        JButton novoBtn = new JButton("Novo");
        JButton salvarBtn = new JButton("Salvar");
        JButton removerBtn = new JButton("Remover");
        JButton avaliacaoBtn = new JButton("Nova Avaliação");

        novoBtn.addActionListener(e -> limparFormulario());
        salvarBtn.addActionListener(e -> salvar());
        removerBtn.addActionListener(e -> remover());
        avaliacaoBtn.addActionListener(e -> novaAvaliacao());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(novoBtn);
        botoes.add(salvarBtn);
        botoes.add(removerBtn);
        botoes.add(avaliacaoBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(botoes, BorderLayout.SOUTH);
        return wrapper;
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        for (Cliente c : service.listarClientes()) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getNome(), c.getCpf(),
                    String.format("%.1f", c.calcularImc()), c.classificarImc()
            });
        }
    }

    private void carregarSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        int id = (int) tableModel.getValueAt(linha, 0);
        service.buscarPorId(id).ifPresent(cliente -> {
            idSelecionado = cliente.getId();
            nomeField.setText(cliente.getNome());
            cpfField.setText(cliente.getCpf());
            telefoneField.setText(cliente.getTelefone());
            emailField.setText(cliente.getEmail());
            pesoField.setText(String.valueOf(cliente.getPeso()));
            alturaField.setText(String.valueOf(cliente.getAltura()));
            onSelecionar.accept(cliente);
        });
    }

    private void limparFormulario() {
        idSelecionado = null;
        nomeField.setText("");
        cpfField.setText("");
        telefoneField.setText("");
        emailField.setText("");
        pesoField.setText("");
        alturaField.setText("");
        tabela.clearSelection();
        onSelecionar.accept(null);
    }

    private void salvar() {
        try {
            double peso = parseDouble(pesoField.getText(), "Peso");
            double altura = parseDouble(alturaField.getText(), "Altura");

            if (idSelecionado == null) {
                Cliente novo = service.cadastrarCliente(nomeField.getText(), cpfField.getText(),
                        telefoneField.getText(), emailField.getText(), peso, altura);
                idSelecionado = novo.getId();
            } else {
                Cliente cliente = service.buscarPorId(idSelecionado)
                        .orElseThrow(() -> new ValidacaoException("Cliente não encontrado."));
                cliente.setNome(nomeField.getText());
                cliente.setCpf(cpfField.getText());
                cliente.setTelefone(telefoneField.getText());
                cliente.setEmail(emailField.getText());
                cliente.setPeso(peso);
                cliente.setAltura(altura);
                service.salvar();
            }
            atualizarTabela();
            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso.");
        } catch (ValidacaoException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (ArquivoNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void remover() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.");
            return;
        }
        try {
            service.removerCliente(idSelecionado);
            limparFormulario();
            atualizarTabela();
        } catch (ArquivoNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro ao remover", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void novaAvaliacao() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.");
            return;
        }
        JTextField pesoAval = new JTextField();
        JTextField alturaAval = new JTextField();
        JTextField dataAval = new JTextField(DataUtil.formatarData(LocalDate.now()));
        JPanel painel = new JPanel(new GridLayout(0, 2, 6, 4));
        painel.add(new JLabel("Data (dd/MM/yyyy):"));
        painel.add(dataAval);
        painel.add(new JLabel("Peso (kg):"));
        painel.add(pesoAval);
        painel.add(new JLabel("Altura (m):"));
        painel.add(alturaAval);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Nova Avaliação Física",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            if (!DataUtil.dataValida(dataAval.getText())) {
                throw new ValidacaoException("Data inválida. Use o formato dd/MM/yyyy.");
            }
            LocalDate data = DataUtil.parseData(dataAval.getText());
            double peso = parseDouble(pesoAval.getText(), "Peso");
            double altura = parseDouble(alturaAval.getText(), "Altura");

            AvaliacaoFisica avaliacao = service.registrarAvaliacao(idSelecionado, data, peso, altura);
            atualizarTabela();
            JOptionPane.showMessageDialog(this, "Avaliação registrada:\n" + avaliacao);
        } catch (ValidacaoException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (ArquivoNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseDouble(String texto, String campo) throws ValidacaoException {
        try {
            double valor = Double.parseDouble(texto.trim().replace(",", "."));
            if (valor <= 0) {
                throw new ValidacaoException(campo + " deve ser maior que zero.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new ValidacaoException(campo + " inválido.");
        }
    }
}
