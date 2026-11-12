package com.vidasaudavel.service;

import com.vidasaudavel.model.Cliente;
import com.vidasaudavel.model.Consulta;
import com.vidasaudavel.model.Pagamento;

import java.time.LocalDate;
import java.util.List;

/** RF13/RF14 — geração dos relatórios financeiro e de progresso do cliente. */
public class RelatorioService {

    /** RF13 — receita por serviço: mensalidades, multas e consultas extras. */
    public RelatorioFinanceiro gerarRelatorioFinanceiro(List<Pagamento> pagamentos, List<Consulta> consultas, LocalDate hoje) {
        double receitaMensalidades = pagamentos.stream()
                .filter(Pagamento::isPago)
                .mapToDouble(Pagamento::getValorMensalidade)
                .sum();
        double receitaMultas = pagamentos.stream()
                .filter(Pagamento::isPago)
                .mapToDouble(Pagamento::getMultaAplicada)
                .sum();
        double receitaConsultasExtras = consultas.stream()
                .filter(Consulta::isExtra)
                .mapToDouble(Consulta::getValor)
                .sum();
        long emAtraso = pagamentos.stream().filter(p -> p.estaAtrasado(hoje)).count();

        return new RelatorioFinanceiro(receitaMensalidades, receitaMultas, receitaConsultasExtras, emAtraso);
    }

    /** RF14 — resumo textual do progresso de um cliente, para a tela ou exportação em PDF/console. */
    public String gerarResumoCliente(Cliente cliente, String feedbackProgresso) {
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente: ").append(cliente.getNome()).append("\n");
        sb.append("IMC atual: ").append(String.format("%.1f", cliente.calcularImc()))
                .append(" (").append(cliente.classificarImc()).append(")\n");
        sb.append("Avaliações registradas: ").append(cliente.getAvaliacoes().size()).append("\n");
        if (cliente.getPlano() != null) {
            sb.append("Plano: ").append(cliente.getPlano().getTipo())
                    .append(" — objetivo ").append(cliente.getPlano().getObjetivo())
                    .append(" — meta calórica ")
                    .append(String.format("%.0f kcal", cliente.getPlano().calcularMetaCalorica(cliente.getPeso())))
                    .append("\n");
        }
        if (cliente.getAssinatura() != null) {
            sb.append("Assinatura: ").append(cliente.getAssinatura().getTipo()).append("\n");
        }
        sb.append("Progresso: ").append(feedbackProgresso).append("\n");
        return sb.toString();
    }
}
