package com.vidasaudavel;

import com.vidasaudavel.exception.ArquivoNaoEncontradoException;
import com.vidasaudavel.model.Nutricionista;
import com.vidasaudavel.model.PersonalTrainer;
import com.vidasaudavel.model.Profissional;
import com.vidasaudavel.persistence.*;
import com.vidasaudavel.service.ClienteService;
import com.vidasaudavel.service.ConsultaService;
import com.vidasaudavel.service.PagamentoService;
import com.vidasaudavel.service.RelatorioService;
import com.vidasaudavel.ui.MainFrame;

import javax.swing.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // segue com o look and feel padrão caso o do sistema não esteja disponível
        }

        try {
            ClienteRepository clienteRepository = new ClienteRepository();
            ConsultaRepository consultaRepository = new ConsultaRepository();
            PagamentoRepository pagamentoRepository = new PagamentoRepository();
            ProfissionalRepository profissionalRepository = new ProfissionalRepository();

            ClienteService clienteService = new ClienteService(clienteRepository);
            ConsultaService consultaService = new ConsultaService(consultaRepository);
            PagamentoService pagamentoService = new PagamentoService(pagamentoRepository);
            RelatorioService relatorioService = new RelatorioService();

            seedProfissionaisSeNecessario(profissionalRepository);

            MainFrame frame = new MainFrame(clienteService, consultaService, pagamentoService,
                    relatorioService, profissionalRepository);
            frame.setVisible(true);

        } catch (ArquivoNaoEncontradoException e) {
            // RNF03 — arquivo de dados corrompido: avisa o usuário em vez de derrubar o sistema
            JOptionPane.showMessageDialog(null,
                    "Não foi possível carregar os dados salvos:\n" + e.getMessage()
                            + "\n\nVerifique os arquivos na pasta 'data/'.",
                    "Erro ao iniciar", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Primeira execução: cadastra dois profissionais padrão para a Agenda não começar vazia. */
    private static void seedProfissionaisSeNecessario(ProfissionalRepository repository)
            throws ArquivoNaoEncontradoException {
        List<Profissional> profissionais = repository.carregarTodos();
        if (profissionais.isEmpty()) {
            profissionais.add(new PersonalTrainer(1, "Carla Mendes", "111.111.111-11",
                    "(86) 99999-1111", "carla@vidasaudavel.com", "Funcional"));
            profissionais.add(new Nutricionista(2, "Rafael Souza", "222.222.222-22",
                    "(86) 99999-2222", "rafael@vidasaudavel.com", "CRN-12345"));
            repository.salvarTodos(profissionais);
        }
    }
}
