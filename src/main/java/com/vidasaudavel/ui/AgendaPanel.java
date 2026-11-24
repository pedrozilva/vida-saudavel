package com.vidasaudavel.ui;

import com.vidasaudavel.exception.*;
import com.vidasaudavel.model.Cliente;
import com.vidasaudavel.model.Consulta;
import com.vidasaudavel.model.Profissional;
import com.vidasaudavel.persistence.ProfissionalRepository;
import com.vidasaudavel.service.ConsultaService;
import com.vidasaudavel.util.DataUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Supplier;

/** RF07/RF08/RF12 — agendamento de consultas, sem conflito de horário. */
public class AgendaPanel extends JPanel {

    private final ConsultaService consultaService;
    private final ProfissionalRepository profissionalRepository;
    private final Supplier<Cliente> clienteAtual;

    private final JLabel clienteLabel = new JLabel("-");
    private final JComboBox<Profissional> profissionalCombo = new JComboBox<>();
    private final JTextField dataHoraField = new JTextField("dd/MM/yyyy HH:mm");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"ID", "Data/Hora", "Profissional", "Extra", "Valor"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable tabela = new JTable(tableModel);

    public AgendaPanel(ConsultaService consultaService, ProfissionalRepository profissionalRepository,
                        Supplier<Cliente> clienteAtual) {
        this.consultaService = consultaService;
        this.profissionalRepository = profissionalRepository;
        this.clienteAtual = clienteAtual;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topo = new JPanel(new GridLayout(0, 2, 6, 4));
        topo.add(new JLabel("Cliente:"));
        topo.add(clienteLabel);
        topo.add(new JLabel("Profissional:"));
        topo.add(profissionalCombo);
        topo.add(new JLabel("Data/Hora (dd/MM/yyyy HH:mm):"));
        topo.add(dataHoraField);

        JButton agendarBtn = new JButton("Agendar Consulta");
        agendarBtn.addActionListener(e -> agendar());
        JButton cancelarBtn = new JButton("Cancelar Selecionada");
        cancelarBtn.addActionListener(e -> cancelar());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(agendarBtn);
        botoes.add(cancelarBtn);

        add(new JLabel("Agenda de Consultas"), BorderLayout.NORTH);
        JPanel centro = new JPanel(new BorderLayout(6, 6));
        centro.add(topo, BorderLayout.NORTH);
        centro.add(botoes, BorderLayout.CENTER);
        add(centro, BorderLayout.PAGE_START);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        carregarProfissionais();
    }

    private void carregarProfissionais() {
        try {
            List<Profissional> profissionais = profissionalRepository.carregarTodos();
            profissionalCombo.removeAllItems();
            for (Profissional p : profissionais) {
                profissionalCombo.addItem(p);
            }
        } catch (ArquivoNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro ao carregar profissionais",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Chamado pelo MainFrame sempre que este painel é exibido. */
    public void atualizar() {
        Cliente cliente = clienteAtual.get();
        clienteLabel.setText(cliente == null ? "Nenhum cliente selecionado" : cliente.getNome());
        atualizarTabela();
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        Cliente cliente = clienteAtual.get();
        if (cliente == null) {
            return;
        }
        for (Consulta c : consultaService.listarPorCliente(cliente.getId())) {
            tableModel.addRow(new Object[]{
                    c.getId(), DataUtil.formatarDataHora(c.getDataHora()),
                    nomeProfissional(c.getProfissionalId()),
                    c.isExtra() ? "Sim" : "Não",
                    DataUtil.formatarMoeda(c.getValor())
            });
        }
    }

    private String nomeProfissional(int id) {
        for (int i = 0; i < profissionalCombo.getItemCount(); i++) {
            Profissional p = profissionalCombo.getItemAt(i);
            if (p.getId() == id) {
                return p.getNome();
            }
        }
        return "#" + id;
    }

    private void agendar() {
        Cliente cliente = clienteAtual.get();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tela de Clientes.");
            return;
        }
        Profissional profissional = (Profissional) profissionalCombo.getSelectedItem();
        if (profissional == null) {
            JOptionPane.showMessageDialog(this, "Cadastre ao menos um profissional.");
            return;
        }
        try {
            LocalDateTime dataHora = java.time.LocalDateTime.parse(
                    dataHoraField.getText().trim(), DataUtil.FORMATO_DATA_HORA);

            consultaService.agendarConsulta(cliente, profissional.getId(), dataHora);
            atualizarTabela();
            JOptionPane.showMessageDialog(this, "Consulta agendada com sucesso.");

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data/hora inválida. Use dd/MM/yyyy HH:mm.",
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (ConflitoHorarioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Conflito de horário", JOptionPane.WARNING_MESSAGE);
        } catch (BloqueioFinanceiroException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Bloqueio financeiro", JOptionPane.WARNING_MESSAGE);
        } catch (ValidacaoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (ArquivoNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta na tabela.");
            return;
        }
        int id = (int) tableModel.getValueAt(linha, 0);
        try {
            consultaService.cancelarConsulta(id);
            atualizarTabela();
        } catch (ArquivoNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro ao cancelar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
